package edu.creamcommerce.domain.order

import org.springframework.stereotype.Repository

@Repository
interface OrderRepository {
    // 주문 관련
    fun save(order: Order): Order
    fun findById(id: OrderId): Order?
    fun findByUserId(userId: String): List<Order>
    fun findAll(): List<Order>
    
    // 주문 아이템 관련
    fun findOrderItemById(id: OrderItemId): OrderItem?
    fun findOrderItemsByOrderId(orderId: OrderId): List<OrderItem>
} 