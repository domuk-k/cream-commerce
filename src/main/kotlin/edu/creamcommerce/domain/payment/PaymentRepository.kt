package edu.creamcommerce.domain.payment

import edu.creamcommerce.domain.order.OrderId
import org.springframework.stereotype.Repository

@Repository
interface PaymentRepository {
    fun save(payment: Payment): Payment
    fun findById(id: PaymentId): Payment?
    fun findByOrderId(orderId: OrderId): Payment?
}