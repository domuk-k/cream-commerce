package edu.creamcommerce.domain.payment

import edu.creamcommerce.domain.order.OrderId
import org.springframework.stereotype.Repository

@Repository
interface PaymentRepository {
    fun save(payment: Payment): Payment
    fun findById(id: PaymentId): Payment?
    fun findByOrderId(orderId: OrderId): Payment?
    
    /**
     * 특정 사용자의 완료된 결제 목록을 조회합니다.
     */
    fun findCompletedPaymentsByUserId(userId: String): List<Payment>
}