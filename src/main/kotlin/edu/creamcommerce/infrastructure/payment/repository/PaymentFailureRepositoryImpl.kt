package edu.creamcommerce.infrastructure.payment.repository

import edu.creamcommerce.domain.payment.PaymentFailure
import edu.creamcommerce.domain.payment.PaymentFailureId
import edu.creamcommerce.domain.payment.PaymentFailureRepository
import edu.creamcommerce.domain.payment.PaymentId
import edu.creamcommerce.infrastructure.payment.mapper.PaymentMapper
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
@Transactional(readOnly = true)
class PaymentFailureRepositoryImpl(
    private val jpaPaymentFailureRepository: JpaPaymentFailureRepository,
    private val paymentMapper: PaymentMapper
) : PaymentFailureRepository {
    
    @Transactional
    override fun save(paymentFailure: PaymentFailure): PaymentFailure {
        val entity = paymentMapper.toEntity(paymentFailure)
        val savedEntity = jpaPaymentFailureRepository.save(entity)
        return paymentMapper.toDomain(savedEntity)
    }
    
    override fun findById(id: PaymentFailureId): PaymentFailure? {
        return jpaPaymentFailureRepository.findById(id.value)
            .map { paymentMapper.toDomain(it) }
            .orElse(null)
    }
    
    override fun findByPaymentId(paymentId: PaymentId): List<PaymentFailure> {
        return jpaPaymentFailureRepository.findByPaymentId(paymentId.value)
            .map { paymentMapper.toDomain(it) }
    }
} 