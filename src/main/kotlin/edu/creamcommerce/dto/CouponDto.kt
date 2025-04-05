package edu.creamcommerce.dto

import java.math.BigDecimal
import java.time.LocalDateTime

enum class DiscountType {
    PERCENT,
    FIXED
}

data class CouponDefinitionDto(
    val id: Long? = null,
    val code: String,
    val name: String,
    val description: String,
    val discountType: DiscountType,
    val discountValue: BigDecimal,
    val maxDiscount: BigDecimal? = null,
    val minOrderAmount: BigDecimal? = null,
    val startDate: LocalDateTime,
    val endDate: LocalDateTime,
    val totalQuantity: Int,
    val remainingQuantity: Int,
    val isActive: Boolean = true
)

data class UserCouponDto(
    val id: Long? = null,
    val userId: Long,
    val couponId: Long,
    val couponInfo: CouponDefinitionDto,
    val isUsed: Boolean = false,
    val usedAt: LocalDateTime? = null,
    val acquiredAt: LocalDateTime = LocalDateTime.now()
)

data class ClaimCouponResponse(
    val success: Boolean,
    val message: String,
    val coupon: UserCouponDto? = null
)

data class AvailableCouponsResponse(
    val coupons: List<CouponDefinitionDto>,
    val totalCount: Int
)