package edu.creamcommerce.application.coupon.usecase

import edu.creamcommerce.application.coupon.dto.query.CouponTemplateStatusDto
import edu.creamcommerce.domain.coupon.CouponRepository
import edu.creamcommerce.domain.coupon.CouponTemplateId
import edu.creamcommerce.domain.coupon.CouponTemplateStatus
import org.springframework.stereotype.Component

@Component
class ChangeCouponTemplateStatusUseCase(
    private val couponRepository: CouponRepository
) {
    operator fun invoke(templateId: CouponTemplateId, targetStatus: CouponTemplateStatus): CouponTemplateStatusDto {
        val template = couponRepository.findTemplateById(templateId)
            ?: throw IllegalArgumentException("쿠폰 템플릿을 찾을 수 없습니다: $templateId")
        
        when (targetStatus) {
            CouponTemplateStatus.ACTIVE -> template.resume()
            CouponTemplateStatus.SUSPENDED -> template.suspend()
            CouponTemplateStatus.TERMINATED -> template.terminate()
            CouponTemplateStatus.DEPLETED -> throw IllegalArgumentException("DEPLETED 상태로 직접 변경할 수 없습니다.")
        }
        
        val updatedTemplate = couponRepository.saveTemplate(template)
        
        return CouponTemplateStatusDto(
            id = updatedTemplate.id,
            status = updatedTemplate.status
        )
    }
} 