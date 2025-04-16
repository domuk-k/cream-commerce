package edu.creamcommerce.application.coupon.dto.query

import edu.creamcommerce.domain.common.Money
import edu.creamcommerce.domain.coupon.*
import java.time.LocalDateTime

data class CouponTemplateDto(
    val id: CouponTemplateId,
    val name: String,
    val description: String,
    val discountType: DiscountType,
    val discountValue: Int,
    val minimumOrderAmount: Money,
    val maximumDiscountAmount: Money?,
    val maxIssuanceCount: Int,
    val issuedCount: Int,
    val maxIssuancePerUser: Int,
    val validDurationHours: Int,
    val startAt: LocalDateTime,
    val endAt: LocalDateTime,
    val status: CouponTemplateStatus
)

data class CouponTemplateStatusDto(
    val id: CouponTemplateId,
    val status: CouponTemplateStatus
)

data class UserCouponDto(
    val id: UserCouponId,
    val userId: UserId,
    val templateId: CouponTemplateId,
    val name: String,
    val description: String,
    val discountType: DiscountType,
    val discountValue: Int,
    val minimumOrderAmount: Money,
    val maximumDiscountAmount: Money?,
    val validUntil: LocalDateTime,
    val status: UserCouponStatus
)

data class CouponUsageResultDto(
    val couponId: UserCouponId,
    val discountAmount: Money,
    val status: UserCouponStatus
)

data class CouponRevokeResultDto(
    val couponId: UserCouponId,
    val status: UserCouponStatus
)

data class ExpireCouponsResultDto(
    val expiredCount: Int
)

// 확장 함수로 Entity -> DTO 변환 기능 제공
fun CouponTemplate.toDto(): CouponTemplateDto {
    return CouponTemplateDto(
        id = this.id,
        name = this.name,
        description = this.description,
        discountType = this.discountType,
        discountValue = this.discountValue,
        minimumOrderAmount = this.minimumOrderAmount,
        maximumDiscountAmount = this.maximumDiscountAmount,
        maxIssuanceCount = this.maxIssuanceCount,
        issuedCount = this.issuedCount,
        maxIssuancePerUser = this.maxIssuancePerUser,
        validDurationHours = this.validDurationHours,
        startAt = this.startAt,
        endAt = this.endAt,
        status = this.status
    )
}

fun UserCoupon.toDto(): UserCouponDto {
    return UserCouponDto(
        id = this.id,
        userId = this.userId,
        templateId = this.templateId,
        name = this.name,
        description = this.description,
        discountType = this.discountType,
        discountValue = this.discountValue,
        minimumOrderAmount = this.minimumOrderAmount,
        maximumDiscountAmount = this.maximumDiscountAmount,
        validUntil = this.validUntil,
        status = this.status
    )
} 