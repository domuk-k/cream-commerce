package edu.creamcommerce.application.order.usecase

import edu.creamcommerce.application.order.dto.query.OrderDto
import edu.creamcommerce.application.order.dto.query.OrderItemDto
import edu.creamcommerce.domain.order.OrderId
import edu.creamcommerce.domain.order.OrderRepository
import org.springframework.stereotype.Component

@Component
class GetOrderByIdUseCase(
    private val orderRepository: OrderRepository
) {
    operator fun invoke(orderId: String): OrderDto? {
        val order = orderRepository.findById(OrderId(orderId)) ?: return null
        
        return OrderDto(
            id = order.id,
            userId = order.userId,
            status = order.status.name,
            totalAmount = order.totalAmount,
            shippingAddress = order.shippingAddress,
            items = order.orderItems.map { item ->
                OrderItemDto(
                    id = item.id,
                    productId = item.productId,
                    optionId = item.optionId,
                    productName = item.productName,
                    optionName = item.optionName,
                    price = item.price,
                    quantity = item.quantity
                )
            },
            createdAt = order.createdAt,
            updatedAt = order.updatedAt
        )
    }
} 