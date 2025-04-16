package edu.creamcommerce.application.order.dto.command

import edu.creamcommerce.domain.coupon.UserId
import edu.creamcommerce.domain.product.OptionId
import edu.creamcommerce.domain.product.ProductId

data class CreateOrderCommand(
    val userId: UserId,
    val items: List<OrderItemCommand>,
    val shippingAddress: String
)

data class OrderItemCommand(
    val productId: ProductId,
    val optionId: OptionId,
    val quantity: Int
)
