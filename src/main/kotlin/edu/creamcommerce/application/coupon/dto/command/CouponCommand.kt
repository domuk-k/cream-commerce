package edu.creamcommerce.application.coupon.dto.command

import edu.creamcommerce.domain.common.Money
import edu.creamcommerce.domain.coupon.*
import java.time.LocalDateTime

data class CreateCouponTemplateCommand(
    val name: String,
    val description: String,
    val discountType: DiscountType,
    val discountValue: Int,
    val minimumOrderAmount: Money,
    val maximumDiscountAmount: Money? = null,
    val maxIssuanceCount: Int = 0,
    val maxIssuancePerUser: Int = 1,
    val validDurationHours: Int = 24 * 7,
    val startAt: LocalDateTime,
    val endAt: LocalDateTime
)

data class ChangeCouponTemplateStatusCommand(
    val templateId: CouponTemplateId,
)

data class IssueCouponCommand(
    val userId: UserId,
    val templateId: CouponTemplateId,
)

data class UseCouponCommand(
    val userId: UserId,
    val couponId: UserCouponId,
    val orderAmount: Money,
    val orderId: CouponOrderId
)

data class RevokeCouponCommand(
    val couponId: UserCouponId
) 