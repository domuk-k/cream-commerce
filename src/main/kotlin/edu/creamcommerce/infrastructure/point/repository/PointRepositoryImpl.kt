package edu.creamcommerce.infrastructure.point.repository

import edu.creamcommerce.domain.coupon.UserId
import edu.creamcommerce.domain.point.Point
import edu.creamcommerce.domain.point.PointId
import edu.creamcommerce.domain.point.PointRepository
import edu.creamcommerce.infrastructure.point.entity.PointEntity
import edu.creamcommerce.infrastructure.point.mapper.PointMapper
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
@Transactional(readOnly = true)
class PointRepositoryImpl(
    private val jpaPointRepository: JpaPointRepository,
    private val pointMapper: PointMapper
) : PointRepository {
    
    override fun findById(id: PointId): Point? {
        return jpaPointRepository.findById(id.value)
            .map { pointMapper.toDomain(it) }
            .orElse(null)
    }
    
    override fun findByUserId(userId: UserId): Point? {
        return jpaPointRepository.findByUserId(userId.value)
            ?.let { pointMapper.toDomain(it) }
    }
    
    @Transactional
    override fun save(point: Point): Point {
        val entity = jpaPointRepository.findById(point.id.value)
            .map { existingEntity ->
                existingEntity.amount = point.amount
                existingEntity.updatedAt = point.updatedAt
                existingEntity
            }
            .orElseGet { pointMapper.toEntity(point) }
            
        val savedEntity = jpaPointRepository.save(entity)
        return pointMapper.toDomain(savedEntity)
    }
} 