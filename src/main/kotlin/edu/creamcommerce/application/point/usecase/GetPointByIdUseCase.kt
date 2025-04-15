package edu.creamcommerce.application.point.usecase

import edu.creamcommerce.application.point.dto.PointDto
import edu.creamcommerce.application.point.dto.toDto
import edu.creamcommerce.domain.point.PointId
import edu.creamcommerce.domain.point.PointRepository
import org.springframework.stereotype.Component

@Component
class GetPointByIdUseCase(
    private val pointRepository: PointRepository
) {
    operator fun invoke(id: PointId): PointDto? = pointRepository.findById(id)?.toDto()
} 