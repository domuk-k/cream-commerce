package edu.creamcommerce.infrastructure.point.entity

import edu.creamcommerce.domain.coupon.UserId
import edu.creamcommerce.domain.point.Point
import edu.creamcommerce.domain.point.PointId
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "points")
class PointEntity(
    @Id
    @Column(name = "id")
    var id: String = UUID.randomUUID().toString(),
    
    @Column(name = "user_id", nullable = false)
    var userId: String,
    
    @Column(name = "amount", nullable = false, precision = 19, scale = 2)
    var amount: BigDecimal,
    
    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: LocalDateTime = LocalDateTime.now(),
    
    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now()
) {
    fun toDomain(): Point {
        return Point.create(
            id = PointId(id),
            userId = UserId(userId),
            amount = amount
        ).also {
            val createdAtField = Point::class.java.getDeclaredField("createdAt")
            createdAtField.isAccessible = true
            createdAtField.set(it, createdAt)
            
            val updatedAtField = Point::class.java.getDeclaredField("updatedAt")
            updatedAtField.isAccessible = true
            updatedAtField.set(it, updatedAt)
        }
    }
    
    companion object {
        fun fromDomain(point: Point): PointEntity {
            return PointEntity(
                id = point.id.value,
                userId = point.userId.value,
                amount = point.amount,
                createdAt = point.createdAt,
                updatedAt = point.updatedAt
            )
        }
    }
} 