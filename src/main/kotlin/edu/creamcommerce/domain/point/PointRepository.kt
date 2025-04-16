package edu.creamcommerce.domain.point

import edu.creamcommerce.domain.coupon.UserId
import org.springframework.stereotype.Repository

@Repository
interface PointRepository {
    fun findById(id: PointId): Point?
    fun findByUserId(userId: UserId): Point?
    fun save(point: Point): Point
} 