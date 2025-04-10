package edu.creamcommerce.application.order.usecase

import edu.creamcommerce.domain.order.*
import edu.creamcommerce.domain.payment.Payment
import edu.creamcommerce.domain.payment.PaymentRepository
import edu.creamcommerce.domain.payment.PaymentStatus
import edu.creamcommerce.domain.point.Point
import edu.creamcommerce.domain.point.PointRepository
import edu.creamcommerce.domain.product.Product
import edu.creamcommerce.domain.product.ProductId
import edu.creamcommerce.domain.product.ProductRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class CancelOrderUseCaseTest : BehaviorSpec({
    val orderRepository = mockk<OrderRepository>()
    val paymentRepository = mockk<PaymentRepository>()
    val pointRepository = mockk<PointRepository>()
    val productRepository = mockk<ProductRepository>()
    val useCase = CancelOrderUseCase(
        orderRepository,
        paymentRepository,
        pointRepository,
        productRepository
    )
    
    given("PENDING 상태의 주문이 있을 때") {
        val orderId = OrderId.create()
        val userId = "test-user"
        
        val order = mockk<Order>().apply {
            every { id } returns orderId
            every { userId } returns userId
            every { status } returns OrderStatus.PENDING
            every { isPending() } returns true
            every { isPaid() } returns false
            every { cancel() } returns this
            every { orderItems } returns emptyList()
        }
        
        every { orderRepository.findById(orderId) } returns order
        every { orderRepository.save(any()) } returns order
        
        every { paymentRepository.findByOrderId(orderId) } returns null
        
        `when`("주문 취소 요청을 하면") {
            
            useCase(orderId)
            
            then("주문이 취소되어야 한다") {
                verify { order.cancel() }
                verify { orderRepository.save(order) }
            }
        }
    }
    
    given("PAID 상태의 주문과 결제가 있을 때") {
        val orderId = OrderId.create()
        val userId = "test-user"
        val productId = ProductId.create()
        val orderAmount = Money(2000)
        
        val orderItem = mockk<OrderItem>().apply {
            every { this@apply.productId } returns productId
            every { quantity } returns 2
        }
        
        val order = mockk<Order>().apply {
            every { id } returns orderId
            every { userId } returns userId
            every { totalAmount } returns orderAmount
            every { status } returns OrderStatus.PAID
            every { isPending() } returns false
            every { isPaid() } returns true
            every { cancel() } returns this
            every { orderItems } returns listOf(orderItem)
        }
        
        every { orderRepository.findById(orderId) } returns order
        every { orderRepository.save(any()) } returns order
        
        val payment = mockk<Payment>().apply {
            every { orderId } returns orderId
            every { amount } returns orderAmount
            every { status } returns PaymentStatus.COMPLETED
            every { isCompleted() } returns true
            every { requestRefund() } returns this
            every { refund() } returns this
        }
        
        every { paymentRepository.findByOrderId(orderId) } returns payment
        every { paymentRepository.save(any()) } returns payment
        
        val point = mockk<Point>().apply {
            every { charge(any()) } returns mockk()
        }
        
        every { pointRepository.findByUserId(userId) } returns point
        
        val product = mockk<Product>().apply {
            every { increaseStock(any()) } returns this
        }
        
        every { productRepository.findById(productId) } returns product
        every { productRepository.save(any()) } returns product
        
        `when`("주문 취소 요청을 하면") {
            
            useCase(orderId)
            
            then("주문이 취소되고 결제가 환불되어야 한다") {
                verify { payment.requestRefund() }
                verify { point.charge(orderAmount.amount) }
                verify { product.increaseStock(2) }
                verify { productRepository.save(product) }
                verify { payment.refund() }
                verify { order.cancel() }
                verify { orderRepository.save(order) }
            }
        }
    }
    
    given("주문이 존재하지 않을 때") {
        val nonExistentOrderId = OrderId.create()
        every { orderRepository.findById(nonExistentOrderId) } returns null
        
        `when`("주문 취소 요청을 하면") {
            then("예외가 발생해야 한다") {
                val exception = shouldThrow<IllegalArgumentException> {
                    useCase(nonExistentOrderId)
                }
                exception.message shouldBe "주문을 찾을 수 없습니다: ${nonExistentOrderId.value}"
            }
        }
    }
    
    given("이미 배송 중인 주문일 때") {
        val orderId = OrderId.create()
        
        val order = mockk<Order>().apply {
            every { id } returns orderId
            every { status } returns OrderStatus.SHIPPED
            every { isPending() } returns false
            every { isPaid() } returns false
        }
        
        every { orderRepository.findById(orderId) } returns order
        
        `when`("주문 취소 요청을 하면") {
            then("예외가 발생해야 한다") {
                val exception = shouldThrow<IllegalStateException> {
                    useCase(orderId)
                }
                exception.message shouldBe "취소는 PENDING 또는 PAID 상태의 주문에만 가능합니다. 현재 상태: SHIPPED"
            }
        }
    }
}) 