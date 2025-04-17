package edu.creamcommerce.application.point.usecase

import edu.creamcommerce.application.point.dto.command.ChargePointCommand
import edu.creamcommerce.domain.coupon.UserId
import edu.creamcommerce.domain.point.Point
import edu.creamcommerce.domain.point.PointHistory
import edu.creamcommerce.domain.point.PointHistoryRepository
import edu.creamcommerce.domain.point.PointRepository
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.*
import java.math.BigDecimal

class ChargePointUseCaseTest : BehaviorSpec({
    val pointRepository = mockk<PointRepository>()
    val pointHistoryRepository = mockk<PointHistoryRepository>()
    val chargePointUseCase = ChargePointUseCase(pointRepository, pointHistoryRepository)
    
    given("포인트 충전 명령이 주어졌을 때") {
        val userId = UserId("test-user")
        val chargeAmount = BigDecimal.valueOf(1000)
        val command = ChargePointCommand(userId = userId, amount = chargeAmount)
        
        `when`("해당 유저의 포인트가 이미 존재하는 경우") {
            val existingPoint = Point.create(userId = userId, amount = BigDecimal.valueOf(500))
            val pointSlot = slot<Point>()
            val historySlot = slot<PointHistory>()
            
            every { pointRepository.findByUserId(userId) } returns existingPoint
            every { pointRepository.save(capture(pointSlot)) } answers { pointSlot.captured }
            every { pointHistoryRepository.save(capture(historySlot)) } answers { historySlot.captured }
            
            then("기존 포인트에 금액이 추가되고 히스토리가 생성된다") {
                val result = chargePointUseCase(command)
                
                result.userId shouldBe userId
                result.amount shouldBe BigDecimal.valueOf(1500) // 500 + 1000
                
                verify { 
                    pointRepository.findByUserId(userId)
                    pointRepository.save(any())
                    pointHistoryRepository.save(any())
                }
                
                val savedPoint = pointSlot.captured
                savedPoint.amount shouldBe BigDecimal.valueOf(1500)
                
                val savedHistory = historySlot.captured
                savedHistory.pointId shouldBe existingPoint.id
                savedHistory.amount shouldBe chargeAmount
            }
        }
        
        `when`("해당 유저의 포인트가 존재하지 않는 경우") {
            val pointSlot = slot<Point>()
            val historySlot = slot<PointHistory>()
            
            every { pointRepository.findByUserId(userId) } returns null
            every { pointRepository.save(capture(pointSlot)) } answers { pointSlot.captured }
            every { pointHistoryRepository.save(capture(historySlot)) } answers { historySlot.captured }
            
            then("새로운 포인트가 생성되고 히스토리가 생성된다") {
                val result = chargePointUseCase(command)
                
                result.userId shouldBe userId
                result.amount shouldBe chargeAmount
                
                verify { 
                    pointRepository.findByUserId(userId)
                    pointRepository.save(any())
                    pointHistoryRepository.save(any())
                }
                
                val savedPoint = pointSlot.captured
                savedPoint.userId shouldBe userId
                savedPoint.amount shouldBe chargeAmount
                
                val savedHistory = historySlot.captured
                savedHistory.pointId shouldBe savedPoint.id
                savedHistory.amount shouldBe chargeAmount
            }
        }
    }
}) 