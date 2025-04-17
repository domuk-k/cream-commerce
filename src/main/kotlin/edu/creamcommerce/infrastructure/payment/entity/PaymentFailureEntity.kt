package edu.creamcommerce.infrastructure.payment.entity

import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "payment_failures")
class PaymentFailureEntity(
    @Id
    @Column(name = "id")
    var id: String = UUID.randomUUID().toString(),
    
    @Column(name = "payment_id", nullable = false)
    var paymentId: String,
    
    @Column(name = "reason", nullable = false, columnDefinition = "TEXT")
    var reason: String,
    
    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: LocalDateTime = LocalDateTime.now()
) 