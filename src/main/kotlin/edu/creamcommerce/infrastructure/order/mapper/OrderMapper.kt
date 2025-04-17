package edu.creamcommerce.infrastructure.order.mapper

import edu.creamcommerce.domain.common.Money
import edu.creamcommerce.domain.coupon.UserId
import edu.creamcommerce.domain.order.Order
import edu.creamcommerce.domain.order.OrderId
import edu.creamcommerce.domain.order.OrderItem
import edu.creamcommerce.domain.order.OrderItemId
import edu.creamcommerce.domain.product.OptionId
import edu.creamcommerce.domain.product.ProductId
import edu.creamcommerce.infrastructure.order.entity.OrderEntity
import edu.creamcommerce.infrastructure.order.entity.OrderItemEntity
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class OrderMapper {
    // Order 매핑
    fun toEntity(domain: Order): OrderEntity {
        return OrderEntity(
            id = domain.id.value,
            userId = domain.userId.value,
            totalAmount = domain.totalAmount.amount,
            status = domain.status,
            shippingAddress = domain.shippingAddress,
            createdAt = domain.createdAt,
            updatedAt = domain.updatedAt
        )
    }
    
    fun updateEntity(entity: OrderEntity, domain: Order): OrderEntity {
        entity.userId = domain.userId.value
        entity.totalAmount = domain.totalAmount.amount
        entity.status = domain.status
        entity.shippingAddress = domain.shippingAddress
        entity.updatedAt = LocalDateTime.now()
        return entity
    }
    
    fun toDomain(entity: OrderEntity, orderItems: List<OrderItem>): Order {
        return Order.create(
            id = OrderId(entity.id),
            userId = UserId(entity.userId),
            orderItems = orderItems,
            shippingAddress = entity.shippingAddress
        )
    }
    
    // OrderItem 매핑
    fun toEntity(domain: OrderItem, orderId: OrderId): OrderItemEntity {
        return OrderItemEntity(
            id = domain.id.value,
            orderId = orderId.value,
            productId = domain.productId.value,
            productName = domain.productName,
            optionId = domain.optionId.value,
            optionName = domain.optionName,
            optionSku = domain.optionSku,
            quantity = domain.quantity,
            price = domain.price.amount,
            createdAt = domain.createdAt
        )
    }
    
    fun updateEntity(entity: OrderItemEntity, domain: OrderItem): OrderItemEntity {
        entity.optionName = domain.optionName
        entity.optionSku = domain.optionSku
        entity.quantity = domain.quantity
        entity.price = domain.price.amount
        return entity
    }
    
    fun toDomain(entity: OrderItemEntity): OrderItem {
        return OrderItem.create(
            id = OrderItemId(entity.id),
            productId = ProductId(entity.productId),
            optionId = OptionId(entity.optionId),
            productName = entity.productName,
            optionName = entity.optionName,
            optionSku = entity.optionSku,
            quantity = entity.quantity,
            price = Money(entity.price),
            createdAt = entity.createdAt
        )
    }
}