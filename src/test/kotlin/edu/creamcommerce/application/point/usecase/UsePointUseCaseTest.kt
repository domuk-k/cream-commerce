package edu.creamcommerce.application.point.usecase

import edu.creamcommerce.application.point.dto.command.UsePointCommand
import edu.creamcommerce.domain.coupon.UserId
import edu.creamcommerce.domain.point.Point
import edu.creamcommerce.domain.point.PointHistory
import edu.creamcommerce.domain.point.PointHistoryRepository
import edu.creamcommerce.domain.point.PointRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import java.math.BigDecimal

class UsePointUseCaseTest : BehaviorSpec({
    val pointRepository = mockk<PointRepository>()
    val pointHistoryRepository = mockk<PointHistoryRepository>()
    val usePointUseCase = UsePointUseCase(pointRepository, pointHistoryRepository)
    
    given("포인트 사용 명령이 주어졌을 때") {
        val userId = UserId("test-user")
        val useAmount = BigDecimal.valueOf(500)
        val command = UsePointCommand(userId = userId, amount = useAmount)
        
        `when`("해당 유저의 포인트가 존재하고 충분한 잔액이 있는 경우") {
            val existingPoint = Point.create(userId = userId, amount = BigDecimal.valueOf(1000))
            val pointSlot = slot<Point>()
            val historySlot = slot<PointHistory>()
            
            every { pointRepository.findByUserId(userId) } returns existingPoint
            every { pointRepository.save(capture(pointSlot)) } answers { pointSlot.captured }
            every { pointHistoryRepository.save(capture(historySlot)) } answers { historySlot.captured }
            
            then("포인트가 차감되고 히스토리가 생성된다") {
                val result = usePointUseCase(command)
                
                result.userId shouldBe userId
                result.amount shouldBe BigDecimal.valueOf(500) // 1000 - 500
                
                verify {
                    pointRepository.findByUserId(userId)
                    pointRepository.save(any())
                    pointHistoryRepository.save(any())
                }
                
                val savedPoint = pointSlot.captured
                savedPoint.amount shouldBe BigDecimal.valueOf(500)
                
                val savedHistory = historySlot.captured
                savedHistory.pointId shouldBe existingPoint.id
                savedHistory.amount shouldBe useAmount.negate() // 음수로 기록됨
            }
        }
        
        `when`("해당 유저의 포인트가 존재하지 않는 경우") {
            every { pointRepository.findByUserId(userId) } returns null
            
            then("예외가 발생한다") {
                val exception = shouldThrow<IllegalArgumentException> {
                    usePointUseCase(command)
                }
                
                exception.message shouldBe "포인트 정보를 찾을 수 없습니다."
                
                verify {
                    pointRepository.findByUserId(userId)
                }
            }
        }
        
        `when`("해당 유저의 포인트가 존재하지만 잔액이 부족한 경우") {
            val existingPoint = Point.create(userId = userId, amount = useAmount - (BigDecimal.valueOf(1)))
            
            every { pointRepository.findByUserId(userId) } returns existingPoint
            
            then("예외가 발생한다") {
                val exception = shouldThrow<IllegalArgumentException> {
                    usePointUseCase(command)
                }
                
                exception.message shouldBe "포인트가 부족합니다."
                
                verify {
                    pointRepository.findByUserId(userId)
                }
            }
        }
    }
}) 