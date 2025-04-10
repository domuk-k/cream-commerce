package edu.creamcommerce.domain.payment

import org.springframework.stereotype.Repository

@Repository
interface PaymentFailureRepository {
    fun save(paymentFailure: PaymentFailure): PaymentFailure
    fun findById(id: PaymentFailureId): PaymentFailure?
    fun findByPaymentId(paymentId: PaymentId): List<PaymentFailure>
} 