package edu.creamcommerce.interfaces.coupon

import edu.creamcommerce.dto.*
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong

@RestController
@RequestMapping("/api/coupons")
@Tag(name = "쿠폰 API", description = "선착순 쿠폰 발급 및 조회 API")
class CouponController {

    // 쿠폰 정보 Mock 데이터
    private val mockCoupons = listOf(
        CouponDefinitionDto(
            id = 1L,
            code = "WELCOME10",
            name = "신규 가입 10% 할인",
            description = "신규 가입 회원을 위한 10% 할인 쿠폰",
            discountType = DiscountType.PERCENT,
            discountValue = BigDecimal("10.00"),
            maxDiscount = BigDecimal("5000.00"),
            minOrderAmount = BigDecimal("10000.00"),
            startDate = LocalDateTime.now().minusDays(30),
            endDate = LocalDateTime.now().plusDays(30),
            totalQuantity = 1000,
            remainingQuantity = 500,
            isActive = true
        ),
        CouponDefinitionDto(
            id = 2L,
            code = "FLASH20",
            name = "선착순 20% 할인",
            description = "선착순 100명에게 제공되는 20% 할인 쿠폰",
            discountType = DiscountType.PERCENT,
            discountValue = BigDecimal("20.00"),
            maxDiscount = BigDecimal("10000.00"),
            minOrderAmount = BigDecimal("20000.00"),
            startDate = LocalDateTime.now(),
            endDate = LocalDateTime.now().plusDays(7),
            totalQuantity = 100,
            remainingQuantity = 100,
            isActive = true
        ),
        CouponDefinitionDto(
            id = 3L,
            code = "SUMMER5000",
            name = "여름 특별 5000원 할인",
            description = "모든 상품 5000원 할인",
            discountType = DiscountType.FIXED,
            discountValue = BigDecimal("5000.00"),
            minOrderAmount = BigDecimal("30000.00"),
            startDate = LocalDateTime.now(),
            endDate = LocalDateTime.now().plusMonths(2),
            totalQuantity = 500,
            remainingQuantity = 500,
            isActive = true
        )
    )

    // 사용자별 쿠폰 발급 현황 (동시성 고려)
    private val userCoupons = ConcurrentHashMap<Long, MutableList<UserCouponDto>>()

    // 쿠폰별 남은 수량 (동시성 고려)
    private val couponQuantities = ConcurrentHashMap<Long, AtomicLong>().apply {
        mockCoupons.forEach { coupon ->
            this[coupon.id!!] = AtomicLong(coupon.remainingQuantity.toLong())
        }
    }

    @GetMapping("/available")
    @Operation(summary = "발급 가능한 쿠폰 목록 조회", description = "현재 발급 가능한 쿠폰 목록을 조회합니다.")
    fun getAvailableCoupons(): ResponseEntity<AvailableCouponsResponse> {
        val availableCoupons = mockCoupons.filter {
            it.isActive &&
                    it.startDate.isBefore(LocalDateTime.now()) &&
                    it.endDate.isAfter(LocalDateTime.now()) &&
                    couponQuantities[it.id]?.get() ?: 0 > 0
        }

        return ResponseEntity.ok(
            AvailableCouponsResponse(
                coupons = availableCoupons,
                totalCount = availableCoupons.size
            )
        )
    }

    @PostMapping("/{id}/claim")
    @Operation(summary = "선착순 쿠폰 발급 요청", description = "선착순 쿠폰 발급을 요청합니다.")
    fun claimCoupon(@PathVariable id: Long): ResponseEntity<ClaimCouponResponse> {
        val userId = 1L // 보통은 인증된 사용자 ID
        val coupon = mockCoupons.find { it.id == id }

        if (coupon == null) {
            return ResponseEntity.ok(
                ClaimCouponResponse(
                    success = false,
                    message = "존재하지 않는 쿠폰입니다."
                )
            )
        }

        // 이미 발급받은 쿠폰인지 확인
        val userCouponList = userCoupons.getOrPut(userId) { mutableListOf() }
        if (userCouponList.any { it.couponId == id }) {
            return ResponseEntity.ok(
                ClaimCouponResponse(
                    success = false,
                    message = "이미 발급받은 쿠폰입니다."
                )
            )
        }

        // 쿠폰 유효성 확인
        if (!coupon.isActive) {
            return ResponseEntity.ok(
                ClaimCouponResponse(
                    success = false,
                    message = "비활성화된 쿠폰입니다."
                )
            )
        }

        val now = LocalDateTime.now()
        if (now.isBefore(coupon.startDate) || now.isAfter(coupon.endDate)) {
            return ResponseEntity.ok(
                ClaimCouponResponse(
                    success = false,
                    message = "쿠폰 발급 기간이 아닙니다."
                )
            )
        }

        // 남은 수량 확인 및 감소 (원자적 연산)
        val remainingQuantity = couponQuantities[id] ?: AtomicLong(0)
        val newQuantity = remainingQuantity.decrementAndGet()

        if (newQuantity < 0) {
            // 수량이 부족하면 다시 증가시키고 실패 응답
            remainingQuantity.incrementAndGet()
            return ResponseEntity.ok(
                ClaimCouponResponse(
                    success = false,
                    message = "쿠폰이 모두 소진되었습니다."
                )
            )
        }

        // 쿠폰 발급 처리
        val userCouponDto = UserCouponDto(
            id = (userCouponList.maxOfOrNull { it.id ?: 0L } ?: 0L) + 1,
            userId = userId,
            couponId = id,
            couponInfo = coupon.copy(remainingQuantity = newQuantity.toInt())
        )

        userCouponList.add(userCouponDto)
        userCoupons[userId] = userCouponList

        return ResponseEntity.ok(
            ClaimCouponResponse(
                success = true,
                message = "쿠폰이 성공적으로 발급되었습니다.",
                coupon = userCouponDto
            )
        )
    }

    @GetMapping("/my-coupons")
    @Operation(summary = "내 쿠폰 목록 조회", description = "사용자가 보유한 쿠폰 목록을 조회합니다.")
    fun getMyCoupons(): ResponseEntity<List<UserCouponDto>> {
        val userId = 1L // 보통은 인증된 사용자 ID
        return ResponseEntity.ok(userCoupons[userId] ?: emptyList())
    }
}