package edu.creamcommerce.domain.coupon

import java.util.UUID

@JvmInline
value class UserCouponId(val value: String) {
    companion object {
        fun create(): UserCouponId = UserCouponId(UUID.randomUUID().toString())
    }
} 