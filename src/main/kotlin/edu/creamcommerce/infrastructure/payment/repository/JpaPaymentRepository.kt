package edu.creamcommerce.infrastructure.payment.repository

import edu.creamcommerce.infrastructure.payment.entity.PaymentEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface JpaPaymentRepository : JpaRepository<PaymentEntity, String> {
    fun findByOrderId(orderId: String): PaymentEntity?
} 