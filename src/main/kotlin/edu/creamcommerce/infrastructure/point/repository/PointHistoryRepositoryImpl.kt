package edu.creamcommerce.infrastructure.point.repository

import edu.creamcommerce.domain.point.PointHistory
import edu.creamcommerce.domain.point.PointHistoryId
import edu.creamcommerce.domain.point.PointHistoryRepository
import edu.creamcommerce.domain.point.PointId
import edu.creamcommerce.infrastructure.point.mapper.PointMapper
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
@Transactional(readOnly = true)
class PointHistoryRepositoryImpl(
    private val jpaPointHistoryRepository: JpaPointHistoryRepository,
    private val pointMapper: PointMapper
) : PointHistoryRepository {
    
    override fun findById(id: PointHistoryId): PointHistory? {
        return jpaPointHistoryRepository.findById(id.value)
            .map { pointMapper.toDomain(it) }
            .orElse(null)
    }
    
    override fun findByPointId(pointId: PointId): List<PointHistory> {
        return jpaPointHistoryRepository.findByPointId(pointId.value)
            .map { pointMapper.toDomain(it) }
    }
    
    override fun findByPointIdOrderByCreatedAtDesc(pointId: PointId): List<PointHistory> {
        return jpaPointHistoryRepository.findByPointIdOrderByCreatedAtDesc(pointId.value)
            .map { pointMapper.toDomain(it) }
    }
    
    @Transactional
    override fun save(pointHistory: PointHistory): PointHistory {
        val entity = pointMapper.toEntity(pointHistory)
        val savedEntity = jpaPointHistoryRepository.save(entity)
        return pointMapper.toDomain(savedEntity)
    }
}