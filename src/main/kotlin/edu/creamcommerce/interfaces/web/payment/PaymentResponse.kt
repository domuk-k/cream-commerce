package edu.creamcommerce.interfaces.web.payment

import java.math.BigDecimal
import java.time.LocalDateTime

data class PaymentResponse(
    val id: String,
    val orderId: String,
    val status: String,
    val amount: BigDecimal,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)

data class ProcessPaymentResponse(
    val paymentId: String,
    val success: Boolean,
    val message: String
)