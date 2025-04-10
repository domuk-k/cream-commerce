package edu.creamcommerce.application.point.usecase

import edu.creamcommerce.application.point.dto.PointHistoryDto
import edu.creamcommerce.application.point.dto.PointHistoryListDto
import edu.creamcommerce.application.point.dto.toDto
import edu.creamcommerce.domain.point.PointHistoryRepository
import edu.creamcommerce.domain.point.PointId
import org.springframework.stereotype.Component

@Component
class GetPointHistoriesUseCase(
    private val pointHistoryRepository: PointHistoryRepository
) {
    operator fun invoke(pointId: PointId): PointHistoryListDto {
        val histories = pointHistoryRepository.findByPointId(pointId)
        
        return PointHistoryListDto(
            histories = histories.map { it.toDto() },
            total = histories.size
        )
    }
} 