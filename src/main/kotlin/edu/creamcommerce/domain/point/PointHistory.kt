package edu.creamcommerce.domain.point

import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

class PointHistory private constructor(
    val id: PointHistoryId,
    val pointId: PointId,
    val type: PointHistoryType,
    val amount: BigDecimal,
    val balance: BigDecimal,
    val createdAt: LocalDateTime
) {
    companion object {
        fun create(
            id: PointHistoryId? = null,
            pointId: PointId,
            type: PointHistoryType,
            amount: BigDecimal,
            balance: BigDecimal
        ): PointHistory {
            return PointHistory(
                id = id ?: PointHistoryId.create(),
                pointId = pointId,
                type = type,
                amount = amount,
                balance = balance,
                createdAt = LocalDateTime.now()
            )
        }
    }
}

@JvmInline
value class PointHistoryId(val value: String) {
    companion object {
        fun create(): PointHistoryId = PointHistoryId(UUID.randomUUID().toString())
    }
} 