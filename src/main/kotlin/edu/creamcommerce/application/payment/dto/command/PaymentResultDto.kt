package edu.creamcommerce.application.payment.dto.command

data class ProcessPaymentResultDto(
    val success: Boolean,
    val paymentId: String,
    val message: String
) 