package edu.creamcommerce.infrastructure.point.repository

import edu.creamcommerce.infrastructure.point.entity.PointHistoryEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface JpaPointHistoryRepository : JpaRepository<PointHistoryEntity, String> {
    fun findByPointId(pointId: String): List<PointHistoryEntity>
    fun findByPointIdOrderByCreatedAtDesc(pointId: String): List<PointHistoryEntity>
} 