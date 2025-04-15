package edu.creamcommerce.domain.point

import org.springframework.stereotype.Repository

@Repository
interface PointRepository {
    fun findById(id: PointId): Point?
    fun findByUserId(userId: String): Point?
    fun save(point: Point): Point
} 