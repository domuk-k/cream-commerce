package edu.creamcommerce.application.order.dto.query

import edu.creamcommerce.domain.common.Money
import edu.creamcommerce.domain.coupon.UserId
import edu.creamcommerce.domain.order.Order
import edu.creamcommerce.domain.order.OrderId
import edu.creamcommerce.domain.order.OrderItemId
import edu.creamcommerce.domain.product.OptionId
import edu.creamcommerce.domain.product.ProductId
import java.time.LocalDateTime

data class OrderDto(
    val id: OrderId,
    val userId: UserId,
    val status: String,
    val totalAmount: Money,
    val shippingAddress: String? = "",
    val items: List<OrderItemDto>,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)

data class OrderItemDto(
    val id: OrderItemId,
    val productId: ProductId,
    val optionId: OptionId,
    val productName: String,
    val optionName: String,
    val price: Money,
    val quantity: Int
)

fun Order.toDomain(): OrderDto {
    return OrderDto(
        id = this.id,
        userId = this.userId,
        items = this.orderItems.map { item ->
            OrderItemDto(
                id = item.id,
                productId = item.productId,
                productName = item.productName,
                optionId = item.optionId,
                optionName = item.optionName,
                quantity = item.quantity,
                price = item.price,
            )
        },
        shippingAddress = this.shippingAddress,
        status = this.status.name,
        totalAmount = this.totalAmount,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt,
    )
}