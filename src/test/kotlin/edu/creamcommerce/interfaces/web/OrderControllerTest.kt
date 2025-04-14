package edu.creamcommerce.interfaces.web

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import edu.creamcommerce.application.order.dto.query.OrderDto
import edu.creamcommerce.application.order.dto.query.OrderItemDto
import edu.creamcommerce.application.order.usecase.CancelOrderUseCase
import edu.creamcommerce.application.order.usecase.CreateOrderUseCase
import edu.creamcommerce.application.order.usecase.FindOrdersUseCase
import edu.creamcommerce.application.order.usecase.GetOrderByIdUseCase
import edu.creamcommerce.domain.common.Money
import edu.creamcommerce.domain.product.ProductId
import edu.creamcommerce.interfaces.web.order.CreateOrderRequest
import edu.creamcommerce.interfaces.web.order.OrderController
import edu.creamcommerce.interfaces.web.order.OrderItemRequestDto
import io.kotest.core.spec.style.ShouldSpec
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import java.math.BigDecimal
import java.time.LocalDateTime

class OrderControllerTest : ShouldSpec({
    val createOrderUseCase = mockk<CreateOrderUseCase>()
    val getOrderByIdUseCase = mockk<GetOrderByIdUseCase>()
    val cancelOrderUseCase = mockk<CancelOrderUseCase>()
    val findOrdersUseCase = mockk<FindOrdersUseCase>()
    
    val orderController = OrderController(
        createOrderUseCase = createOrderUseCase,
        getOrderByIdUseCase = getOrderByIdUseCase,
        cancelOrderUseCase = cancelOrderUseCase,
        findOrdersUseCase = findOrdersUseCase
    )
    
    val mockMvc = MockMvcBuilders.standaloneSetup(orderController).build()
    val objectMapper = ObjectMapper().apply {
        registerModule(JavaTimeModule())
        disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
    }
    
    context("주문 생성") {
        should("POST /api/orders 요청 시 주문을 생성하고 생성된 주문 정보를 반환한다") {
            // given
            val request = CreateOrderRequest(
                userId = "test-user",
                items = listOf(
                    OrderItemRequestDto(
                        productId = "prod-1",
                        quantity = 2
                    )
                ),
                shippingAddress = "서울시 강남구 테스트로 123"
            )
            
            val result = OrderDto(
                id = "order-1",
                userId = "test-user",
                status = "PAID",
                shippingAddress = "서울시 강남구 테스트로 123",
                items = listOf(
                    OrderItemDto(
                        id = "item-1",
                        productId = ProductId("prod-1"),
                        productName = "테스트 상품",
                        price = Money(BigDecimal.valueOf(10000)),
                        quantity = 2
                    )
                ),
                totalAmount = Money(BigDecimal.valueOf(20000)),
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now()
            )
            
            every { createOrderUseCase(any()) } returns result
            
            // when & then
            mockMvc.perform(
                post("/api/orders")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.data.orderId").value("order-1"))
                .andExpect(jsonPath("$.data.totalAmount").value(20000))
                .andExpect(jsonPath("$.message").value("주문이 성공적으로 생성되었습니다."))
            
            verify { createOrderUseCase(any()) }
        }
    }
    
    context("주문 조회") {
        should("GET /api/orders/{orderId} 요청 시 주문 상세 정보를 반환한다") {
            // given
            val orderId = "order-1"
            val orderResponse = createTestOrderResponseDto(orderId)
            
            every { getOrderByIdUseCase(orderId) } returns orderResponse
            
            // when & then
            mockMvc.perform(
                get("/api/orders/{orderId}", orderId)
                    .contentType(MediaType.APPLICATION_JSON)
            )
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.data.id").value(orderId))
                .andExpect(jsonPath("$.data.userId").value("test-user"))
                .andExpect(jsonPath("$.data.status").value("PAID"))
            
            verify { getOrderByIdUseCase(orderId) }
        }
    }
    
    context("주문 목록 조회") {
        should("GET /api/orders 요청 시 주문 목록을 반환한다") {
            // given
            val userId = "test-user"
            val orders = listOf(
                createTestOrderResponseDto("order-1"),
                createTestOrderResponseDto("order-2")
            )
            
            every { findOrdersUseCase(userId) } returns orders
            
            // when & then
            mockMvc.perform(
                get("/api/orders")
                    .param("userId", userId)
                    .contentType(MediaType.APPLICATION_JSON)
            )
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].id").value("order-1"))
                .andExpect(jsonPath("$.data[1].id").value("order-2"))
            
            verify { findOrdersUseCase(userId) }
        }
    }
    
    context("주문 취소") {
        should("POST /api/orders/{orderId}/cancel 요청 시 주문을 취소하고 결과를 반환한다") {
            // given
            val orderId = "order-1"
            
            justRun { cancelOrderUseCase.invoke(orderId) }
            
            // when & then
            mockMvc.perform(
                post("/api/orders/{orderId}/cancel", orderId)
                    .contentType(MediaType.APPLICATION_JSON)
            )
                .andExpect(status().isOk)
            
            verify { cancelOrderUseCase.invoke(orderId) }
        }
    }
}) {
    companion object {
        fun createTestOrderResponseDto(
            id: String,
            userId: String = "test-user",
            status: String = "PAID"
        ): OrderDto {
            return OrderDto(
                id = id,
                userId = userId,
                status = status,
                totalAmount = Money(BigDecimal.valueOf(20000)),
                shippingAddress = "서울시 강남구 테스트로 123",
                items = listOf(
                    OrderItemDto(
                        id = "item-1",
                        productId = ProductId("prod-1"),
                        productName = "테스트 상품",
                        price = Money(BigDecimal.valueOf(10000)),
                        quantity = 2
                    )
                ),
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now()
            )
        }
    }
} 