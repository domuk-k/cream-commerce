package edu.creamcommerce.interfaces.response

import java.math.BigDecimal
import java.time.LocalDateTime

data class ProcessPaymentRequest(
    val orderId: String
)

data class PaymentResponse(
    val id: String,
    val orderId: String,
    val status: String,
    val amount: BigDecimal,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
) 