package edu.creamcommerce.infrastructure.coupon.mapper

import edu.creamcommerce.domain.common.Money
import edu.creamcommerce.domain.coupon.*
import edu.creamcommerce.infrastructure.coupon.entity.CouponTemplateEntity
import edu.creamcommerce.infrastructure.coupon.entity.UserCouponEntity
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.time.LocalDateTime

@Component
class CouponMapper {
    // CouponTemplate 매핑
    fun toEntity(couponTemplate: CouponTemplate): CouponTemplateEntity {
        return CouponTemplateEntity(
            id = couponTemplate.id.value,
            name = couponTemplate.name,
            description = couponTemplate.description,
            discountType = couponTemplate.discountType,
            discountValue = couponTemplate.discountValue,
            minimumOrderAmount = BigDecimal.valueOf(couponTemplate.minimumOrderAmount.amount.toLong()),
            maximumDiscountAmount = couponTemplate.maximumDiscountAmount?.let {
                BigDecimal.valueOf(it.amount.toLong())
            },
            maxIssuanceCount = couponTemplate.maxIssuanceCount,
            maxIssuancePerUser = couponTemplate.maxIssuancePerUser,
            validDurationHours = couponTemplate.validDurationHours,
            startAt = couponTemplate.startAt,
            endAt = couponTemplate.endAt,
            issuedCount = couponTemplate.issuedCount,
            status = couponTemplate.status,
            createdAt = LocalDateTime.now()
        )
    }
    
    fun toDomain(entity: CouponTemplateEntity): CouponTemplate {
        val template = CouponTemplate.create(
            id = CouponTemplateId(entity.id),
            name = entity.name,
            description = entity.description,
            discountType = entity.discountType,
            discountValue = entity.discountValue,
            minimumOrderAmount = Money(entity.minimumOrderAmount),
            maximumDiscountAmount = entity.maximumDiscountAmount?.let { Money(it) },
            maxIssuanceCount = entity.maxIssuanceCount,
            maxIssuancePerUser = entity.maxIssuancePerUser,
            validDurationHours = entity.validDurationHours,
            startAt = entity.startAt,
            endAt = entity.endAt,
        )
        
        // 발급 수량
        repeat(entity.issuedCount) {
            template.issue()
        }
        
        // 상태 동기화
        when (entity.status) {
            CouponTemplateStatus.SUSPENDED -> template.suspend()
            CouponTemplateStatus.TERMINATED -> template.terminate()
            CouponTemplateStatus.DEPLETED -> { /* 자동 전환됨 */
            }
            
            CouponTemplateStatus.ACTIVE -> { /* 기본 상태 */
            }
        }
        
        return template
    }
    
    // UserCoupon 매핑
    fun toEntity(userCoupon: UserCoupon): UserCouponEntity {
        return UserCouponEntity(
            id = userCoupon.id.value,
            userId = userCoupon.userId.value,
            templateId = userCoupon.templateId.value,
            name = userCoupon.name,
            description = userCoupon.description,
            discountType = userCoupon.discountType,
            discountValue = userCoupon.discountValue,
            minimumOrderAmount = BigDecimal.valueOf(userCoupon.minimumOrderAmount.amount.toLong()),
            maximumDiscountAmount = userCoupon.maximumDiscountAmount?.let {
                BigDecimal.valueOf(it.amount.toLong())
            },
            status = userCoupon.status,
            validUntil = userCoupon.validUntil,
            usedAt = if (userCoupon.status == UserCouponStatus.USED) LocalDateTime.now() else null, // 상태에 따라 설정
            orderId = userCoupon.orderId?.value,
            issuedAt = userCoupon.issuedAt,
        )
    }
    
    fun toDomain(entity: UserCouponEntity): UserCoupon {
        val userCoupon = UserCoupon.create(
            id = UserCouponId(entity.id),
            userId = UserId(entity.userId),
            templateId = CouponTemplateId(entity.templateId),
            name = entity.name,
            description = entity.description,
            discountType = entity.discountType,
            discountValue = entity.discountValue,
            minimumOrderAmount = Money(entity.minimumOrderAmount),
            maximumDiscountAmount = entity.maximumDiscountAmount?.let { Money(it) },
            validUntil = entity.validUntil,
            status = entity.status,
            orderId = entity.orderId?.let { CouponOrderId(it) },
            issuedAt = entity.issuedAt,
            usedAt = entity.usedAt,
        )
        
        return userCoupon
    }
    
}