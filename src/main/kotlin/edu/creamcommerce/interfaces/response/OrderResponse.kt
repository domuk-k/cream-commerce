package edu.creamcommerce.interfaces.response

import java.math.BigDecimal
import java.time.LocalDateTime

data class CreateOrderRequest(
    val userId: String,
    val items: List<OrderItemRequestDto>,
    val shippingAddress: String
)

data class OrderItemRequestDto(
    val productId: String,
    val quantity: Int
)

data class OrderResponse(
    val id: String,
    val userId: String,
    val status: String,
    val totalAmount: BigDecimal,
    val shippingAddress: String,
    val items: List<OrderItemResponse>,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)

data class OrderItemResponse(
    val id: String,
    val productId: String,
    val productName: String,
    val price: BigDecimal,
    val quantity: Int
) 