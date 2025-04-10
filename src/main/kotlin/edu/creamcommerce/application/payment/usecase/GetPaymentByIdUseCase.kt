package edu.creamcommerce.application.payment.usecase

import edu.creamcommerce.application.payment.dto.query.PaymentResponseDto
import edu.creamcommerce.domain.payment.PaymentId
import edu.creamcommerce.domain.payment.PaymentRepository
import org.springframework.stereotype.Component

@Component
class GetPaymentByIdUseCase(
    private val paymentRepository: PaymentRepository
) {
    operator fun invoke(paymentId: String): PaymentResponseDto? {
        val payment = paymentRepository.findById(PaymentId(paymentId)) ?: return null
        
        return PaymentResponseDto(
            id = payment.id.value,
            orderId = payment.orderId.value,
            status = payment.status.name,
            amount = payment.amount.amount,
            createdAt = payment.createdAt,
            updatedAt = payment.updatedAt
        )
    }
} 