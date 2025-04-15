package edu.creamcommerce.application.order.usecase

import edu.creamcommerce.domain.order.OrderId
import edu.creamcommerce.domain.order.OrderRepository
import edu.creamcommerce.domain.payment.PaymentRepository
import edu.creamcommerce.domain.point.PointRepository
import edu.creamcommerce.domain.product.ProductRepository
import org.springframework.stereotype.Component

@Component
class CancelOrderUseCase(
    private val orderRepository: OrderRepository,
    private val paymentRepository: PaymentRepository,
    private val pointRepository: PointRepository,
    private val productRepository: ProductRepository
) {
    operator fun invoke(orderId: OrderId) {
        // 주문 조회
        val order = orderRepository.findById(orderId)
            ?: throw IllegalArgumentException("주문을 찾을 수 없습니다: ${orderId}")
        
        // 주문 상태 확인
        if (!order.isPending() && !order.isPaid()) {
            throw IllegalStateException("취소는 PENDING 또는 PAID 상태의 주문에만 가능합니다. 현재 상태: ${order.status}")
        }
        
        // 결제 정보 조회
        val payment = paymentRepository.findByOrderId(order.id)
        
        // 결제가 진행됐으면 환불 처리
        if (payment != null && payment.isCompleted()) {
            // 포인트 환불
            val point = pointRepository.findByUserId(order.userId)
                ?: throw IllegalStateException("사용자 포인트 정보를 찾을 수 없습니다: ${order.userId}")
            
            // 환불 처리
            payment.requestRefund()
            paymentRepository.save(payment)
            
            // 포인트 환불
            point.charge(payment.amount.amount)
            
            // 상품 재고 복구
            for (orderItem in order.orderItems) {
                val product = productRepository.findById(orderItem.productId)
                    ?: throw IllegalStateException("상품을 찾을 수 없습니다: ${orderItem.productId.value}")
                
                product.increaseStock(orderItem.optionId, orderItem.quantity)
                productRepository.save(product)
            }
            
            // 환불 완료
            payment.refund()
            paymentRepository.save(payment)
        }
        
        // 주문 취소 처리
        order.cancel()
        orderRepository.save(order)
        
    }
} 