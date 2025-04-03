package edu.creamcommerce.interfaces.order

import edu.creamcommerce.dto.*
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

@RestController
@RequestMapping("/api/orders")
@Tag(name = "주문 및 결제 API", description = "상품 주문 및 결제 관련 API")
class OrderController {

    private val mockOrders = mutableListOf(
        OrderDto(
            id = 1L,
            userId = 1L,
            totalAmount = BigDecimal("109.98"),
            discountAmount = BigDecimal("10.00"),
            finalAmount = BigDecimal("99.98"),
            status = "COMPLETED",
            orderItems = listOf(
                OrderItemDto(
                    id = 1L,
                    productId = 1L,
                    productName = "프리미엄 티셔츠",
                    quantity = 1,
                    price = BigDecimal("29.99")
                ),
                OrderItemDto(
                    id = 2L,
                    productId = 2L,
                    productName = "디자이너 청바지",
                    quantity = 1,
                    price = BigDecimal("79.99")
                )
            ),
            appliedCouponIds = listOf(2L),
            paymentMethod = "POINT", // WALLET에서 POINT로 변경
            shippingAddress = AddressDto(
                street = "테스트 거리 123",
                city = "서울",
                zipCode = "12345",
                country = "대한민국"
            )
        )
    )

    @PostMapping("/{id}/payment")
    @Operation(summary = "주문 결제", description = "주문에 대한 결제를 진행합니다.")
    fun processPayment(
        @PathVariable id: Long,
        @RequestBody paymentRequest: PaymentRequest
    ): ResponseEntity<PaymentResponse> {
        val order = mockOrders.find { it.id == id }

        return if (order != null) {
            // 결제 로직 (실제로는 결제 처리 서비스를 호출)
            val updatedOrder = order.copy(
                status = "PAID",
                paymentMethod = paymentRequest.paymentMethod
            )

            // 업데이트된 주문으로 대체
            val orderIndex = mockOrders.indexOfFirst { it.id == id }
            if (orderIndex >= 0) {
                mockOrders[orderIndex] = updatedOrder
            }

            ResponseEntity.ok(
                PaymentResponse(
                    orderId = id,
                    paymentId = UUID.randomUUID().toString(),
                    amount = updatedOrder.finalAmount,
                    status = "SUCCESS"
                )
            )
        } else {
            ResponseEntity.notFound().build()
        }
    }
}