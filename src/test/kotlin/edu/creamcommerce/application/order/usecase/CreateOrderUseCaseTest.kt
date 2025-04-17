package edu.creamcommerce.application.order.usecase

import edu.creamcommerce.application.order.dto.command.CreateOrderCommand
import edu.creamcommerce.application.order.dto.command.OrderItemCommand
import edu.creamcommerce.domain.common.Money
import edu.creamcommerce.domain.coupon.UserId
import edu.creamcommerce.domain.order.Order
import edu.creamcommerce.domain.order.OrderRepository
import edu.creamcommerce.domain.product.OptionId
import edu.creamcommerce.domain.product.Product
import edu.creamcommerce.domain.product.ProductId
import edu.creamcommerce.domain.product.ProductRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import java.math.BigDecimal

class CreateOrderUseCaseTest : BehaviorSpec({
    val orderRepository = mockk<OrderRepository>()
    val productRepository = mockk<ProductRepository>()
    val useCase = CreateOrderUseCase(orderRepository, productRepository)
    
    given("상품이 존재하고 활성화되어 있을 때") {
        val productId = ProductId.create()
        val optionId = OptionId.create()
        val productPrice = Money(1000)
        val product = mockk<Product>().apply {
            every { id } returns productId
            every { name } returns "테스트 상품"
            every { price } returns productPrice
            every { isActive() } returns true
        }
        
        every { productRepository.findById(productId) } returns product
        
        val orderSlot = slot<Order>()
        every { orderRepository.save(capture(orderSlot)) } answers {
            orderSlot.captured
        }
        
        `when`("주문 생성 요청을 하면") {
            val userId = UserId("test-user")
            val command = CreateOrderCommand(
                userId = userId,
                items = listOf(
                    OrderItemCommand(
                        productId = productId,
                        optionId = optionId,
                        quantity = 2
                    )
                ),
                shippingAddress = "서울시 강남구 테스트로 123"
            )
            
            val result = useCase(command)
            
            then("주문이 생성되어야 한다") {
                verify { orderRepository.save(any()) }
                
                val savedOrder = orderSlot.captured
                savedOrder.userId shouldBe userId
                savedOrder.status.name shouldBe "PENDING"
                savedOrder.totalAmount.amount shouldBe BigDecimal.valueOf(2000)
                savedOrder.orderItems.size shouldBe 1
                savedOrder.orderItems[0].productId shouldBe productId
                savedOrder.orderItems[0].quantity shouldBe 2
                savedOrder.shippingAddress shouldBe "서울시 강남구 테스트로 123"
                
                result.totalAmount shouldBe Money(2000)
            }
        }
    }
    
    given("상품이 존재하지 않을 때") {
        val nonExistentProductId = ProductId.create()
        every { productRepository.findById(nonExistentProductId) } returns null
        
        `when`("주문 생성 요청을 하면") {
            val userId = UserId("test-user")
            val command = CreateOrderCommand(
                userId = userId,
                items = listOf(
                    OrderItemCommand(
                        productId = nonExistentProductId,
                        optionId = OptionId.create(),
                        quantity = 2
                    )
                ),
                shippingAddress = "서울시 강남구 테스트로 123"
            )
            
            then("예외가 발생해야 한다") {
                val exception = shouldThrow<IllegalArgumentException> {
                    useCase(command)
                }
                exception.message shouldBe "상품 ID가 유효하지 않습니다: ${nonExistentProductId.value}"
            }
        }
    }
    
    given("상품이 비활성화되어 있을 때") {
        val inactiveProductId = ProductId.create()
        val inactiveProduct = mockk<Product>().apply {
            every { id } returns inactiveProductId
            every { name } returns "비활성화된 상품"
            every { isActive() } returns false
        }
        
        every { productRepository.findById(inactiveProductId) } returns inactiveProduct
        
        `when`("주문 생성 요청을 하면") {
            val userId = UserId("test-user")
            val command = CreateOrderCommand(
                userId = userId,
                items = listOf(
                    OrderItemCommand(
                        productId = inactiveProductId,
                        optionId = OptionId.create(),
                        quantity = 2
                    )
                ),
                shippingAddress = "서울시 강남구 테스트로 123"
            )
            
            then("예외가 발생해야 한다") {
                val exception = shouldThrow<IllegalArgumentException> {
                    useCase(command)
                }
                exception.message shouldBe "비활성화된 상품입니다: 비활성화된 상품"
            }
        }
    }
}) 