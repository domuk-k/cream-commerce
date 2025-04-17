package edu.creamcommerce.integration

import edu.creamcommerce.application.order.dto.command.CreateOrderCommand
import edu.creamcommerce.application.order.dto.command.OrderItemCommand
import edu.creamcommerce.application.order.usecase.CreateOrderUseCase
import edu.creamcommerce.application.payment.dto.command.ProcessPaymentCommand
import edu.creamcommerce.application.payment.usecase.GetPaymentByIdUseCase
import edu.creamcommerce.application.payment.usecase.ProcessPaymentUseCase
import edu.creamcommerce.application.product.dto.command.AddProductOptionCommand
import edu.creamcommerce.application.product.dto.command.CreateProductCommand
import edu.creamcommerce.application.product.dto.command.UpdateProductStatusCommand
import edu.creamcommerce.application.product.usecase.AddProductOptionUseCase
import edu.creamcommerce.application.product.usecase.CreateProductUseCase
import edu.creamcommerce.application.product.usecase.GetProductByIdUseCase
import edu.creamcommerce.application.product.usecase.UpdateProductStatusUseCase
import edu.creamcommerce.domain.coupon.UserId
import edu.creamcommerce.domain.order.OrderId
import edu.creamcommerce.domain.payment.PaymentStatus
import edu.creamcommerce.domain.product.OptionId
import edu.creamcommerce.domain.product.ProductId
import edu.creamcommerce.domain.product.ProductStatus
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Transactional
class PaymentIntegrationTest : BaseIntegrationTest() {
    
    @Autowired
    private lateinit var processPaymentUseCase: ProcessPaymentUseCase
    
    @Autowired
    private lateinit var getPaymentByIdUseCase: GetPaymentByIdUseCase
    
    @Autowired
    private lateinit var createOrderUseCase: CreateOrderUseCase
    
    @Autowired
    private lateinit var createProductUseCase: CreateProductUseCase
    
    @Autowired
    private lateinit var addProductOptionUseCase: AddProductOptionUseCase
    
    @Autowired
    private lateinit var updateProductStatusUseCase: UpdateProductStatusUseCase
    
    @Autowired
    private lateinit var getProductByIdUseCase: GetProductByIdUseCase
    
    private var userId = UserId("")
    private var orderId = OrderId("")
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
        
        val option = addProductOptionUseCase(
            productId, AddProductOptionCommand(
                name = "기본 옵션",
                additionalPrice = BigDecimal.ZERO,
                sku = "TEST-SKU-" + System.currentTimeMillis(),
                stock = 100
            )
        )
        optionId = OptionId(option.id)
        
        // 옵션이 추가되었는지 확인한 후 상품 상태 변경
        val productWithOption = getProductByIdUseCase(productId)
        println("상품 옵션 개수: ${productWithOption?.options?.size}")
        
        updateProductStatusUseCase(
            productId,
            UpdateProductStatusCommand(
                ProductStatus.Active,
            )
        )
        
        val order = createOrderUseCase(
            CreateOrderCommand(
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
        )
        orderId = order.id
    }
    
    @Nested
    @DisplayName("결제 처리 유스케이스")
    inner class ProcessPaymentTest {
        
        @Test
        @DisplayName("결제 처리 성공 응답")
        fun processPayment() {
            // given
            val command = ProcessPaymentCommand(
                orderId = orderId.value,
            )
            
            // when
            val result = processPaymentUseCase(command)
            
            // then
            assertThat(result).isNotNull
            assertThat(result.success).isTrue
        }
    }
    
    @Nested
    @DisplayName("결제 조회 유스케이스")
    inner class GetPaymentTest {
        
        @Test
        @DisplayName("결제 ID로 결제 정보 조회 시 정확한 결제 정보를 반환한다")
        fun getPaymentById() {
            // given
            val command = ProcessPaymentCommand(
                orderId = orderId.value
            )
            val payment = processPaymentUseCase(command)
            
            // when
            val result = getPaymentByIdUseCase(payment.paymentId)
            
            // then
            assertThat(result).isNotNull
            assertThat(result?.id).isEqualTo(payment.paymentId)
            assertThat(result?.orderId).isEqualTo(orderId)
            assertThat(result?.amount).isEqualTo(BigDecimal.valueOf(10000))
            assertThat(result?.status).isEqualTo(PaymentStatus.COMPLETED)
        }
    }
    
    @Nested
    @DisplayName("결제 통합 시나리오")
    inner class IntegratedPaymentScenarioTest {
        
        @Test
        @DisplayName("결제 프로세스가 정상적으로 동작한다")
        fun paymentLifecycle() {
            // given - 새 주문 생성
            val newProduct = createProductUseCase(
                CreateProductCommand(
                    name = "고가 상품",
                    price = BigDecimal.valueOf(50000)
                )
            )
            
            val newOption = addProductOptionUseCase(
                productId,
                AddProductOptionCommand(
                    name = "프리미엄 옵션",
                    additionalPrice = BigDecimal.valueOf(10000),
                    sku = "PREMIUM-SKU-" + System.currentTimeMillis(),
                    stock = 2
                )
            )
            
            val newOrder = createOrderUseCase(
                CreateOrderCommand(
                    userId = userId,
                    items = listOf(
                        OrderItemCommand(
                            productId = ProductId(newProduct.id),
                            optionId = OptionId(newOption.id),
                            quantity = 2
                        )
                    ),
                    shippingAddress = "서울시 강남구 테헤란로 456"
                )
            )
            
            // 결제 처리
            val paymentCommand = ProcessPaymentCommand(
                orderId = newOrder.id.value
            )
            
            val payment = processPaymentUseCase(paymentCommand)
            
            // 결제 조회
            val retrievedPayment = getPaymentByIdUseCase(payment.paymentId)
            
            // then
            assertThat(retrievedPayment).isNotNull
            assertThat(retrievedPayment?.status).isEqualTo(PaymentStatus.COMPLETED)
            assertThat(retrievedPayment?.amount).isEqualTo(BigDecimal.valueOf(120000))
        }
        
    }
} 