package edu.creamcommerce.application.point.dto

import edu.creamcommerce.domain.point.PointHistory
import edu.creamcommerce.domain.point.PointHistoryType
import java.math.BigDecimal
import java.time.LocalDateTime

data class PointHistoryDto(
    val id: String,
    val pointId: String,
    val type: PointHistoryType,
    val amount: BigDecimal,
    val balance: BigDecimal,
    val createdAt: LocalDateTime
)

fun PointHistory.toDto(): PointHistoryDto = PointHistoryDto(
    id = this.id.value,
    pointId = this.pointId.value,
    type = this.type,
    amount = this.amount,
    balance = this.balance,
    createdAt = this.createdAt
) 