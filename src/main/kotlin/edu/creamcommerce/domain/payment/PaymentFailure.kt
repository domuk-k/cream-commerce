package edu.creamcommerce.domain.payment

import java.time.LocalDateTime
import java.util.*

class PaymentFailure private constructor(
    val id: PaymentFailureId,
    val paymentId: PaymentId,
    val reason: String,
    val createdAt: LocalDateTime
) {
    companion object {
        fun create(
            id: PaymentFailureId? = null,
            paymentId: PaymentId,
            reason: String
        ): PaymentFailure {
            return PaymentFailure(
                id = id ?: PaymentFailureId.create(),
                paymentId = paymentId,
                reason = reason,
                createdAt = LocalDateTime.now()
            )
        }
    }
}

@JvmInline
value class PaymentFailureId(val value: String) {
    companion object {
        fun create(): PaymentFailureId = PaymentFailureId(UUID.randomUUID().toString())
    }
} 