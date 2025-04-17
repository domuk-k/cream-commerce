package edu.creamcommerce.infrastructure.payment.entity

import edu.creamcommerce.domain.payment.PaymentStatus
import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "payments")
class PaymentEntity(
    @Id
    @Column(name = "id")
    var id: String = UUID.randomUUID().toString(),
    
    @Column(name = "order_id", nullable = false)
    var orderId: String,
    
    @Column(name = "amount", nullable = false, precision = 19, scale = 2)
    var amount: BigDecimal,
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    var status: PaymentStatus,
    
    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: LocalDateTime = LocalDateTime.now(),
    
    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now()
) 