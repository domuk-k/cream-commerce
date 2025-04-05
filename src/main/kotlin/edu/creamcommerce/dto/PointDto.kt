package edu.creamcommerce.dto

import java.math.BigDecimal
import java.time.LocalDateTime

data class PointDto(
    val id: Long? = null,
    val userId: Long,
    val balance: BigDecimal,
    val updatedAt: LocalDateTime = LocalDateTime.now()
)

data class PointChargeRequest(
    val amount: BigDecimal
)

data class PointChargeResponse(
    val pointId: Long,
    val userId: Long,
    val previousBalance: BigDecimal,
    val chargedAmount: BigDecimal,
    val currentBalance: BigDecimal,
    val transactionId: String,
    val timestamp: LocalDateTime = LocalDateTime.now()
)

data class PointBalanceResponse(
    val pointId: Long,
    val userId: Long,
    val balance: BigDecimal,
    val lastUpdated: LocalDateTime
)