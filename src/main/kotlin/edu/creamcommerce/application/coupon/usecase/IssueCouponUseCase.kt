package edu.creamcommerce.application.coupon.usecase

import edu.creamcommerce.application.coupon.dto.command.IssueCouponCommand
import edu.creamcommerce.application.coupon.dto.query.UserCouponDto
import edu.creamcommerce.application.coupon.dto.query.toDto
import edu.creamcommerce.domain.coupon.CouponRepository
import org.springframework.stereotype.Component

@Component
class IssueCouponUseCase(
    private val couponRepository: CouponRepository
) {
    operator fun invoke(command: IssueCouponCommand): UserCouponDto? {
        val template = couponRepository.findTemplateById(command.templateId)
            ?: throw IllegalArgumentException("쿠폰 템플릿을 찾을 수 없습니다: ${command.templateId}")
        
        // 사용자별 발급 제한 확인
        val issuedCount = couponRepository.findUserCouponCount(command.userId, command.templateId)
        if (issuedCount >= template.maxIssuancePerUser) {
            return null
        }
        
        // 쿠폰 발급
        val userCoupon = edu.creamcommerce.domain.coupon.UserCoupon.issue(command.userId, template) ?: return null
        
        val savedCoupon = couponRepository.saveUserCoupon(userCoupon)
        
        return savedCoupon.toDto()
    }
} 