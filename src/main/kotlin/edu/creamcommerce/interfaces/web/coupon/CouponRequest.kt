package edu.creamcommerce.interfaces.web.coupon

import edu.creamcommerce.domain.common.Money
import edu.creamcommerce.domain.coupon.DiscountType
import java.time.LocalDateTime

data class CreateCouponTemplateRequest(
    val name: String,
    val description: String,
    val discountType: DiscountType,
    val discountValue: Int,
    val minimumOrderAmount: Int,
    val maximumDiscountAmount: Int? = null,
    val maxIssuanceCount: Int = 0,
    val maxIssuancePerUser: Int = 1,
    val validDurationHours: Int = 24 * 7,
    val startAt: LocalDateTime,
    val endAt: LocalDateTime
)

data class ChangeCouponTemplateStatusRequest(
    val templateId: String
)

data class IssueCouponRequest(
    val userId: String,
    val templateId: String
)

data class UseCouponRequest(
    val userId: String,
    val couponId: String,
    val orderAmount: Int
)

data class RevokeCouponRequest(
    val couponId: String
) 