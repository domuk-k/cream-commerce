package edu.creamcommerce.domain.coupon

import java.util.UUID

@JvmInline
value class UserId(val value: String) {
    companion object {
        fun create(): UserId = UserId(UUID.randomUUID().toString())
    }
} 