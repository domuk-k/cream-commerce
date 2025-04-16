package edu.creamcommerce.application.coupon.usecase

import edu.creamcommerce.application.coupon.dto.command.RevokeCouponCommand
import edu.creamcommerce.application.coupon.dto.query.CouponRevokeResultDto
import edu.creamcommerce.domain.coupon.CouponRepository
import edu.creamcommerce.domain.coupon.UserCouponStatus
import org.springframework.stereotype.Component

@Component
class RevokeCouponUseCase(
    private val couponRepository: CouponRepository
) {
    operator fun invoke(command: RevokeCouponCommand): CouponRevokeResultDto? {
        val coupon = couponRepository.findUserCouponById(command.couponId) ?: return null
        
        if (coupon.status != UserCouponStatus.VALID) {
            return null
        }
        
        coupon.revoke()
        couponRepository.saveUserCoupon(coupon)
        
        return CouponRevokeResultDto(
            couponId = coupon.id,
            status = coupon.status
        )
    }
} 