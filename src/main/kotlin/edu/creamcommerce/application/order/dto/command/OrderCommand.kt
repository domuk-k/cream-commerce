package edu.creamcommerce.application.order.dto.command

data class CreateOrderCommand(
    val userId: String,
    val items: List<OrderItemCommand>,
    val shippingAddress: String
)

data class OrderItemCommand(
    val productId: String,
    val quantity: Int
)
