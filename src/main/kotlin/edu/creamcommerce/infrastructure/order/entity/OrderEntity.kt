package edu.creamcommerce.infrastructure.order.entity

import edu.creamcommerce.domain.order.OrderStatus
import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "orders")
class OrderEntity(
    @Id
    @Column(name = "id")
    var id: String = UUID.randomUUID().toString(),
    
    @Column(name = "user_id", nullable = false)
    var userId: String,
    
    @Column(name = "total_amount", nullable = false, precision = 19, scale = 2)
    var totalAmount: BigDecimal,
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    var status: OrderStatus,
    
    @Column(name = "shipping_address", nullable = true)
    var shippingAddress: String? = null,
    
    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: LocalDateTime = LocalDateTime.now(),
    
    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now()
)