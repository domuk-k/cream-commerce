package edu.creamcommerce.interfaces.request

data class ProcessPaymentResponse(
    val paymentId: String,
    val success: Boolean,
    val message: String
)