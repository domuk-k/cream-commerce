package edu.creamcommerce.application.coupon.usecase

import edu.creamcommerce.application.coupon.dto.command.CreateCouponTemplateCommand
import edu.creamcommerce.application.coupon.dto.query.CouponTemplateDto
import edu.creamcommerce.application.coupon.dto.query.toDto
import edu.creamcommerce.domain.coupon.CouponRepository
import edu.creamcommerce.domain.coupon.CouponTemplate
import org.springframework.stereotype.Component

@Component
class CreateCouponTemplateUseCase(
    private val couponRepository: CouponRepository
) {
    operator fun invoke(command: CreateCouponTemplateCommand): CouponTemplateDto {
        val template = CouponTemplate.create(
            name = command.name,
            description = command.description,
            discountType = command.discountType,
            discountValue = command.discountValue,
            minimumOrderAmount = command.minimumOrderAmount,
            maximumDiscountAmount = command.maximumDiscountAmount,
            maxIssuanceCount = command.maxIssuanceCount,
            maxIssuancePerUser = command.maxIssuancePerUser,
            validDurationHours = command.validDurationHours,
            startAt = command.startAt,
            endAt = command.endAt
        )
        
        val savedTemplate = couponRepository.saveTemplate(template)
        
        return savedTemplate.toDto()
    }
} 