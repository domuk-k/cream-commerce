package edu.creamcommerce.interfaces.web.order

import edu.creamcommerce.application.order.dto.query.OrderDto
import edu.creamcommerce.application.order.usecase.CancelOrderUseCase
import edu.creamcommerce.application.order.usecase.CreateOrderUseCase
import edu.creamcommerce.application.order.usecase.FindOrdersUseCase
import edu.creamcommerce.application.order.usecase.GetOrderByIdUseCase
import edu.creamcommerce.domain.order.OrderId
import edu.creamcommerce.interfaces.web.ApiResponse
import edu.creamcommerce.interfaces.web.emptySuccessResponse
import edu.creamcommerce.interfaces.web.toSuccessResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/orders")
@Tag(name = "주문 관리 API", description = "주문 생성, 조회, 취소 API")
class OrderController(
    private val createOrderUseCase: CreateOrderUseCase,
    private val getOrderByIdUseCase: GetOrderByIdUseCase,
    private val cancelOrderUseCase: CancelOrderUseCase,
    private val findOrdersUseCase: FindOrdersUseCase
) {
    
    @PostMapping
    @Operation(summary = "주문 생성", description = "새로운 주문을 생성합니다.")
    fun createOrder(@RequestBody request: CreateOrderRequest): ResponseEntity<ApiResponse<OrderDto>> {
        val result = createOrderUseCase(request.toCommand())
        return result.toSuccessResponse()
    }
    
    @GetMapping
    @Operation(summary = "주문 목록 조회", description = "주문 목록을 조회합니다. 사용자 ID로 필터링할 수 있습니다.")
    fun getOrders(@RequestParam(required = false) userId: String): ResponseEntity<ApiResponse<List<OrderDto>>> {
        val orders = findOrdersUseCase(userId)
        return orders.toSuccessResponse()
    }
    
    @GetMapping("/{orderId}")
    @Operation(summary = "주문 조회", description = "주문 ID로 주문 정보를 조회합니다.")
    fun getOrder(@PathVariable orderId: String): ResponseEntity<ApiResponse<OrderDto>> {
        val order = getOrderByIdUseCase(orderId) ?: return ResponseEntity.notFound().build()
        return order.toSuccessResponse()
    }
    
    @PostMapping("/{orderId}/cancel")
    @Operation(summary = "주문 취소", description = "주문을 취소합니다.")
    fun cancelOrder(@PathVariable orderId: OrderId): ResponseEntity<ApiResponse<Nothing>> {
        cancelOrderUseCase(orderId)
        return emptySuccessResponse()
    }
}