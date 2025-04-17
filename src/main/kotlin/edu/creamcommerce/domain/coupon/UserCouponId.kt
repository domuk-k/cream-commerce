package edu.creamcommerce.domain.coupon

import java.util.*

@JvmInline
value class UserCouponId(val value: String) {
    companion object {
        fun create(): UserCouponId = UserCouponId(UUID.randomUUID().toString())
    }
}

@JvmInline
value class CouponOrderId(val value: String) {
    companion object {
        fun create(): UserCouponId = UserCouponId(UUID.randomUUID().toString())
    }
}

