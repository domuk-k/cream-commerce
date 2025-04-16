package edu.creamcommerce.application.coupon.usecase

import edu.creamcommerce.application.coupon.dto.query.CouponTemplateDto
import edu.creamcommerce.application.coupon.dto.query.toDto
import edu.creamcommerce.domain.coupon.CouponRepository
import org.springframework.stereotype.Component

@Component
class GetActiveCouponTemplatesUseCase(
    private val couponRepository: CouponRepository
) {
    operator fun invoke(): List<CouponTemplateDto> {
        val templates = couponRepository.findAllActiveTemplates()
        
        return templates.map { it.toDto() }
    }
} 