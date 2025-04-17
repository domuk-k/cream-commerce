package edu.creamcommerce.integration

import edu.creamcommerce.application.point.dto.command.ChargePointCommand
import edu.creamcommerce.application.point.dto.command.UsePointCommand
import edu.creamcommerce.application.point.usecase.ChargePointUseCase
import edu.creamcommerce.application.point.usecase.GetPointByUserIdUseCase
import edu.creamcommerce.application.point.usecase.UsePointUseCase
import edu.creamcommerce.domain.coupon.UserId
import edu.creamcommerce.domain.point.PointHistoryRepository
import edu.creamcommerce.domain.point.PointHistoryType
import edu.creamcommerce.domain.point.PointRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Transactional
class PointIntegrationTest : BaseIntegrationTest() {
    
    @Autowired
    private lateinit var pointRepository: PointRepository
    
    @Autowired
    private lateinit var pointHistoryRepository: PointHistoryRepository
    
    @Autowired
    private lateinit var chargePointUseCase: ChargePointUseCase
    
    @Autowired
    private lateinit var usePointUseCase: UsePointUseCase
    
    @Autowired
    private lateinit var getPointByUserIdUseCase: GetPointByUserIdUseCase
    
    private var userId = UserId("")
    
    @BeforeEach
    fun setUp() {
        userId = UserId("test-user-" + System.currentTimeMillis())
    }
    
    @Nested
    @DisplayName("포인트 충전 유스케이스")
    inner class ChargePointTest {
        
        @Test
        @DisplayName("포인트 충전 시 포인트 잔액이 증가하고 이력이 저장된다")
        fun chargePoint() {
            // given
            val amount = BigDecimal.valueOf(1000)
            
            // when
            val result = chargePointUseCase(ChargePointCommand(userId, amount))
            
            // then
            // 포인트 잔액 확인
            val point = pointRepository.findByUserId(userId)
            assertThat(point).isNotNull
            assertThat(point!!.amount).isEqualTo(amount)
            
            // 포인트 이력 확인
            val histories = pointHistoryRepository.findByPointId(result.id)
            assertThat(histories).hasSize(1)
            assertThat(histories[0].type).isEqualTo(PointHistoryType.CHARGE)
            assertThat(histories[0].amount).isEqualTo(amount)
        }
        
        @Test
        @DisplayName("포인트를 여러번 충전하면 잔액에 누적된다")
        fun chargePointMultipleTimes() {
            // given
            val firstAmount = BigDecimal.valueOf(1000)
            val secondAmount = BigDecimal.valueOf(2000)
            
            // when
            val firstResult = chargePointUseCase(ChargePointCommand(userId, firstAmount))
            val secondResult = chargePointUseCase(ChargePointCommand(userId, secondAmount))
            
            // then
            // 포인트 잔액 확인
            val point = pointRepository.findByUserId(userId)
            assertThat(point).isNotNull
            assertThat(point!!.amount).isEqualTo(firstAmount.add(secondAmount))
            
            // 포인트 이력 확인
            val histories = pointHistoryRepository.findByPointId(secondResult.id)
            assertThat(histories).hasSize(2)
            
            // 최신 이력이 먼저 조회됨
            assertThat(histories[0].type).isEqualTo(PointHistoryType.CHARGE)
            assertThat(histories[0].amount).isEqualTo(secondAmount)
            
            assertThat(histories[1].type).isEqualTo(PointHistoryType.CHARGE)
            assertThat(histories[1].amount).isEqualTo(firstAmount)
        }
    }
    
    @Nested
    @DisplayName("포인트 조회 유스케이스")
    inner class GetPointTest {
        
        @Test
        @DisplayName("포인트 조회 시 정확한 잔액을 반환한다")
        fun getPointByUserId() {
            // given
            val amount = BigDecimal.valueOf(1000)
            chargePointUseCase(ChargePointCommand(userId, amount))
            
            // when
            val result = getPointByUserIdUseCase(userId)
            
            // then
            assertThat(result).isNotNull
            if (result !== null) {
                assertThat(result.userId).isEqualTo(userId)
                assertThat(result.amount).isEqualTo(amount)
            }
        }
        
        @Test
        @DisplayName("포인트가 없는 사용자 조회 시 결과는 null이다")
        fun getPointByUserIdWithNoPoint() {
            // given
            val nonExistingUserId = UserId("non-existing-user")
            
            // when
            val result = getPointByUserIdUseCase(nonExistingUserId)
            
            // then
            assertThat(result).isNull()
        }
    }
    
    @Nested
    @DisplayName("포인트 사용 유스케이스")
    inner class UsePointTest {
        
        @Test
        @DisplayName("포인트 사용 시 잔액이 감소하고 이력이 저장된다")
        fun usePoint() {
            // given
            val chargeAmount = BigDecimal.valueOf(1000)
            val useAmount = BigDecimal.valueOf(500)
            chargePointUseCase(ChargePointCommand(userId, chargeAmount))
            
            // when
            val result = usePointUseCase(UsePointCommand(userId, useAmount))
            
            // then
            // 포인트 잔액 확인
            val point = pointRepository.findByUserId(userId)
            assertThat(point).isNotNull
            assertThat(point!!.amount).isEqualTo(chargeAmount.subtract(useAmount))
            
            // 포인트 이력 확인
            val histories = pointHistoryRepository.findByPointId(result.id)
            assertThat(histories).hasSize(2)
            
            assertThat(histories[0].type).isEqualTo(PointHistoryType.CHARGE)
            assertThat(histories[0].amount).isEqualTo(chargeAmount)
            
            assertThat(histories[1].type).isEqualTo(PointHistoryType.USE)
            assertThat(histories[1].amount).isEqualTo(useAmount.negate())
        }
        
        @Test
        @DisplayName("포인트 전액 사용 시 잔액이 0이 된다")
        fun useAllPoints() {
            // given
            val chargeAmount = BigDecimal.valueOf(1000)
            chargePointUseCase(ChargePointCommand(userId, chargeAmount))
            
            // when
            val result = usePointUseCase(UsePointCommand(userId, chargeAmount))
            
            // then
            // 포인트 잔액 확인
            val point = pointRepository.findByUserId(userId)
            assertThat(point).isNotNull
            assertThat(point!!.amount).isEqualTo(BigDecimal.ZERO)
            
            // 포인트 이력 확인
            val histories = pointHistoryRepository.findByPointId(result.id)
            assertThat(histories).hasSize(2)
            
            assertThat(histories[0].type).isEqualTo(PointHistoryType.USE)
            assertThat(histories[0].amount).isEqualTo(chargeAmount.negate())
        }
    }
    
    @Nested
    @DisplayName("포인트 통합 시나리오")
    inner class IntegratedPointScenarioTest {
        
        @Test
        @DisplayName("포인트 충전 후 사용 시 잔액과 이력이 정확히 반영된다")
        fun chargeAndUsePoint() {
            // given
            val chargeAmount = BigDecimal.valueOf(1000)
            val useAmount = BigDecimal.valueOf(300)
            
            // when
            val point = chargePointUseCase(ChargePointCommand(userId, chargeAmount))
            usePointUseCase(UsePointCommand(userId, useAmount))
            
            // then
            // 포인트 잔액 확인
            val savedPoint = pointRepository.findByUserId(userId)
            assertThat(savedPoint).isNotNull
            assertThat(savedPoint!!.amount).isEqualTo(chargeAmount.subtract(useAmount))
            
            // 포인트 이력 확인
            val histories = pointHistoryRepository.findByPointId(point.id)
            assertThat(histories).hasSize(2)
            
            // 포인트 조회 API 확인
            val pointDto = getPointByUserIdUseCase(userId)
            assertThat(pointDto?.amount).isEqualTo(chargeAmount.subtract(useAmount))
        }
        
        @Test
        @DisplayName("포인트 충전, 사용, 추가 충전, 추가 사용 시 최종 잔액이 정확하다")
        fun complexPointScenario() {
            // given
            val firstChargeAmount = BigDecimal.valueOf(1000)
            val firstUseAmount = BigDecimal.valueOf(300)
            val secondChargeAmount = BigDecimal.valueOf(500)
            val secondUseAmount = BigDecimal.valueOf(700)
            
            // when - 충전 -> 사용 -> 충전 -> 사용 시나리오
            chargePointUseCase(ChargePointCommand(userId, firstChargeAmount))
            usePointUseCase(UsePointCommand(userId, firstUseAmount))
            chargePointUseCase(ChargePointCommand(userId, secondChargeAmount))
            usePointUseCase(UsePointCommand(userId, secondUseAmount))
            
            // then
            // 최종 포인트 잔액 확인
            val expectedAmount = firstChargeAmount
                .subtract(firstUseAmount)
                .add(secondChargeAmount)
                .subtract(secondUseAmount)
            
            val savedPoint = pointRepository.findByUserId(userId)
            assertThat(savedPoint).isNotNull
            assertThat(savedPoint!!.amount).isEqualTo(expectedAmount)
            
            // 포인트 이력 확인
            val histories = pointHistoryRepository.findByPointId(savedPoint.id)
            assertThat(histories).hasSize(4)
            
            // 포인트 조회 API 확인
            val pointDto = getPointByUserIdUseCase(userId)
            assertThat(pointDto?.amount).isEqualTo(expectedAmount)
        }
    }
} 