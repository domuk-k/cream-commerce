package edu.creamcommerce.application.point.usecase

import edu.creamcommerce.application.point.dto.PointDto
import edu.creamcommerce.application.point.dto.command.ChargePointCommand
import edu.creamcommerce.application.point.dto.toDto
import edu.creamcommerce.domain.point.Point
import edu.creamcommerce.domain.point.PointHistoryRepository
import edu.creamcommerce.domain.point.PointRepository
import org.springframework.stereotype.Component

@Component
class ChargePointUseCase(
    private val pointRepository: PointRepository,
    private val pointHistoryRepository: PointHistoryRepository
) {
    operator fun invoke(command: ChargePointCommand): PointDto {
        val point = pointRepository.findByUserId(command.userId)
            ?: Point.create(userId = command.userId)
        
        val pointHistory = point.charge(command.amount)
        
        val savedPoint = pointRepository.save(point)
        pointHistoryRepository.save(pointHistory)
        
        return savedPoint.toDto()
    }
} 