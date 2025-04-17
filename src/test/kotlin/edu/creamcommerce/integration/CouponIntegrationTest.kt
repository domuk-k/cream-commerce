package edu.creamcommerce.integration

import edu.creamcommerce.application.coupon.dto.command.CreateCouponTemplateCommand
import edu.creamcommerce.application.coupon.dto.command.IssueCouponCommand
import edu.creamcommerce.application.coupon.dto.command.RevokeCouponCommand
import edu.creamcommerce.application.coupon.dto.command.UseCouponCommand
import edu.creamcommerce.application.coupon.usecase.*
import edu.creamcommerce.domain.common.Money
import edu.creamcommerce.domain.coupon.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.LocalDateTime

@Transactional
class CouponIntegrationTest : BaseIntegrationTest() {
    
    @Autowired
    private lateinit var createCouponTemplateUseCase: CreateCouponTemplateUseCase
    
    @Autowired
    private lateinit var getActiveCouponTemplatesUseCase: GetActiveCouponTemplatesUseCase
    
    @Autowired
    private lateinit var changeCouponTemplateStatusUseCase: ChangeCouponTemplateStatusUseCase
    
    @Autowired
    private lateinit var issueCouponUseCase: IssueCouponUseCase
    
    @Autowired
    private lateinit var getUserCouponsUseCase: GetUserCouponsUseCase
    
    @Autowired
    private lateinit var getValidUserCouponsUseCase: GetValidUserCouponsUseCase
    
    @Autowired
    private lateinit var useCouponUseCase: UseCouponUseCase
    
    @Autowired
    private lateinit var revokeCouponUseCase: RevokeCouponUseCase
    
    @Autowired
    private lateinit var expireOutdatedCouponsUseCase: ExpireOutdatedCouponsUseCase
    
    private var userId = UserId("")
    private var templateId = CouponTemplateId("")
    
    @BeforeEach
    fun setUp() {
        // 테스트용 사용자 ID 생성
        userId = UserId("test-user-" + System.currentTimeMillis())
        
        // 테스트용 쿠폰 템플릿 생성
        val createCommand = CreateCouponTemplateCommand(
            name = "테스트 쿠폰",
            description = "테스트 쿠폰 설명",
            discountType = DiscountType.PERCENTAGE,
            discountValue = 10,
            minimumOrderAmount = Money(BigDecimal.valueOf(5000)),
            maximumDiscountAmount = Money(BigDecimal.valueOf(5000)),
            maxIssuanceCount = 100,
            maxIssuancePerUser = 1,
            validDurationHours = 24,
            startAt = LocalDateTime.now(),
            endAt = LocalDateTime.now().plusDays(7)
        )
        
        val template = createCouponTemplateUseCase(createCommand)
        templateId = template.id
    }
    
    @Nested
    @DisplayName("쿠폰 템플릿 관리 유스케이스")
    inner class CouponTemplateManagementTest {
        
        @Test
        @DisplayName("쿠폰 템플릿 생성 시 기본 정보가 정확히 저장된다")
        fun createCouponTemplate() {
            // given
            val command = CreateCouponTemplateCommand(
                name = "신규 쿠폰",
                description = "신규 쿠폰 설명",
                discountType = DiscountType.FIXED_AMOUNT,
                discountValue = 2000,
                minimumOrderAmount = Money(BigDecimal.valueOf(10000)),
                maximumDiscountAmount = null,
                maxIssuanceCount = 200,
                maxIssuancePerUser = 2,
                validDurationHours = 48,
                startAt = LocalDateTime.now(),
                endAt = LocalDateTime.now().plusDays(14)
            )
            
            // when
            val result = createCouponTemplateUseCase(command)
            
            // then
            assertThat(result).isNotNull
            assertThat(result.name).isEqualTo(command.name)
            assertThat(result.discountType).isEqualTo(command.discountType)
            assertThat(result.discountValue).isEqualTo(command.discountValue)
            assertThat(result.status).isEqualTo(CouponTemplateStatus.ACTIVE)
            assertThat(result.maxIssuanceCount).isEqualTo(command.maxIssuanceCount)
            assertThat(result.issuedCount).isEqualTo(0)
        }
        
        @Test
        @DisplayName("활성 쿠폰 템플릿만 조회된다")
        fun getActiveCouponTemplates() {
            // given
            // 추가 활성 쿠폰 템플릿 생성
            createCouponTemplateUseCase(
                CreateCouponTemplateCommand(
                    name = "활성 쿠폰",
                    description = "활성 쿠폰 설명",
                    discountType = DiscountType.PERCENTAGE,
                    discountValue = 20,
                    minimumOrderAmount = Money(BigDecimal.valueOf(10000)),
                    maximumDiscountAmount = Money(BigDecimal.valueOf(10000)),
                    maxIssuanceCount = 50,
                    maxIssuancePerUser = 1,
                    validDurationHours = 24,
                    startAt = LocalDateTime.now(),
                    endAt = LocalDateTime.now().plusDays(7)
                )
            )
            
            // 비활성 쿠폰 템플릿 생성 후 상태 변경
            val inactiveTemplate = createCouponTemplateUseCase(
                CreateCouponTemplateCommand(
                    name = "비활성 쿠폰",
                    description = "비활성 쿠폰 설명",
                    discountType = DiscountType.FIXED_AMOUNT,
                    discountValue = 1000,
                    minimumOrderAmount = Money(BigDecimal.valueOf(5000)),
                    maximumDiscountAmount = null,
                    maxIssuanceCount = 100,
                    maxIssuancePerUser = 1,
                    validDurationHours = 24,
                    startAt = LocalDateTime.now(),
                    endAt = LocalDateTime.now().plusDays(7)
                )
            )
            
            changeCouponTemplateStatusUseCase(
                inactiveTemplate.id,
                CouponTemplateStatus.ACTIVE
            )
            
            // when
            val result = getActiveCouponTemplatesUseCase()
            
            // then
            // 초기에 생성한 템플릿과 추가로 생성한 활성 템플릿만 조회되어야 함 (총 2개)
            assertThat(result).hasSize(2)
            assertThat(result.any { it.status != CouponTemplateStatus.ACTIVE }).isFalse()
        }
        
        @Test
        @DisplayName("쿠폰 템플릿 상태 변경이 정상적으로 적용된다")
        fun changeCouponTemplateStatus() {
            // given
            
            // when
            val result = changeCouponTemplateStatusUseCase(templateId, CouponTemplateStatus.SUSPENDED)
            
            // then
            assertThat(result).isNotNull
            assertThat(result.status).isEqualTo(CouponTemplateStatus.SUSPENDED)
            
            // 활성 템플릿 조회 시 나타나지 않음을 확인
            val activeTemplates = getActiveCouponTemplatesUseCase()
            assertThat(activeTemplates.any { it.id == templateId }).isFalse()
        }
    }
    
    @Nested
    @DisplayName("쿠폰 발급 유스케이스")
    inner class CouponIssuanceTest {
        
        @Test
        @DisplayName("쿠폰 발급 시 사용자에게 쿠폰이 정상적으로 발급된다")
        fun issueCoupon() {
            // given
            val command = IssueCouponCommand(
                templateId = templateId,
                userId = userId
            )
            
            // when
            val result = issueCouponUseCase(command)
            
            // then
            assertThat(result).isNotNull
            assertThat(result?.userId).isEqualTo(userId)
            assertThat(result?.templateId).isEqualTo(templateId)
            assertThat(result?.status).isEqualTo(UserCouponStatus.VALID)
            
            // 템플릿의 발급 카운트가 증가했는지 확인
            val activeTemplates = getActiveCouponTemplatesUseCase()
            val updatedTemplate = activeTemplates.find { it.id == templateId }
            assertThat(updatedTemplate?.issuedCount).isEqualTo(1)
        }
        
        @Test
        @DisplayName("사용자의 유효한 쿠폰만 조회된다")
        fun getValidUserCoupons() {
            // given
            // 유효한 쿠폰 발급
            issueCouponUseCase(
                IssueCouponCommand(
                    templateId = templateId,
                    userId = userId
                )
            )
            
            // 두 번째 템플릿 생성
            val secondTemplate = createCouponTemplateUseCase(
                CreateCouponTemplateCommand(
                    name = "두 번째 쿠폰",
                    description = "두 번째 쿠폰 설명",
                    discountType = DiscountType.FIXED_AMOUNT,
                    discountValue = 1000,
                    minimumOrderAmount = Money(BigDecimal.valueOf(5000)),
                    maximumDiscountAmount = null,
                    maxIssuanceCount = 100,
                    maxIssuancePerUser = 1,
                    validDurationHours = 24,
                    startAt = LocalDateTime.now(),
                    endAt = LocalDateTime.now().plusDays(7)
                )
            )
            
            // 두 번째 쿠폰 발급 후 사용 처리
            val secondCoupon = issueCouponUseCase(
                IssueCouponCommand(
                    templateId = secondTemplate.id,
                    userId = userId
                )
            )
            
            secondCoupon?.let {
                // 쿠폰 사용 처리
                useCouponUseCase(
                    UseCouponCommand(
                        couponId = it.id,
                        userId = userId,
                        orderId = CouponOrderId("test-order-" + System.currentTimeMillis()),
                        orderAmount = Money(BigDecimal.valueOf(5000))
                    )
                )
            }
            
            // when
            val result = getValidUserCouponsUseCase(userId)
            
            // then
            assertThat(result).hasSize(1)
            assertThat(result[0].status).isEqualTo(UserCouponStatus.VALID)
        }
        
        @Test
        @DisplayName("사용자의 모든 쿠폰이 조회된다")
        fun getAllUserCoupons() {
            // given
            // 유효한 쿠폰 발급
            issueCouponUseCase(
                IssueCouponCommand(
                    templateId = templateId,
                    userId = userId
                )
            )
            
            // 두 번째 템플릿 생성
            val secondTemplate = createCouponTemplateUseCase(
                CreateCouponTemplateCommand(
                    name = "두 번째 쿠폰",
                    description = "두 번째 쿠폰 설명",
                    discountType = DiscountType.FIXED_AMOUNT,
                    discountValue = 1000,
                    minimumOrderAmount = Money(BigDecimal.valueOf(5000)),
                    maximumDiscountAmount = null,
                    maxIssuanceCount = 100,
                    maxIssuancePerUser = 1,
                    validDurationHours = 24,
                    startAt = LocalDateTime.now(),
                    endAt = LocalDateTime.now().plusDays(7)
                )
            )
            
            // 두 번째 쿠폰 발급 후 사용 처리
            val secondCoupon = issueCouponUseCase(
                IssueCouponCommand(
                    templateId = secondTemplate.id,
                    userId = userId
                )
            )
            
            secondCoupon?.let {
                // 쿠폰 사용 처리
                useCouponUseCase(
                    UseCouponCommand(
                        couponId = it.id,
                        userId = userId,
                        orderId = CouponOrderId("test-order-" + System.currentTimeMillis()),
                        orderAmount = Money(BigDecimal.valueOf(5000))
                    )
                )
            }
            
            // when
            val result = getUserCouponsUseCase(userId)
            
            // then - 모든 상태의 쿠폰이 조회됨
            assertThat(result).hasSize(2)
            assertThat(result.any { it.status == UserCouponStatus.VALID }).isTrue()
            assertThat(result.any { it.status == UserCouponStatus.USED }).isTrue()
        }
    }
    
    @Nested
    @DisplayName("쿠폰 사용 유스케이스")
    inner class CouponUsageTest {
        
        @Test
        @DisplayName("쿠폰 사용 시 상태가 USED로 변경된다")
        fun useCoupon() {
            // given
            // 쿠폰 발급
            val coupon = issueCouponUseCase(
                IssueCouponCommand(
                    templateId = templateId,
                    userId = userId
                )
            )
            
            val orderId = "test-order-" + System.currentTimeMillis()
            
            // when
            val command = UseCouponCommand(
                couponId = coupon?.id ?: UserCouponId.create(),
                userId = userId,
                orderId = CouponOrderId(orderId),
                orderAmount = Money(BigDecimal.valueOf(5000))
            )
            
            val result = useCouponUseCase(command)
            
            // then
            assertThat(result).isNotNull
            assertThat(result?.status).isEqualTo(UserCouponStatus.USED)
            assertThat(result?.discountAmount).isEqualTo(orderId)
            
            // 유효 쿠폰 목록에 포함되지 않는지 확인
            val validCoupons = getValidUserCouponsUseCase(userId)
            assertThat(validCoupons).isEmpty()
        }
        
        @Test
        @DisplayName("쿠폰 취소 시 상태가 REVOKED로 변경된다")
        fun revokeCoupon() {
            // given
            // 쿠폰 발급
            val coupon = issueCouponUseCase(
                IssueCouponCommand(
                    templateId = templateId,
                    userId = userId
                )
            )
            
            assertThat(coupon).isNotNull
            // when
            val result = revokeCouponUseCase(RevokeCouponCommand(coupon?.id ?: UserCouponId.create()))
            
            // then
            assertThat(result).isNotNull
            assertThat(result?.status).isEqualTo(UserCouponStatus.REVOKED)
            
            // 유효 쿠폰 목록에 포함되지 않는지 확인
            val validCoupons = getValidUserCouponsUseCase(userId)
            assertThat(validCoupons).isEmpty()
        }
    }
    
    @Nested
    @DisplayName("쿠폰 만료 유스케이스")
    inner class CouponExpirationTest {
        
        @Test
        @DisplayName("만료된 쿠폰은 EXPIRED 상태로 변경된다")
        fun expireOutdatedCoupons() {
            // given
            // 짧은 유효기간의 템플릿 생성
            val shortTemplate = createCouponTemplateUseCase(
                CreateCouponTemplateCommand(
                    name = "즉시 만료 쿠폰",
                    description = "즉시 만료 쿠폰 설명",
                    discountType = DiscountType.PERCENTAGE,
                    discountValue = 10,
                    minimumOrderAmount = Money(BigDecimal.valueOf(5000)),
                    maximumDiscountAmount = Money(BigDecimal.valueOf(5000)),
                    maxIssuanceCount = 100,
                    maxIssuancePerUser = 1,
                    validDurationHours = 1, // 1시간 유효기간
                    startAt = LocalDateTime.now().minusDays(1),
                    endAt = LocalDateTime.now().plusDays(1)
                )
            )
            
            // 쿠폰 발급 후 유효기간을 지난 것으로 설정 (테스트 환경에서)
            val coupon = issueCouponUseCase(
                IssueCouponCommand(
                    templateId = shortTemplate.id,
                    userId = userId
                )
            )
            
            // when
            val result = expireOutdatedCouponsUseCase()
            
            // then
            // 만료된 쿠폰이 존재함
            assertThat(result.expiredCount > 0).isTrue()
            
            // 사용자의 쿠폰을 조회하여 상태 확인
            val userCoupons = getUserCouponsUseCase(userId)
            val expiredCoupon = userCoupons.find { it.id == coupon?.id }
            assertThat(expiredCoupon?.status).isEqualTo(UserCouponStatus.EXPIRED)
        }
    }
    
    @Nested
    @DisplayName("쿠폰 통합 시나리오")
    inner class IntegratedCouponScenarioTest {
        
        @Test
        @DisplayName("쿠폰 템플릿 생성, 발급, 사용, 조회 프로세스가 정상적으로 동작한다")
        fun couponLifecycle() {
            // given - 새 템플릿 생성
            val newTemplate = createCouponTemplateUseCase(
                CreateCouponTemplateCommand(
                    name = "통합 테스트 쿠폰",
                    description = "통합 테스트용 쿠폰",
                    discountType = DiscountType.PERCENTAGE,
                    discountValue = 15,
                    minimumOrderAmount = Money(BigDecimal.valueOf(10000)),
                    maximumDiscountAmount = Money(BigDecimal.valueOf(10000)),
                    maxIssuanceCount = 100,
                    maxIssuancePerUser = 2,
                    validDurationHours = 48,
                    startAt = LocalDateTime.now(),
                    endAt = LocalDateTime.now().plusDays(14)
                )
            )
            
            // 첫 번째 쿠폰 발급
            val coupon1 = issueCouponUseCase(
                IssueCouponCommand(
                    templateId = newTemplate.id,
                    userId = userId
                )
            )
            
            // 두 번째 쿠폰 발급
            val coupon2 = issueCouponUseCase(
                IssueCouponCommand(
                    templateId = newTemplate.id,
                    userId = userId
                )
            )
            
            // 유효 쿠폰 확인
            val validCouponsBeforeUse = getValidUserCouponsUseCase(userId)
            assertThat(validCouponsBeforeUse).hasSize(2)
            
            // 첫 번째 쿠폰 사용
            val orderId = CouponOrderId("integrated-test-order-" + System.currentTimeMillis())
            useCouponUseCase(
                UseCouponCommand(
                    couponId = coupon1?.id ?: UserCouponId.create(),
                    userId = userId,
                    orderId = orderId,
                    orderAmount = Money(BigDecimal.valueOf(20000)) // 주문 금액
                )
            )
            
            assertThat(coupon2?.id).isNotNull()
            
            // 두 번째 쿠폰 취소
            revokeCouponUseCase(
                RevokeCouponCommand(
                    couponId = coupon2?.id ?: UserCouponId.create(),
                )
            )
            
            // then - 최종 상태 확인
            val allCoupons = getUserCouponsUseCase(userId)
            assertThat(allCoupons).hasSize(2)
            
            // 상태별 쿠폰 수 확인
            assertThat(allCoupons.count { it.status == UserCouponStatus.USED }).isEqualTo(1)
            assertThat(allCoupons.count { it.status == UserCouponStatus.REVOKED }).isEqualTo(1)
            
            // 유효 쿠폰 없음 확인
            val validCouponsAfterUse = getValidUserCouponsUseCase(userId)
            assertThat(validCouponsAfterUse).isEmpty()
            
            // 템플릿 발급 카운트 확인
            val templates = getActiveCouponTemplatesUseCase()
            val updatedTemplate = templates.find { it.id == newTemplate.id }
            assertThat(updatedTemplate?.issuedCount).isEqualTo(2)
        }
    }
} 