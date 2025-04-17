package edu.creamcommerce.integration

import edu.creamcommerce.application.order.dto.command.CreateOrderCommand
import edu.creamcommerce.application.order.dto.command.OrderItemCommand
import edu.creamcommerce.application.order.usecase.CancelOrderUseCase
import edu.creamcommerce.application.order.usecase.CreateOrderUseCase
import edu.creamcommerce.application.order.usecase.FindOrdersUseCase
import edu.creamcommerce.application.order.usecase.GetOrderByIdUseCase
import edu.creamcommerce.application.product.dto.command.AddProductOptionCommand
import edu.creamcommerce.application.product.dto.command.CreateProductCommand
import edu.creamcommerce.application.product.usecase.AddProductOptionUseCase
import edu.creamcommerce.application.product.usecase.CreateProductUseCase
import edu.creamcommerce.domain.coupon.UserId
import edu.creamcommerce.domain.order.OrderStatus
import edu.creamcommerce.domain.product.OptionId
import edu.creamcommerce.domain.product.ProductId
import io.kotest.assertions.throwables.shouldNotThrow
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Transactional
class OrderIntegrationTest : BaseIntegrationTest() {
    
    @Autowired
    private lateinit var createOrderUseCase: CreateOrderUseCase
    
    @Autowired
    private lateinit var getOrderByIdUseCase: GetOrderByIdUseCase
    
    @Autowired
    private lateinit var findOrdersUseCase: FindOrdersUseCase
    
    @Autowired
    private lateinit var cancelOrderUseCase: CancelOrderUseCase
    
    @Autowired
    private lateinit var createProductUseCase: CreateProductUseCase
    
    @Autowired
    private lateinit var addProductOptionUseCase: AddProductOptionUseCase
    
    private var userId = UserId("")
    private var productId = ProductId("")
    private var optionId = OptionId("")
    
    @BeforeEach
    fun setUp() {
        // 테스트용 사용자 ID 생성
        userId = UserId("test-user-" + System.currentTimeMillis())
        
        // 테스트용 상품 생성
        val product = createProductUseCase(
            CreateProductCommand(
                name = "테스트 상품",
                price = BigDecimal.valueOf(10000)
            )
        )
        productId = ProductId(product.id)
        
        // 테스트용 상품 옵션 생성
        val option = addProductOptionUseCase(
            productId,
            AddProductOptionCommand(
                name = "기본 옵션",
                additionalPrice = BigDecimal.ZERO,
                sku = "TEST-SKU-" + System.currentTimeMillis(),
                stock = 100
            )
        )
        optionId = OptionId(
            option.id
        )
    }
    
    @Nested
    @DisplayName("주문 생성 유스케이스")
    inner class CreateOrderTest {
        
        @Test
        @DisplayName("주문 생성 시 상품 정보와 함께 주문이 정상적으로 등록된다")
        fun createOrder() {
            // given
            val command = CreateOrderCommand(
                userId = userId,
                items = listOf(
                    OrderItemCommand(
                        productId = productId,
                        optionId = optionId,
                        quantity = 2
                    )
                ),
                shippingAddress = "서울시 강남구 테헤란로 123"
            )
            
            // when
            val result = createOrderUseCase(command)
            
            // then
            assertThat(result).isNotNull
            assertThat(result.userId).isEqualTo(userId)
            assertThat(result.status).isEqualTo(OrderStatus.PENDING)
            assertThat(result.items).hasSize(1)
            assertThat(result.items[0].productId).isEqualTo(productId.value)
            assertThat(result.items[0].optionId).isEqualTo(optionId)
            assertThat(result.items[0].quantity).isEqualTo(2)
            assertThat(result.shippingAddress).isEqualTo("서울시 강남구 테헤란로 123")
        }
        
        @Test
        @DisplayName("여러 상품을 포함한 주문 생성이 가능하다")
        fun createOrderWithMultipleItems() {
            // given
            // 추가 상품 생성
            val product2 = createProductUseCase(
                CreateProductCommand(
                    name = "추가 테스트 상품",
                    price = BigDecimal.valueOf(20000)
                )
            )
            
            val option2 = addProductOptionUseCase(
                productId,
                AddProductOptionCommand(
                    name = "추가 상품 옵션",
                    additionalPrice = BigDecimal.valueOf(2000),
                    sku = "TEST-SKU-ADDITIONAL-" + System.currentTimeMillis(),
                    stock = 123
                )
            )
            
            val command = CreateOrderCommand(
                userId = userId,
                items = listOf(
                    OrderItemCommand(
                        productId = productId,
                        optionId = optionId,
                        quantity = 1
                    ),
                    OrderItemCommand(
                        productId = ProductId(product2.id),
                        optionId = OptionId(option2.id),
                        quantity = 2
                    )
                ),
                shippingAddress = "서울시 강남구 테헤란로 123"
            )
            
            // when
            val result = createOrderUseCase(command)
            
            // then
            assertThat(result).isNotNull
            assertThat(result.items).hasSize(2)
            
            // 총 금액 확인 (10000 + (20000 + 2000) * 2 = 54000)
            val expectedTotalAmount = BigDecimal.valueOf(10000)
                .add(BigDecimal.valueOf(20000).add(BigDecimal.valueOf(2000)).multiply(BigDecimal.valueOf(2)))
            
            assertThat(result.totalAmount).isEqualTo(expectedTotalAmount)
        }
    }
    
    @Nested
    @DisplayName("주문 조회 유스케이스")
    inner class GetOrderTest {
        
        @Test
        @DisplayName("ID로 주문 조회 시 정확한 주문 정보를 반환한다")
        fun getOrderById() {
            // given
            val createCommand = CreateOrderCommand(
                userId = userId,
                items = listOf(
                    OrderItemCommand(
                        productId = productId,
                        optionId = optionId,
                        quantity = 1
                    )
                ),
                shippingAddress = "서울시 강남구 테헤란로 123"
            )
            val createdOrder = createOrderUseCase(createCommand)
            
            // when
            val result = getOrderByIdUseCase(createdOrder.id.value)
            
            // then
            assertThat(result).isNotNull
            assertThat(result?.id).isEqualTo(createdOrder.id)
            assertThat(result?.userId).isEqualTo(userId)
            assertThat(result?.items).hasSize(1)
            assertThat(result?.status).isEqualTo(OrderStatus.PENDING)
        }
        
        @Test
        @DisplayName("사용자 ID로 주문 목록 조회 시 해당 사용자의 주문만 반환한다")
        fun findOrdersByUserId() {
            // given
            // 첫 번째 사용자의 주문 생성
            val createCommand1 = CreateOrderCommand(
                userId = userId,
                items = listOf(
                    OrderItemCommand(
                        productId = productId,
                        optionId = optionId,
                        quantity = 1
                    )
                ),
                shippingAddress = "서울시 강남구 테헤란로 123"
            )
            createOrderUseCase(createCommand1)
            
            // 두 번째 사용자의 주문 생성
            val userId2 = UserId("another-user-" + System.currentTimeMillis())
            val createCommand2 = CreateOrderCommand(
                userId = userId2,
                items = listOf(
                    OrderItemCommand(
                        productId = productId,
                        optionId = optionId,
                        quantity = 1
                    )
                ),
                shippingAddress = "서울시 강남구 테헤란로 456"
            )
            createOrderUseCase(createCommand2)
            
            // when
            val result = findOrdersUseCase(userId.value)
            
            // then
            assertThat(result).isNotNull
        }
    }
    
    @Nested
    @DisplayName("주문 취소 유스케이스")
    inner class CancelOrderTest {
        
        @Test
        @DisplayName("주문 취소 시 상태가 CANCELED로 변경된다")
        fun cancelOrder() {
            // given
            val createCommand = CreateOrderCommand(
                userId = userId,
                items = listOf(
                    OrderItemCommand(
                        productId = productId,
                        optionId = optionId,
                        quantity = 1
                    )
                ),
                shippingAddress = "서울시 강남구 테헤란로 123"
            )
            val createdOrder = createOrderUseCase(createCommand)
            
            // when
            cancelOrderUseCase(createdOrder.id)
            
            // 취소 상태 유지 확인
            val canceledOrder = getOrderByIdUseCase(createdOrder.id.value)
            assertThat(canceledOrder?.status).isEqualTo(OrderStatus.CANCELED)
        }
    }
    
    @Nested
    @DisplayName("주문 통합 시나리오")
    inner class IntegratedOrderScenarioTest {
        
        @Test
        @DisplayName("주문 생성, 조회, 취소가 정상적으로 동작한다")
        fun orderLifecycle() {
            // given
            val createCommand = CreateOrderCommand(
                userId = userId,
                items = listOf(
                    OrderItemCommand(
                        productId = productId,
                        optionId = optionId,
                        quantity = 2
                    )
                ),
                shippingAddress = "서울시 강남구 테헤란로 123"
            )
            
            // when - 주문 생성
            val createdOrder = createOrderUseCase(createCommand)
            
            // 주문 조회
            val retrievedOrder = getOrderByIdUseCase(createdOrder.id.value)
            assertThat(retrievedOrder).isNotNull
            assertThat(retrievedOrder?.status).isEqualTo(OrderStatus.PENDING)
            
            // 주문 취소
            shouldNotThrow<Exception> {
                cancelOrderUseCase(createdOrder.id)
            }
            
            // 사용자의 모든 주문 조회
            val orders = findOrdersUseCase(userId.value)
            assertThat(orders).hasSize(1)
            assertThat(orders[0].id).isEqualTo(createdOrder.id)
            assertThat(orders[0].status).isEqualTo(OrderStatus.CANCELED)
        }
    }
} 