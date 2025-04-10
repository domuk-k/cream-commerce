package edu.creamcommerce.application.point.dto

import edu.creamcommerce.domain.point.Point
import java.math.BigDecimal
import java.time.LocalDateTime

data class PointDto(
    val id: String,
    val userId: String,
    val amount: BigDecimal,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)

fun Point.toDto(): PointDto = PointDto(
    id = this.id.value,
    userId = this.userId,
    amount = this.amount,
    createdAt = this.createdAt,
    updatedAt = this.updatedAt
) 