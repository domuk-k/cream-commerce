package edu.creamcommerce.interfaces.web.order

import edu.creamcommerce.application.order.dto.command.CreateOrderCommand
import edu.creamcommerce.application.order.dto.command.OrderItemCommand
import edu.creamcommerce.domain.coupon.UserId
import edu.creamcommerce.domain.order.OrderId
import edu.creamcommerce.domain.order.OrderItemId
import edu.creamcommerce.domain.product.OptionId
import edu.creamcommerce.domain.product.ProductId
import java.math.BigDecimal
import java.time.LocalDateTime

data class CreateOrderRequest(
    val userId: UserId,
    val items: List<OrderItemRequestDto>,
    val shippingAddress: String
)

fun CreateOrderRequest.toCommand(): CreateOrderCommand {
    return CreateOrderCommand(
        userId = userId,
        items = items.map {
            OrderItemCommand(productId = it.productId, optionId = it.optionId, quantity = it.quantity)
        },
        shippingAddress = shippingAddress
    )
}

data class OrderItemRequestDto(
    val productId: ProductId,
    val optionId: OptionId,
    val quantity: Int
)

data class OrderResponse(
    val id: OrderId,
    val userId: UserId,
    val status: String,
    val totalAmount: BigDecimal,
    val shippingAddress: String,
    val items: List<OrderItemResponse>,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)

data class OrderItemResponse(
    val id: OrderItemId,
    val productId: ProductId,
    val productName: String,
    val price: BigDecimal,
    val quantity: Int
) 