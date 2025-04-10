package edu.creamcommerce.application.payment.dto.query

import java.math.BigDecimal
import java.time.LocalDateTime

data class PaymentResponseDto(
    val id: String,
    val orderId: String,
    val status: String,
    val amount: BigDecimal,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
) 