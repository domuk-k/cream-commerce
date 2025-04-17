package edu.creamcommerce.infrastructure.order.repository

import edu.creamcommerce.infrastructure.order.entity.OrderEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface JpaOrderRepository : JpaRepository<OrderEntity, String> {
    fun findByUserId(userId: String): List<OrderEntity>
} 