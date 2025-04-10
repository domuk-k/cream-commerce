package edu.creamcommerce.domain.payment

import edu.creamcommerce.domain.order.Money
import edu.creamcommerce.domain.order.OrderId
import java.time.LocalDateTime
import java.util.*

class Payment private constructor(
    val id: PaymentId,
    val orderId: OrderId,
    amount: Money,
    var status: PaymentStatus,
    val createdAt: LocalDateTime,
    var updatedAt: LocalDateTime
) {
    companion object {
        fun create(
            id: PaymentId? = null,
            orderId: OrderId,
            amount: Money
        ): Payment {
            val now = LocalDateTime.now()
            return Payment(
                id = id ?: PaymentId.create(),
                orderId = orderId,
                amount = amount,
                status = PaymentStatus.READY,
                createdAt = now,
                updatedAt = now
            )
        }
    }
    
    var amount: Money = amount
        private set
        
    fun process(): Payment {
        if (status != PaymentStatus.READY) {
            throw IllegalStateException("결제 처리는 READY 상태에서만 가능합니다. 현재 상태: $status")
        }
        
        status = PaymentStatus.PROCESSING
        updatedAt = LocalDateTime.now()
        return this
    }
    
    fun complete(): Payment {
        if (status != PaymentStatus.PROCESSING) {
            throw IllegalStateException("결제 완료는 PROCESSING 상태에서만 가능합니다. 현재 상태: $status")
        }
        
        status = PaymentStatus.COMPLETED
        updatedAt = LocalDateTime.now()
        return this
    }
    
    fun fail(reason: String = "결제 처리 실패"): PaymentFailure {
        if (status != PaymentStatus.PROCESSING && status != PaymentStatus.READY) {
            throw IllegalStateException("결제 실패 처리는 READY 또는 PROCESSING 상태에서만 가능합니다. 현재 상태: $status")
        }
        
        status = PaymentStatus.FAILED
        updatedAt = LocalDateTime.now()
        
        return PaymentFailure.create(
            paymentId = this.id,
            reason = reason
        )
    }
    
    fun requestRefund(): Payment {
        if (status != PaymentStatus.COMPLETED) {
            throw IllegalStateException("환불 요청은 COMPLETED 상태에서만 가능합니다. 현재 상태: $status")
        }
        
        status = PaymentStatus.REFUNDING
        updatedAt = LocalDateTime.now()
        return this
    }
    
    fun refund(): Payment {
        if (status != PaymentStatus.REFUNDING) {
            throw IllegalStateException("환불 처리는 REFUNDING 상태에서만 가능합니다. 현재 상태: $status")
        }
        
        status = PaymentStatus.REFUNDED
        updatedAt = LocalDateTime.now()
        return this
    }
    
    fun cancel(): Payment {
        if (status != PaymentStatus.READY && status != PaymentStatus.FAILED) {
            throw IllegalStateException("결제 취소는 READY 또는 FAILED 상태에서만 가능합니다. 현재 상태: $status")
        }
        
        status = PaymentStatus.CANCELED
        updatedAt = LocalDateTime.now()
        return this
    }
    
    fun retry(): Payment {
        if (status != PaymentStatus.FAILED) {
            throw IllegalStateException("결제 재시도는 FAILED 상태에서만 가능합니다. 현재 상태: $status")
        }
        
        status = PaymentStatus.READY
        updatedAt = LocalDateTime.now()
        return this
    }
    
    fun isReady(): Boolean = status == PaymentStatus.READY
    fun isProcessing(): Boolean = status == PaymentStatus.PROCESSING
    fun isCompleted(): Boolean = status == PaymentStatus.COMPLETED
    fun isFailed(): Boolean = status == PaymentStatus.FAILED
    fun isRefunding(): Boolean = status == PaymentStatus.REFUNDING
    fun isRefunded(): Boolean = status == PaymentStatus.REFUNDED
    fun isCanceled(): Boolean = status == PaymentStatus.CANCELED
}

@JvmInline
value class PaymentId(val value: String) {
    companion object {
        fun create(): PaymentId = PaymentId(UUID.randomUUID().toString())
    }
}

enum class PaymentStatus {
    READY,       // 결제 준비
    PROCESSING,  // 결제 처리 중
    COMPLETED,   // 결제 완료
    FAILED,      // 결제 실패
    REFUNDING,   // 환불 처리 중
    REFUNDED,    // 환불 완료
    CANCELED     // 취소됨
} 