package edu.creamcommerce.domain.order

import org.springframework.stereotype.Repository

@Repository
interface OrderRepository {
    fun save(order: Order): Order
    fun findById(id: OrderId): Order?
    fun findByUserId(userId: String): List<Order>
    fun findAll(): List<Order>
} 