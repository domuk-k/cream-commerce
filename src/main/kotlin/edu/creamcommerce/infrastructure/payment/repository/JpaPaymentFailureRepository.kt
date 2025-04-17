package edu.creamcommerce.infrastructure.payment.repository

import edu.creamcommerce.infrastructure.payment.entity.PaymentFailureEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface JpaPaymentFailureRepository : JpaRepository<PaymentFailureEntity, String> {
    fun findByPaymentId(paymentId: String): List<PaymentFailureEntity>
} 