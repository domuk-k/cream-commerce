package edu.creamcommerce.interfaces.web.coupon

//
//@RestController
//@RequestMapping("/api/coupons")
//@Tag(name = "쿠폰 API", description = "선착순 쿠폰 발급 및 조회 API")
//class CouponController {
//    private val userCoupons = ConcurrentHashMap<Long, MutableList<UserCouponDto>>()
//
//    @GetMapping("/available")
//    @Operation(summary = "발급 가능한 쿠폰 목록 조회", description = "현재 발급 가능한 쿠폰 목록을 조회합니다.")
//    fun getAvailableCoupons(): ResponseEntity<AvailableCouponsResponse> {
//        val availableCoupons = mockCoupons.filter {
//            it.isActive &&
//                    it.startDate.isBefore(LocalDateTime.now()) &&
//                    it.endDate.isAfter(LocalDateTime.now()) &&
//                    (couponQuantities[it.id]?.get() ?: 0) > 0
//        }
//
//        return ResponseEntity.ok(
//            AvailableCouponsResponse(
//                coupons = availableCoupons,
//                totalCount = availableCoupons.size
//            )
//        )
//    }
//
//    @PostMapping("/{id}/claim")
//    @Operation(summary = "선착순 쿠폰 발급 요청", description = "선착순 쿠폰 발급을 요청합니다.")
//    fun claimCoupon(@PathVariable id: Long): ResponseEntity<ClaimCouponResponse> {
//        val userId = 1L // 보통은 인증된 사용자 ID
//        val coupon = mockCoupons.find { it.id == id }
//
//        if (coupon == null) {
//            return ResponseEntity.ok(
//                ClaimCouponResponse(
//                    success = false,
//                    message = "존재하지 않는 쿠폰입니다."
//                )
//            )
//        }
//
//        // 이미 발급받은 쿠폰인지 확인
//        val userCouponList = userCoupons.getOrPut(userId) { mutableListOf() }
//        if (userCouponList.any { it.couponId == id }) {
//            return ResponseEntity.ok(
//                ClaimCouponResponse(
//                    success = false,
//                    message = "이미 발급받은 쿠폰입니다."
//                )
//            )
//        }
//
//        // 쿠폰 유효성 확인
//        if (!coupon.isActive) {
//            return ResponseEntity.ok(
//                ClaimCouponResponse(
//                    success = false,
//                    message = "비활성화된 쿠폰입니다."
//                )
//            )
//        }
//
//        val now = LocalDateTime.now()
//        if (now.isBefore(coupon.startDate) || now.isAfter(coupon.endDate)) {
//            return ResponseEntity.ok(
//                ClaimCouponResponse(
//                    success = false,
//                    message = "쿠폰 발급 기간이 아닙니다."
//                )
//            )
//        }
//
//        // 남은 수량 확인 및 감소 (원자적 연산)
//        val remainingQuantity = couponQuantities[id] ?: AtomicLong(0)
//        val newQuantity = remainingQuantity.decrementAndGet()
//
//        if (newQuantity < 0) {
//            // 수량이 부족하면 다시 증가시키고 실패 응답
//            remainingQuantity.incrementAndGet()
//            return ResponseEntity.ok(
//                ClaimCouponResponse(
//                    success = false,
//                    message = "쿠폰이 모두 소진되었습니다."
//                )
//            )
//        }
//
//        // 쿠폰 발급 처리
//        val userCouponDto = UserCouponDto(
//            id = (userCouponList.maxOfOrNull { it.id ?: 0L } ?: 0L) + 1,
//            userId = userId,
//            couponId = id,
//            couponInfo = coupon.copy(remainingQuantity = newQuantity.toInt())
//        )
//
//        userCouponList.add(userCouponDto)
//        userCoupons[userId] = userCouponList
//
//        return ResponseEntity.ok(
//            ClaimCouponResponse(
//                success = true,
//                message = "쿠폰이 성공적으로 발급되었습니다.",
//                coupon = userCouponDto
//            )
//        )
//    }
//
//    @GetMapping("/my-coupons")
//    @Operation(summary = "내 쿠폰 목록 조회", description = "사용자가 보유한 쿠폰 목록을 조회합니다.")
//    fun getMyCoupons(): ResponseEntity<List<UserCouponDto>> {
//        val userId = 1L // 보통은 인증된 사용자 ID
//        return ResponseEntity.ok(userCoupons[userId] ?: emptyList())
//    }
//}