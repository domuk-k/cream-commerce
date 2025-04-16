package edu.creamcommerce.application.coupon.usecase

import edu.creamcommerce.application.coupon.dto.query.UserCouponDto
import edu.creamcommerce.application.coupon.dto.query.toDto
import edu.creamcommerce.domain.coupon.CouponRepository
import edu.creamcommerce.domain.coupon.UserId
import org.springframework.stereotype.Component

@Component
class GetUserCouponsUseCase(
    private val couponRepository: CouponRepository
) {
    operator fun invoke(userId: UserId): List<UserCouponDto> {
        val coupons = couponRepository.findUserCouponsByUserId(userId)
        
        return coupons.map { it.toDto() }
    }
} 