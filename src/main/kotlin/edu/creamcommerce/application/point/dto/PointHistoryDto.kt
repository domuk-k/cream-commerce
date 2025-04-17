package edu.creamcommerce.application.point.dto

import edu.creamcommerce.domain.point.PointHistory
import edu.creamcommerce.domain.point.PointHistoryId
import edu.creamcommerce.domain.point.PointHistoryType
import edu.creamcommerce.domain.point.PointId
import java.math.BigDecimal
import java.time.LocalDateTime

data class PointHistoryDto(
    val id: PointHistoryId,
    val pointId: PointId,
    val type: PointHistoryType,
    val amount: BigDecimal,
    val balance: BigDecimal,
    val createdAt: LocalDateTime
)

fun PointHistory.toDto(): PointHistoryDto = PointHistoryDto(
    id = this.id,
    pointId = this.pointId,
    type = this.type,
    amount = this.amount,
    balance = this.balance,
    createdAt = this.createdAt
) 