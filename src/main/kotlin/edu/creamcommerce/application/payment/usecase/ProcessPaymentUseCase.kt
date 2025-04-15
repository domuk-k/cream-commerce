package edu.creamcommerce.application.payment.usecase

import edu.creamcommerce.application.payment.dto.command.ProcessPaymentCommand
import edu.creamcommerce.application.payment.dto.command.ProcessPaymentResultDto
import edu.creamcommerce.domain.order.OrderId
import edu.creamcommerce.domain.order.OrderRepository
import edu.creamcommerce.domain.payment.Payment
import edu.creamcommerce.domain.payment.PaymentFailureRepository
import edu.creamcommerce.domain.payment.PaymentRepository
import edu.creamcommerce.domain.point.PointRepository
import edu.creamcommerce.domain.product.ProductRepository
import org.springframework.stereotype.Component

@Component
class ProcessPaymentUseCase(
    private val orderRepository: OrderRepository,
    private val paymentRepository: PaymentRepository,
    private val paymentFailureRepository: PaymentFailureRepository,
    private val pointRepository: PointRepository,
    private val productRepository: ProductRepository
) {
    operator fun invoke(command: ProcessPaymentCommand): ProcessPaymentResultDto {
        // 주문 조회
        val order = orderRepository.findById(OrderId(command.orderId))
            ?: throw IllegalArgumentException("주문을 찾을 수 없습니다: ${command.orderId}")
        
        // 주문 상태 확인
        if (!order.isPending()) {
            throw IllegalStateException("결제는 PENDING 상태의 주문에만 가능합니다. 현재 상태: ${order.status}")
        }
        
        // 이미 결제가 있는지 확인
        val existingPayment = paymentRepository.findByOrderId(order.id)
        if (existingPayment != null && !existingPayment.isFailed() && !existingPayment.isCanceled()) {
            throw IllegalStateException("이미 진행 중인 결제가 있습니다.")
        }
        
        // 결제 생성
        val payment = Payment.create(
            orderId = order.id,
            amount = order.totalAmount
        )
        
        // 결제 처리 시작
        try {
            // 포인트 잔액 확인
            val point = pointRepository.findByUserId(order.userId)
                ?: throw IllegalStateException("사용자 포인트 정보를 찾을 수 없습니다: ${order.userId}")
            
            if (point.amount < payment.amount.amount) {
                val failure = payment.fail("포인트 잔액이 부족합니다.")
                paymentRepository.save(payment)
                paymentFailureRepository.save(failure)
                
                return ProcessPaymentResultDto(
                    success = false,
                    paymentId = payment.id.value,
                    message = "포인트 잔액이 부족합니다."
                )
            }
            
            // 상품 재고 확인 및 예약
            for (orderItem in order.orderItems) {
                val product = productRepository.findById(orderItem.productId)
                    ?: throw IllegalStateException("상품을 찾을 수 없습니다: ${orderItem.productId.value}")
                
                // 상품 옵션 찾기
                val option = product.options.find { it.id == orderItem.optionId }
                    ?: throw IllegalStateException("상품 옵션을 찾을 수 없습니다: ${orderItem.optionId.value}")
                
                // 재고 확인
                if (!option.hasEnoughStock(orderItem.quantity)) {
                    val failure = payment.fail("상품 옵션 재고가 부족합니다: ${orderItem.productName} - ${option.name}")
                    paymentRepository.save(payment)
                    paymentFailureRepository.save(failure)
                    
                    return ProcessPaymentResultDto(
                        success = false,
                        paymentId = payment.id.value,
                        message = "상품 옵션 재고가 부족합니다: ${orderItem.productName} - ${option.name}"
                    )
                }
            }
            
            // 결제 진행 상태로 변경
            payment.process()
            paymentRepository.save(payment)
            
            // 포인트 차감
            point.use(payment.amount.amount)
            
            // 상품 재고 차감
            for (orderItem in order.orderItems) {
                val product = productRepository.findById(orderItem.productId)!!
                
                // 상품 옵션 찾기
                val option = product.options.find { it.id == orderItem.optionId }!!
                
                // 옵션 재고 차감
                option.decreaseStock(orderItem.quantity)
                
                productRepository.save(product)
            }
            
            // 결제 완료 처리
            payment.complete()
            paymentRepository.save(payment)
            
            // 주문 상태 변경
            order.pay()
            orderRepository.save(order)
            
            return ProcessPaymentResultDto(
                success = true,
                paymentId = payment.id.value,
                message = "결제가 완료되었습니다."
            )
            
        } catch (e: Exception) {
            // 결제 실패 처리
            val failure = payment.fail(e.message ?: "결제 중 오류가 발생했습니다.")
            paymentRepository.save(payment)
            paymentFailureRepository.save(failure)
            
            return ProcessPaymentResultDto(
                success = false,
                paymentId = payment.id.value,
                message = e.message ?: "결제 중 오류가 발생했습니다."
            )
        }
    }
} 