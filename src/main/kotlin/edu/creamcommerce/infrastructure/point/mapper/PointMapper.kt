package edu.creamcommerce.infrastructure.point.mapper

import edu.creamcommerce.domain.coupon.UserId
import edu.creamcommerce.domain.point.*
import edu.creamcommerce.infrastructure.point.entity.PointEntity
import edu.creamcommerce.infrastructure.point.entity.PointHistoryEntity
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class PointMapper {
    // Point 매핑
    fun toEntity(domain: Point): PointEntity {
        return PointEntity(
            id = domain.id.value,
            userId = domain.userId.value,
            amount = domain.amount,
            createdAt = domain.createdAt,
            updatedAt = domain.updatedAt
        )
    }

    fun toDomain(entity: PointEntity): Point {
        return Point.create(
            id = PointId(entity.id),
            userId = UserId(entity.userId),
            amount = entity.amount
        )
    }
    
    // PointHistory 매핑
    fun toEntity(domain: PointHistory): PointHistoryEntity {
        return PointHistoryEntity(
            id = domain.id.value,
            pointId = domain.pointId.value,
            type = domain.type,
            amount = domain.amount,
            balance = domain.balance,
            createdAt = domain.createdAt
        )
    }

    fun toDomain(entity: PointHistoryEntity): PointHistory {
        return PointHistory.create(
            id = PointHistoryId(entity.id),
            pointId = PointId(entity.pointId),
            type = entity.type,
            amount = entity.amount,
            balance = entity.balance
        )
    }
} 