package edu.creamcommerce.infrastructure.point.repository

import edu.creamcommerce.infrastructure.point.entity.PointEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface JpaPointRepository : JpaRepository<PointEntity, String> {
    fun findByUserId(userId: String): PointEntity?
} 