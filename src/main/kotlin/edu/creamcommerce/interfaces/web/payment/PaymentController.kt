package edu.creamcommerce.interfaces.web.payment

import edu.creamcommerce.application.payment.dto.command.ProcessPaymentCommand
import edu.creamcommerce.application.payment.dto.command.ProcessPaymentResultDto
import edu.creamcommerce.application.payment.dto.query.PaymentResponseDto
import edu.creamcommerce.application.payment.usecase.GetPaymentByIdUseCase
import edu.creamcommerce.application.payment.usecase.ProcessPaymentUseCase
import edu.creamcommerce.interfaces.web.ApiResponse
import edu.creamcommerce.interfaces.web.toSuccessResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/payments")
@Tag(name = "결제 관리 API", description = "결제 처리, 조회 API")
class PaymentController(
    private val processPaymentUseCase: ProcessPaymentUseCase,
    private val getPaymentByIdUseCase: GetPaymentByIdUseCase
) {
    
    @PostMapping("/process")
    @Operation(summary = "결제 처리", description = "주문에 대한 결제를 처리합니다.")
    fun processPayment(@RequestBody request: ProcessPaymentRequest): ResponseEntity<ApiResponse<ProcessPaymentResultDto>> {
        val result = processPaymentUseCase(ProcessPaymentCommand(orderId = request.orderId))
        
        return if (result.success) {
            result.toSuccessResponse(result.message)
        } else {
            ResponseEntity.badRequest().body(ApiResponse.Companion.error(result.message))
        }
    }
    
    @GetMapping("/{paymentId}")
    @Operation(summary = "결제 조회", description = "결제 ID로 결제 정보를 조회합니다.")
    fun getPayment(@PathVariable paymentId: String): ResponseEntity<ApiResponse<PaymentResponseDto>> {
        val payment = getPaymentByIdUseCase(paymentId) ?: return ResponseEntity.notFound().build()
        return payment.toSuccessResponse()
    }
    
    @GetMapping("/order/{orderId}")
    @Operation(summary = "주문별 결제 조회", description = "주문 ID로 해당 주문의 결제 정보를 조회합니다.")
    fun getPaymentByOrderId(@PathVariable orderId: String): ResponseEntity<ApiResponse<String>> {
        // 이 부분은 해당 기능을 구현할 GetPaymentByOrderIdUseCase가 필요합니다.
        // 현재는 미구현 상태로 안내 메시지만 반환합니다.
        val message = "주문 ID $orderId 에 대한 결제 조회 기능은 아직 구현되지 않았습니다."
        return message.toSuccessResponse()
    }
}