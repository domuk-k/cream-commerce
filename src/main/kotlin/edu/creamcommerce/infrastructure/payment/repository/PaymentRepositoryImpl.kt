package edu.creamcommerce.infrastructure.payment.repository

import edu.creamcommerce.domain.order.OrderId
import edu.creamcommerce.domain.payment.Payment
import edu.creamcommerce.domain.payment.PaymentId
import edu.creamcommerce.domain.payment.PaymentRepository
import edu.creamcommerce.infrastructure.payment.mapper.PaymentMapper
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
@Transactional(readOnly = true)
class PaymentRepositoryImpl(
    private val jpaPaymentRepository: JpaPaymentRepository,
    private val paymentMapper: PaymentMapper
) : PaymentRepository {
    
    @Transactional
    override fun save(payment: Payment): Payment {
        val entity = paymentMapper.toEntity(payment)
        val savedEntity = jpaPaymentRepository.save(entity)
        return paymentMapper.toDomain(savedEntity)
    }
    
    override fun findById(id: PaymentId): Payment? {
        return jpaPaymentRepository.findById(id.value)
            .map { paymentMapper.toDomain(it) }
            .orElse(null)
    }
    
    override fun findByOrderId(orderId: OrderId): Payment? {
        return jpaPaymentRepository.findByOrderId(orderId.value)
            ?.let { paymentMapper.toDomain(it) }
    }
} 