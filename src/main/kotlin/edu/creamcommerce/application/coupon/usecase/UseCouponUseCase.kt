package edu.creamcommerce.application.coupon.usecase

import edu.creamcommerce.application.coupon.dto.command.UseCouponCommand
import edu.creamcommerce.application.coupon.dto.query.CouponUsageResultDto
import edu.creamcommerce.domain.coupon.CouponRepository
import org.springframework.stereotype.Component

@Component
class UseCouponUseCase(
    private val couponRepository: CouponRepository
) {
    operator fun invoke(command: UseCouponCommand): CouponUsageResultDto? {
        val coupon = couponRepository.findUserCouponById(command.couponId) 
            ?: return null
            
        // 쿠폰 소유자 확인
        if (coupon.userId != command.userId) {
            return null
        }
        
        // 최소 주문 금액 확인
        if (command.orderAmount < coupon.minimumOrderAmount) {
            return null
        }
        
        // 쿠폰 사용 가능 여부 확인
        if (!coupon.isUsable()) {
            return null
        }
        
        // 할인 금액 계산
        val discountAmount = coupon.calculateDiscount(command.orderAmount)
        
        // 쿠폰 사용 처리
        if (!coupon.use()) {
            return null
        }
        
        couponRepository.saveUserCoupon(coupon)
        
        return CouponUsageResultDto(
            couponId = coupon.id,
            discountAmount = discountAmount,
            status = coupon.status
        )
    }
} 