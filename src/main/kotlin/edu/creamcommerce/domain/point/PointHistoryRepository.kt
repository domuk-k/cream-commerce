package edu.creamcommerce.domain.point

import org.springframework.stereotype.Repository

@Repository
interface PointHistoryRepository {
    fun save(pointHistory: PointHistory): PointHistory
    fun findByPointId(pointId: PointId): List<PointHistory>
} 