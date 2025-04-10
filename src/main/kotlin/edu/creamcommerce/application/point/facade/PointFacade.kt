package edu.creamcommerce.application.point.facade

import edu.creamcommerce.application.point.dto.PointDto
import edu.creamcommerce.application.point.dto.PointHistoryListDto
import edu.creamcommerce.application.point.dto.command.ChargePointCommand
import edu.creamcommerce.application.point.dto.command.UsePointCommand
import edu.creamcommerce.application.point.usecase.*
import edu.creamcommerce.domain.point.PointId
import org.springframework.stereotype.Component

@Component
class PointFacade(
    private val getPointByIdUseCase: GetPointByIdUseCase,
    private val getPointByUserIdUseCase: GetPointByUserIdUseCase,
    private val chargePointUseCase: ChargePointUseCase,
    private val usePointUseCase: UsePointUseCase,
    private val getPointHistoriesUseCase: GetPointHistoriesUseCase
) {
    fun getPointByUserId(userId: String): PointDto {
        return getPointByUserIdUseCase(userId) ?: throw NoSuchElementException("포인트 정보를 찾을 수 없습니다.")
    }
    
    fun chargePoint(command: ChargePointCommand): PointDto {
        return chargePointUseCase(command)
    }
    
    fun usePoint(command: UsePointCommand): PointDto {
        return usePointUseCase(command)
    }
    
    fun getPointHistories(pointId: PointId): PointHistoryListDto {
        getPointByIdUseCase(pointId) ?: throw NoSuchElementException("포인트 정보를 찾을 수 없습니다.")
        return getPointHistoriesUseCase(pointId)
    }
} 