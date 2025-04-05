package edu.creamcommerce.dto

import java.math.BigDecimal
import java.time.LocalDateTime

data class OrderDto(
    val id: Long? = null,
    val userId: Long,
    val totalAmount: BigDecimal,
    val discountAmount: BigDecimal = BigDecimal.ZERO,
    val finalAmount: BigDecimal,
    val status: String,
    val orderItems: List<OrderItemDto>,
    val appliedCouponIds: List<Long> = emptyList(),
    val paymentMethod: String,
    val shippingAddress: AddressDto,
    val createdAt: LocalDateTime = LocalDateTime.now()
)

data class OrderItemDto(
    val id: Long? = null,
    val productId: Long,
    val productName: String,
    val quantity: Int,
    val price: BigDecimal
)

data class AddressDto(
    val street: String,
    val city: String,
    val zipCode: String,
    val country: String
)

data class CreateOrderRequest(
    val items: List<OrderItemRequest>,
    val couponIds: List<Long> = emptyList(),
    val paymentMethod: String,
    val shippingAddress: AddressDto
)

data class OrderItemRequest(
    val productId: Long,
    val quantity: Int
)

data class PaymentRequest(
    val orderId: Long,
)

data class PaymentResponse(
    val orderId: Long,
    val paymentId: String,
    val amount: BigDecimal,
    val status: String,
    val timestamp: LocalDateTime = LocalDateTime.now()
)