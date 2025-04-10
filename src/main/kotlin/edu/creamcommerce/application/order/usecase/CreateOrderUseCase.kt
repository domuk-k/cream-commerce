package edu.creamcommerce.application.order.usecase

import edu.creamcommerce.application.order.dto.command.CreateOrderCommand
import edu.creamcommerce.application.order.dto.query.OrderDto
import edu.creamcommerce.application.order.dto.query.toDomain
import edu.creamcommerce.domain.order.Money
import edu.creamcommerce.domain.order.Order
import edu.creamcommerce.domain.order.OrderItem
import edu.creamcommerce.domain.order.OrderRepository
import edu.creamcommerce.domain.product.ProductId
import edu.creamcommerce.domain.product.ProductRepository
import org.springframework.stereotype.Component

@Component
class CreateOrderUseCase(
    private val orderRepository: OrderRepository,
    private val productRepository: ProductRepository
) {
    operator fun invoke(command: CreateOrderCommand): OrderDto {
        // 상품 유효성 확인
        val orderItems = command.items.map { item ->
            val product = productRepository.findById(ProductId(item.productId))
                ?: throw IllegalArgumentException("상품 ID가 유효하지 않습니다: ${item.productId}")
            
            if (!product.isActive()) {
                throw IllegalArgumentException("비활성화된 상품입니다: ${product.name}")
            }
            
            // 주문 항목 생성
            OrderItem.create(
                productId = product.id,
                productName = product.name,
                price = Money(product.price.amount),
                quantity = item.quantity
            )
        }
        
        // 주문 생성
        val order = Order.create(
            userId = command.userId,
            orderItems = orderItems,
            shippingAddress = command.shippingAddress
        )
        
        val savedOrder = orderRepository.save(order)
        
        return savedOrder.toDomain()
    }
} 