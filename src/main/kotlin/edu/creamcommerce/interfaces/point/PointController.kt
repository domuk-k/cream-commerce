package edu.creamcommerce.interfaces.point

import edu.creamcommerce.dto.PointBalanceResponse
import edu.creamcommerce.dto.PointChargeRequest
import edu.creamcommerce.dto.PointChargeResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

@RestController
@RequestMapping("/api/points")
@Tag(name = "포인트 관리 API", description = "사용자 포인트 충전 및 조회 API")
class PointController {

    // Mock 포인트 데이터 (사용자 ID -> 포인트)
    private val mockPointBalances = mutableMapOf<Long, BigDecimal>(
        1L to BigDecimal("10000.00"),
        2L to BigDecimal("5000.00")
    )

    // Mock 트랜잭션 기록
    private val mockTransactions = mutableListOf<PointChargeResponse>()

    @GetMapping("/balance")
    @Operation(summary = "포인트 조회", description = "현재 사용자의 포인트를 조회합니다.")
    fun getBalance(): ResponseEntity<PointBalanceResponse> {
        val userId = 1L // 보통은 인증된 사용자 ID
        val balance = mockPointBalances[userId] ?: BigDecimal.ZERO

        return ResponseEntity.ok(
            PointBalanceResponse(
                pointId = userId,
                userId = userId,
                balance = balance,
                lastUpdated = LocalDateTime.now()
            )
        )
    }

    @PostMapping("/charge")
    @Operation(summary = "포인트 충전", description = "사용자 포인트를 충전합니다.")
    fun chargePoint(@RequestBody request: PointChargeRequest): ResponseEntity<PointChargeResponse> {
        val userId = 1L // 보통은 인증된 사용자 ID
        val currentBalance = mockPointBalances[userId] ?: BigDecimal.ZERO
        val newBalance = currentBalance.add(request.amount)

        mockPointBalances[userId] = newBalance

        val response = PointChargeResponse(
            pointId = userId,
            userId = userId,
            previousBalance = currentBalance,
            chargedAmount = request.amount,
            currentBalance = newBalance,
            transactionId = UUID.randomUUID().toString()
        )

        mockTransactions.add(response)

        return ResponseEntity.ok(response)
    }

    @GetMapping("/transactions")
    @Operation(summary = "충전 내역 조회", description = "사용자의 포인트 충전 내역을 조회합니다.")
    fun getTransactionHistory(): ResponseEntity<List<PointChargeResponse>> {
        val userId = 1L // 보통은 인증된 사용자 ID
        val userTransactions = mockTransactions.filter { it.userId == userId }

        return ResponseEntity.ok(userTransactions)
    }
}