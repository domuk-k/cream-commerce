package edu.creamcommerce.domain.order

import edu.creamcommerce.domain.common.Money
import edu.creamcommerce.domain.coupon.UserId
import java.time.LocalDateTime
import java.util.*

class Order private constructor(
    val id: OrderId,
    val userId: UserId,
    var status: OrderStatus,
    val totalAmount: Money,
    private val _orderItems: MutableList<OrderItem>,
    val shippingAddress: String,
    val createdAt: LocalDateTime,
    var updatedAt: LocalDateTime
) {
    companion object {
        fun create(
            id: OrderId? = null,
            userId: UserId,
            orderItems: List<OrderItem>,
            shippingAddress: String
        ): Order {
            val now = LocalDateTime.now()
            val totalAmount = orderItems.fold(Money.ZERO) { acc, item -> acc + item.price * item.quantity }
            
            return Order(
                id = id ?: OrderId.create(),
                userId = userId,
                status = OrderStatus.PENDING,
                totalAmount = totalAmount,
                _orderItems = orderItems.toMutableList(),
                shippingAddress = shippingAddress,
                createdAt = now,
                updatedAt = now
            )
        }
    }
    
    val orderItems: List<OrderItem> get() = _orderItems.toList()
    
    fun pay(): Order {
        if (status != OrderStatus.PENDING) {
            throw IllegalStateException("결제는 PENDING 상태에서만 가능합니다. 현재 상태: $status")
        }
        
        status = OrderStatus.PAID
        updatedAt = LocalDateTime.now()
        return this
    }
    
    fun cancel(): Order {
        if (status != OrderStatus.PENDING && status != OrderStatus.PAID) {
            throw IllegalStateException("취소는 PENDING 또는 PAID 상태에서만 가능합니다. 현재 상태: $status")
        }
        
        status = OrderStatus.CANCELED
        updatedAt = LocalDateTime.now()
        return this
    }
    
    fun processShipping(): Order {
        if (status != OrderStatus.PAID) {
            throw IllegalStateException("배송 처리는 PAID 상태에서만 가능합니다. 현재 상태: $status")
        }
        
        status = OrderStatus.PROCESSING
        updatedAt = LocalDateTime.now()
        return this
    }
    
    fun ship(): Order {
        if (status != OrderStatus.PROCESSING) {
            throw IllegalStateException("배송 시작은 PROCESSING 상태에서만 가능합니다. 현재 상태: $status")
        }
        
        status = OrderStatus.SHIPPED
        updatedAt = LocalDateTime.now()
        return this
    }
    
    fun deliver(): Order {
        if (status != OrderStatus.SHIPPED) {
            throw IllegalStateException("배송 완료는 SHIPPED 상태에서만 가능합니다. 현재 상태: $status")
        }
        
        status = OrderStatus.DELIVERED
        updatedAt = LocalDateTime.now()
        return this
    }
    
    fun complete(): Order {
        if (status != OrderStatus.DELIVERED) {
            throw IllegalStateException("주문 완료는 DELIVERED 상태에서만 가능합니다. 현재 상태: $status")
        }
        
        status = OrderStatus.COMPLETED
        updatedAt = LocalDateTime.now()
        return this
    }
    
    fun requestRefund(): Order {
        if (status != OrderStatus.PAID && status != OrderStatus.PROCESSING && status != OrderStatus.SHIPPED) {
            throw IllegalStateException("환불 요청은 PAID, PROCESSING, SHIPPED 상태에서만 가능합니다. 현재 상태: $status")
        }
        
        status = OrderStatus.REFUNDING
        updatedAt = LocalDateTime.now()
        return this
    }
    
    fun refund(): Order {
        if (status != OrderStatus.REFUNDING) {
            throw IllegalStateException("환불 처리는 REFUNDING 상태에서만 가능합니다. 현재 상태: $status")
        }
        
        status = OrderStatus.REFUNDED
        updatedAt = LocalDateTime.now()
        return this
    }
    
    fun isPending(): Boolean = status == OrderStatus.PENDING
    fun isPaid(): Boolean = status == OrderStatus.PAID
    fun isShipped(): Boolean = status == OrderStatus.SHIPPED
    fun isCompleted(): Boolean = status == OrderStatus.COMPLETED
    fun isCanceled(): Boolean = status == OrderStatus.CANCELED
    fun isRefunded(): Boolean = status == OrderStatus.REFUNDED
}

@JvmInline
value class OrderId(val value: String) {
    companion object {
        fun create(): OrderId = OrderId(UUID.randomUUID().toString())
    }
}

enum class OrderStatus {
    PENDING,     // 결제 대기
    PAID,        // 결제 완료
    PROCESSING,  // 주문 처리 중
    SHIPPED,     // 배송 시작
    DELIVERED,   // 배송 완료
    COMPLETED,   // 주문 완료
    REFUNDING,   // 환불 중
    REFUNDED,    // 환불 완료
    CANCELED     // 취소됨
} 