package edu.creamcommerce.domain.coupon

import java.util.UUID

@JvmInline
value class CouponTemplateId(val value: String) {
    companion object {
        fun create(): CouponTemplateId = CouponTemplateId(UUID.randomUUID().toString())
    }
} 