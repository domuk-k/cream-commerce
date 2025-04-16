package edu.creamcommerce.application.point.dto

import edu.creamcommerce.domain.coupon.UserId
import edu.creamcommerce.domain.point.Point
import edu.creamcommerce.domain.point.PointId
import java.math.BigDecimal
import java.time.LocalDateTime

data class PointDto(
    val id: PointId,
    val userId: UserId,
    val amount: BigDecimal,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)

fun Point.toDto(): PointDto = PointDto(
    id = this.id,
    userId = this.userId,
    amount = this.amount,
    createdAt = this.createdAt,
    updatedAt = this.updatedAt
) 