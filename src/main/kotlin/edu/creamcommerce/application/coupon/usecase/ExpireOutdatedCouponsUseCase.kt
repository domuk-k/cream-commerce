package edu.creamcommerce.application.coupon.usecase

import edu.creamcommerce.application.coupon.dto.query.ExpireCouponsResultDto
import edu.creamcommerce.domain.coupon.CouponRepository
import org.springframework.stereotype.Component

@Component
class ExpireOutdatedCouponsUseCase(
    private val couponRepository: CouponRepository
) {
    operator fun invoke(): ExpireCouponsResultDto {
        val expiredCoupons = couponRepository.findExpiredUserCoupons()
        var expiredCount = 0
        
        for (coupon in expiredCoupons) {
            coupon.expire()
            couponRepository.saveUserCoupon(coupon)
            expiredCount++
        }
        
        return ExpireCouponsResultDto(
            expiredCount = expiredCount
        )
    }
} 