package edu.creamcommerce.application.payment.usecase

import edu.creamcommerce.application.payment.dto.command.ProcessPaymentCommand
import edu.creamcommerce.domain.common.Money
import edu.creamcommerce.domain.order.*
import edu.creamcommerce.domain.payment.*
import edu.creamcommerce.domain.point.Point
import edu.creamcommerce.domain.point.PointRepository
import edu.creamcommerce.domain.product.Product
import edu.creamcommerce.domain.product.ProductId
import edu.creamcommerce.domain.product.ProductRepository
import io.mockk.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.util.*

class ProcessPaymentUseCaseTest {
    
    @Test
    fun `주문이 존재하지 않을 때 예외가 발생해야 한다`() {
        // given
        val orderRepository = mockk<OrderRepository>()
        val paymentRepository = mockk<PaymentRepository>()
        val paymentFailureRepository = mockk<PaymentFailureRepository>()
        val pointRepository = mockk<PointRepository>()
        val productRepository = mockk<ProductRepository>()
        
        val useCase = ProcessPaymentUseCase(
            orderRepository,
            paymentRepository,
            paymentFailureRepository,
            pointRepository,
            productRepository
        )
        
        val nonExistentOrderId = OrderId(UUID.randomUUID().toString())
        every { orderRepository.findById(nonExistentOrderId) } returns null
        
        // when/then
        val command = ProcessPaymentCommand(nonExistentOrderId.value)
        val exception = assertThrows(IllegalArgumentException::class.java) {
            useCase(command)
        }
        
        assertEquals("주문을 찾을 수 없습니다: ${nonExistentOrderId.value}", exception.message)
    }
    
    @Test
    fun `주문이 PENDING 상태가 아닐 때 예외가 발생해야 한다`() {
        // given
        val orderRepository = mockk<OrderRepository>()
        val paymentRepository = mockk<PaymentRepository>()
        val paymentFailureRepository = mockk<PaymentFailureRepository>()
        val pointRepository = mockk<PointRepository>()
        val productRepository = mockk<ProductRepository>()
        
        val useCase = ProcessPaymentUseCase(
            orderRepository,
            paymentRepository,
            paymentFailureRepository,
            pointRepository,
            productRepository
        )
        
        val orderId = OrderId(UUID.randomUUID().toString())
        val order = mockk<Order>()
        every { order.id } returns orderId
        every { order.status } returns OrderStatus.PAID
        every { order.isPending() } returns false
        
        every { orderRepository.findById(orderId) } returns order
        
        // when/then
        val command = ProcessPaymentCommand(orderId.value)
        val exception = assertThrows(IllegalStateException::class.java) {
            useCase(command)
        }
        
        assertEquals("결제는 PENDING 상태의 주문에만 가능합니다. 현재 상태: PAID", exception.message)
    }
    
    @Test
    fun `이미 진행 중인 결제가 있을 때 예외가 발생해야 한다`() {
        // given
        val orderRepository = mockk<OrderRepository>()
        val paymentRepository = mockk<PaymentRepository>()
        val paymentFailureRepository = mockk<PaymentFailureRepository>()
        val pointRepository = mockk<PointRepository>()
        val productRepository = mockk<ProductRepository>()
        
        val useCase = ProcessPaymentUseCase(
            orderRepository,
            paymentRepository,
            paymentFailureRepository,
            pointRepository,
            productRepository
        )
        
        val orderId = OrderId(UUID.randomUUID().toString())
        val order = mockk<Order>()
        every { order.id } returns orderId
        every { order.status } returns OrderStatus.PENDING
        every { order.isPending() } returns true
        
        every { orderRepository.findById(orderId) } returns order
        
        val existingPayment = mockk<Payment>()
        every { existingPayment.isFailed() } returns false
        every { existingPayment.isCanceled() } returns false
        
        every { paymentRepository.findByOrderId(orderId) } returns existingPayment
        
        // when/then
        val command = ProcessPaymentCommand(orderId.value)
        val exception = assertThrows(IllegalStateException::class.java) {
            useCase(command)
        }
        
        assertEquals("이미 진행 중인 결제가 있습니다.", exception.message)
    }
    
    @Test
    fun `포인트가 부족할 때 결제가 실패해야 한다`() {
        // given
        val orderRepository = mockk<OrderRepository>()
        val paymentRepository = mockk<PaymentRepository>()
        val paymentFailureRepository = mockk<PaymentFailureRepository>()
        val pointRepository = mockk<PointRepository>()
        val productRepository = mockk<ProductRepository>()
        
        val useCase = ProcessPaymentUseCase(
            orderRepository,
            paymentRepository,
            paymentFailureRepository,
            pointRepository,
            productRepository
        )
        
        val orderId = OrderId(UUID.randomUUID().toString())
        val userId = "test-user"
        val orderAmount = Money(5000)
        
        val orderItem = mockk<OrderItem>()
        every { orderItem.productId } returns ProductId(UUID.randomUUID().toString())
        every { orderItem.quantity } returns 2
        every { orderItem.productName } returns "테스트 상품"
        
        val order = mockk<Order>()
        every { order.id } returns orderId
        every { order.userId } returns userId
        every { order.status } returns OrderStatus.PENDING
        every { order.totalAmount } returns orderAmount
        every { order.isPending() } returns true
        every { order.orderItems } returns listOf(orderItem)
        
        every { orderRepository.findById(orderId) } returns order
        
        every { paymentRepository.findByOrderId(orderId) } returns null
        
        val payment = mockk<Payment>(relaxed = true)
        every { payment.id } returns PaymentId(UUID.randomUUID().toString())
        every { payment.amount } returns orderAmount
        
        val paymentSlot = slot<Payment>()
        every { paymentRepository.save(capture(paymentSlot)) } returns payment
        
        val failureSlot = slot<PaymentFailure>()
        every { paymentFailureRepository.save(capture(failureSlot)) } returns mockk()
        
        val point = mockk<Point>()
        every { point.amount } returns BigDecimal.valueOf(1000) // 주문 금액보다 적은 포인트
        
        every { pointRepository.findByUserId(userId) } returns point
        
        // when
        val command = ProcessPaymentCommand(orderId.value)
        val result = useCase(command)
        
        // then
        assertFalse(result.success)
        assertEquals("포인트 잔액이 부족합니다.", result.message)
    }
    
    @Test
    fun `상품 재고가 부족할 때 결제가 실패해야 한다`() {
        // given
        val orderRepository = mockk<OrderRepository>()
        val paymentRepository = mockk<PaymentRepository>()
        val paymentFailureRepository = mockk<PaymentFailureRepository>()
        val pointRepository = mockk<PointRepository>()
        val productRepository = mockk<ProductRepository>()
        
        val useCase = ProcessPaymentUseCase(
            orderRepository,
            paymentRepository,
            paymentFailureRepository,
            pointRepository,
            productRepository
        )
        
        val orderId = OrderId(UUID.randomUUID().toString())
        val userId = "test-user"
        val productId = ProductId(UUID.randomUUID().toString())
        val orderAmount = Money(2000)
        
        val orderItem = mockk<OrderItem>()
        every { orderItem.productId } returns productId
        every { orderItem.quantity } returns 2
        every { orderItem.productName } returns "테스트 상품"
        
        val order = mockk<Order>()
        every { order.id } returns orderId
        every { order.userId } returns userId
        every { order.status } returns OrderStatus.PENDING
        every { order.totalAmount } returns orderAmount
        every { order.isPending() } returns true
        every { order.orderItems } returns listOf(orderItem)
        
        every { orderRepository.findById(orderId) } returns order
        
        every { paymentRepository.findByOrderId(orderId) } returns null
        
        val payment = mockk<Payment>(relaxed = true)
        every { payment.id } returns PaymentId(UUID.randomUUID().toString())
        every { payment.amount } returns orderAmount
        
        val paymentSlot = slot<Payment>()
        every { paymentRepository.save(capture(paymentSlot)) } returns payment
        
        val failureSlot = slot<PaymentFailure>()
        every { paymentFailureRepository.save(capture(failureSlot)) } returns mockk()
        
        val point = mockk<Point>()
        every { point.amount } returns BigDecimal.valueOf(5000) // 충분한 포인트
        
        every { pointRepository.findByUserId(userId) } returns point
        
        val product = mockk<Product>()
        every { product.hasEnoughStock(any()) } returns false
        
        every { productRepository.findById(productId) } returns product
        
        // when
        val command = ProcessPaymentCommand(orderId.value)
        val result = useCase(command)
        
        // then
        assertFalse(result.success)
        assertEquals("상품 재고가 부족합니다: 테스트 상품", result.message)
    }
    
    @Test
    fun `유효한 주문이 존재하고 포인트가 충분하고 상품 재고가 충분할 때 결제가 성공적으로 처리되어야 한다`() {
        // given
        val orderRepository = mockk<OrderRepository>()
        val paymentRepository = mockk<PaymentRepository>()
        val paymentFailureRepository = mockk<PaymentFailureRepository>()
        val pointRepository = mockk<PointRepository>()
        val productRepository = mockk<ProductRepository>()
        
        val useCase = ProcessPaymentUseCase(
            orderRepository,
            paymentRepository,
            paymentFailureRepository,
            pointRepository,
            productRepository
        )
        
        val orderId = OrderId(UUID.randomUUID().toString())
        val userId = "test-user"
        val productId = ProductId(UUID.randomUUID().toString())
        val orderAmount = Money(2000)
        
        val orderItem = mockk<OrderItem>()
        every { orderItem.productId } returns productId
        every { orderItem.quantity } returns 2
        every { orderItem.productName } returns "테스트 상품"
        
        val order = mockk<Order>()
        every { order.id } returns orderId
        every { order.userId } returns userId
        every { order.status } returns OrderStatus.PENDING
        every { order.totalAmount } returns orderAmount
        every { order.isPending() } returns true
        every { order.pay() } returns order
        every { order.orderItems } returns listOf(orderItem)
        
        every { orderRepository.findById(orderId) } returns order
        every { orderRepository.save(any()) } returns order
        
        every { paymentRepository.findByOrderId(orderId) } returns null
        
        // Payment 생성 모킹
        val payment = mockk<Payment>()
        val paymentId = PaymentId(UUID.randomUUID().toString())
        every { payment.id } returns paymentId
        every { payment.orderId } returns orderId
        every { payment.amount } returns orderAmount
        every { payment.process() } returns payment
        every { payment.complete() } returns payment
        
        // Payment.create 정적 메서드 모킹
        mockkStatic(Payment::class)
        every { Payment.create(orderId = orderId, amount = orderAmount) } returns payment
        
        val paymentSlot = slot<Payment>()
        every { paymentRepository.save(capture(paymentSlot)) } returns payment
        
        val point = mockk<Point>()
        every { point.amount } returns BigDecimal.valueOf(5000) // 충분한 포인트
        every { point.use(any()) } returns mockk()
        
        every { pointRepository.findByUserId(userId) } returns point
        
        val product = mockk<Product>()
        every { product.hasEnoughStock(any()) } returns true
        every { product.decreaseStock(any()) } returns product
        
        every { productRepository.findById(productId) } returns product
        every { productRepository.save(any()) } returns product
        
        // when
        val command = ProcessPaymentCommand(orderId.value)
        val result = useCase(command)
        
        // then
        assertTrue(result.success)
        assertEquals("결제가 완료되었습니다.", result.message)
        
        verify { order.pay() }
        verify { orderRepository.save(order) }
        verify { payment.process() }
        verify { payment.complete() }
        verify { point.use(orderAmount.amount) }
        verify { product.hasEnoughStock(2) }
        verify { product.decreaseStock(2) }
        verify { productRepository.save(product) }
    }
} 