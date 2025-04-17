package edu.creamcommerce.infrastructure.order.repository

import edu.creamcommerce.infrastructure.order.entity.OrderItemEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface JpaOrderItemRepository : JpaRepository<OrderItemEntity, String> {
    fun findByOrderId(orderId: String): List<OrderItemEntity>
} 