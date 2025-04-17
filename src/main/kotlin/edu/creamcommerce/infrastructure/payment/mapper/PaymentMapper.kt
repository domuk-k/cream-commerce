package edu.creamcommerce.infrastructure.payment.mapper

import edu.creamcommerce.domain.common.Money
import edu.creamcommerce.domain.order.OrderId
import edu.creamcommerce.domain.payment.Payment
import edu.creamcommerce.domain.payment.PaymentFailure
import edu.creamcommerce.domain.payment.PaymentFailureId
import edu.creamcommerce.domain.payment.PaymentId
import edu.creamcommerce.infrastructure.payment.entity.PaymentEntity
import edu.creamcommerce.infrastructure.payment.entity.PaymentFailureEntity
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.time.LocalDateTime

@Component
class PaymentMapper {
    // Payment 매핑
    fun toEntity(domain: Payment): PaymentEntity {
        return PaymentEntity(
            id = domain.id.value,
            orderId = domain.orderId.value,
            amount = domain.amount.amount,
            status = domain.status,
            createdAt = domain.createdAt,
            updatedAt = domain.updatedAt
        )
    }

    fun toDomain(entity: PaymentEntity): Payment {
        val payment = Payment.create(
            id = PaymentId(entity.id),
            orderId = OrderId(entity.orderId),
            amount = Money(entity.amount)
        )
        
        // 상태 동기화
        syncStatus(payment, entity)
        
        return payment
    }
    
    // PaymentFailure 매핑
    fun toEntity(domain: PaymentFailure): PaymentFailureEntity {
        return PaymentFailureEntity(
            id = domain.id.value,
            paymentId = domain.paymentId.value,
            reason = domain.reason,
            createdAt = domain.createdAt
        )
    }

    fun toDomain(entity: PaymentFailureEntity): PaymentFailure {
        return PaymentFailure.create(
            id = PaymentFailureId(entity.id),
            paymentId = PaymentId(entity.paymentId),
            reason = entity.reason
        )
    }
    
    // 상태 동기화 헬퍼 메서드
    private fun syncStatus(domain: Payment, entity: PaymentEntity) {
        if (domain.status == entity.status) return // 이미 동일한 상태면 변경 불필요
        
        // 상태별 적절한 메서드 호출
        when (entity.status) {
            edu.creamcommerce.domain.payment.PaymentStatus.READY -> {} // 기본 상태
            edu.creamcommerce.domain.payment.PaymentStatus.PROCESSING -> domain.process()
            edu.creamcommerce.domain.payment.PaymentStatus.COMPLETED -> {
                domain.process() // READY -> PROCESSING -> COMPLETED 순서를 지켜야 함
                domain.complete()
            }
            edu.creamcommerce.domain.payment.PaymentStatus.FAILED -> domain.fail("엔티티로부터 상태 동기화")
            edu.creamcommerce.domain.payment.PaymentStatus.REFUNDING -> domain.requestRefund()
            edu.creamcommerce.domain.payment.PaymentStatus.REFUNDED -> {
                domain.requestRefund() // COMPLETED -> REFUNDING -> REFUNDED 순서를 지켜야 함
                domain.refund()
            }
            edu.creamcommerce.domain.payment.PaymentStatus.CANCELED -> domain.cancel()
        }
    }
} 