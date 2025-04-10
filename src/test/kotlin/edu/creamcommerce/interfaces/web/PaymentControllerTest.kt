package edu.creamcommerce.interfaces.web

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import edu.creamcommerce.application.payment.dto.command.ProcessPaymentCommand
import edu.creamcommerce.application.payment.dto.command.ProcessPaymentResultDto
import edu.creamcommerce.application.payment.dto.query.PaymentResponseDto
import edu.creamcommerce.application.payment.usecase.GetPaymentByIdUseCase
import edu.creamcommerce.application.payment.usecase.ProcessPaymentUseCase
import edu.creamcommerce.interfaces.response.ProcessPaymentRequest
import io.kotest.core.spec.style.ShouldSpec
import io.mockk.every
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

class PaymentControllerTest : ShouldSpec({
    val processPaymentUseCase = mockk<ProcessPaymentUseCase>()
    val getPaymentByIdUseCase = mockk<GetPaymentByIdUseCase>()
    
    val paymentController = PaymentController(
        processPaymentUseCase = processPaymentUseCase,
        getPaymentByIdUseCase = getPaymentByIdUseCase
    )
    
    val mockMvc = MockMvcBuilders.standaloneSetup(paymentController).build()
    val objectMapper = ObjectMapper().apply {
        registerModule(JavaTimeModule())
        disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
    }
    
    context("결제 처리") {
        should("POST /api/payments/process 요청 시 결제를 처리하고 결과를 반환한다") {
            // given
            val request = ProcessPaymentRequest(
                orderId = "order-1"
            )
            
            val result = ProcessPaymentResultDto(
                success = true,
                paymentId = "payment-1",
                message = "결제가 완료되었습니다."
            )
            
            every { processPaymentUseCase(ProcessPaymentCommand("order-1")) } returns result
            
            // when & then
            mockMvc.perform(
                post("/api/payments/process")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.data.success").value(true))
                .andExpect(jsonPath("$.data.paymentId").value("payment-1"))
                .andExpect(jsonPath("$.message").value("결제가 완료되었습니다."))
            
            verify { processPaymentUseCase(ProcessPaymentCommand("order-1")) }
        }
        
        should("결제 실패 시 에러 메시지를 반환한다") {
            // given
            val request = ProcessPaymentRequest(
                orderId = "order-1"
            )
            
            val result = ProcessPaymentResultDto(
                success = false,
                paymentId = "payment-1",
                message = "포인트 잔액이 부족합니다."
            )
            
            every { processPaymentUseCase(ProcessPaymentCommand("order-1")) } returns result
            
            // when & then
            mockMvc.perform(
                post("/api/payments/process")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )
                .andExpect(status().isBadRequest)
                .andExpect(jsonPath("$.error").value("포인트 잔액이 부족합니다."))
            
            verify { processPaymentUseCase(ProcessPaymentCommand("order-1")) }
        }
    }
    
    context("결제 조회") {
        should("GET /api/payments/{paymentId} 요청 시 결제 정보를 반환한다") {
            // given
            val paymentId = "payment-1"
            val paymentResponse = createTestPaymentResponseDto(paymentId)
            
            every { getPaymentByIdUseCase(paymentId) } returns paymentResponse
            
            // when & then
            mockMvc.perform(
                get("/api/payments/{paymentId}", paymentId)
                    .contentType(MediaType.APPLICATION_JSON)
            )
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.data.id").value(paymentId))
                .andExpect(jsonPath("$.data.orderId").value("order-1"))
                .andExpect(jsonPath("$.data.status").value("COMPLETED"))
            
            verify { getPaymentByIdUseCase(paymentId) }
        }
    }
}) {
    companion object {
        fun createTestPaymentResponseDto(
            id: String,
            orderId: String = "order-1",
            status: String = "COMPLETED"
        ): PaymentResponseDto {
            return PaymentResponseDto(
                id = id,
                orderId = orderId,
                status = status,
                amount = BigDecimal.valueOf(20000),
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now()
            )
        }
    }
} 