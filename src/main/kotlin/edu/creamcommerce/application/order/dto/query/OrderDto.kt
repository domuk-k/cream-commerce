package edu.creamcommerce.application.order.dto.query

import edu.creamcommerce.domain.order.Money
import edu.creamcommerce.domain.order.Order
import edu.creamcommerce.domain.product.ProductId
import java.time.LocalDateTime

data class OrderDto(
    val id: String,
    val userId: String,
    val status: String,
    val totalAmount: Money,
    val shippingAddress: String,
    val items: List<OrderItemDto>,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)

data class OrderItemDto(
    val id: String,
    val productId: ProductId,
    val productName: String,
    val price: Money,
    val quantity: Int
)

fun Order.toDomain(): OrderDto {
    return OrderDto(
        id = this.id.value,
        userId = this.userId,
        items = this.orderItems.map { item ->
            OrderItemDto(
                id = item.id.value,
                productId = item.productId,
                productName = item.productName,
                quantity = item.quantity,
                price = item.price
            )
        },
        shippingAddress = this.shippingAddress,
        status = this.status.name,
        totalAmount = this.totalAmount,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt,
    )
}