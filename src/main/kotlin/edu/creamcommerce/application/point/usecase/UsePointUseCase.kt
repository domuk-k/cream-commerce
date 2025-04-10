package edu.creamcommerce.application.point.usecase

import edu.creamcommerce.application.point.dto.PointDto
import edu.creamcommerce.application.point.dto.command.UsePointCommand
import edu.creamcommerce.application.point.dto.toDto
import edu.creamcommerce.domain.point.PointHistoryRepository
import edu.creamcommerce.domain.point.PointRepository
import org.springframework.stereotype.Component

@Component
class UsePointUseCase(
    private val pointRepository: PointRepository,
    private val pointHistoryRepository: PointHistoryRepository
) {
    operator fun invoke(command: UsePointCommand): PointDto {
        val point = pointRepository.findByUserId(command.userId)
            ?: throw IllegalArgumentException("포인트 정보를 찾을 수 없습니다.")
        
        val pointHistory = point.use(command.amount)
        
        val savedPoint = pointRepository.save(point)
        pointHistoryRepository.save(pointHistory)
        
        return savedPoint.toDto()
    }
} 