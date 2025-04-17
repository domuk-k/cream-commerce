package edu.creamcommerce.application.point.facade

import edu.creamcommerce.application.point.dto.PointDto
import edu.creamcommerce.application.point.dto.PointHistoryDto
import edu.creamcommerce.application.point.dto.PointHistoryListDto
import edu.creamcommerce.application.point.dto.command.ChargePointCommand
import edu.creamcommerce.application.point.dto.command.UsePointCommand
import edu.creamcommerce.application.point.usecase.*
import edu.creamcommerce.domain.coupon.UserId
import edu.creamcommerce.domain.point.PointHistoryId
import edu.creamcommerce.domain.point.PointHistoryType
import edu.creamcommerce.domain.point.PointId
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.math.BigDecimal
import java.time.LocalDateTime

class PointFacadeTest : BehaviorSpec({
    val getPointByIdUseCase = mockk<GetPointByIdUseCase>()
    val getPointByUserIdUseCase = mockk<GetPointByUserIdUseCase>()
    val chargePointUseCase = mockk<ChargePointUseCase>()
    val usePointUseCase = mockk<UsePointUseCase>()
    val getPointHistoriesUseCase = mockk<GetPointHistoriesUseCase>()
    
    val pointFacade = PointFacade(
        getPointByIdUseCase = getPointByIdUseCase,
        getPointByUserIdUseCase = getPointByUserIdUseCase,
        chargePointUseCase = chargePointUseCase,
        usePointUseCase = usePointUseCase,
        getPointHistoriesUseCase = getPointHistoriesUseCase
    )
    given("사용자 ID가 주어졌을 때") {
        val userId = UserId("test-user-id")
        val pointDto = createTestPointDto(PointId("point-id"), userId)
        
        `when`("해당 사용자의 포인트가 존재하는 경우") {
            every { getPointByUserIdUseCase.invoke(userId) } returns pointDto
            
            then("포인트 정보를 반환한다") {
                val result = pointFacade.getPointByUserId(userId)
                
                result shouldBe pointDto
                verify { getPointByUserIdUseCase.invoke(userId) }
            }
        }
        
        `when`("해당 사용자의 포인트가 존재하지 않는 경우") {
            every { getPointByUserIdUseCase.invoke(userId) } returns null
            
            then("NoSuchElementException 예외가 발생한다") {
                val exception = shouldThrow<NoSuchElementException> {
                    pointFacade.getPointByUserId(userId)
                }
                
                exception.message shouldBe "포인트 정보를 찾을 수 없습니다."
                verify { getPointByUserIdUseCase.invoke(userId) }
            }
        }
    }
    
    given("포인트 충전 명령이 주어졌을 때") {
        val command = ChargePointCommand(
            userId = UserId("test-user-id"),
            amount = BigDecimal.valueOf(1000)
        )
        
        val chargedPointDto = createTestPointDto(
            id = PointId("point-id"),
            userId = command.userId,
            amount = command.amount
        )
        
        `when`("충전을 요청하면") {
            every { chargePointUseCase(command) } returns chargedPointDto
            
            then("충전된 포인트 정보를 반환한다") {
                val result = pointFacade.chargePoint(command)
                
                result shouldBe chargedPointDto
                result.userId shouldBe command.userId
                result.amount shouldBe command.amount
                
                verify { chargePointUseCase(command) }
            }
        }
    }
    
    given("포인트 사용 명령이 주어졌을 때") {
        val command = UsePointCommand(
            userId = UserId("test-user-id"),
            amount = BigDecimal.valueOf(500)
        )
        
        val remainingAmount = BigDecimal.valueOf(500) // 1000 - 500
        val usedPointDto = createTestPointDto(
            id = PointId("point-id"),
            userId = command.userId,
            amount = remainingAmount
        )
        
        `when`("사용을 요청하면") {
            every { usePointUseCase(command) } returns usedPointDto
            
            then("사용 후 포인트 정보를 반환한다") {
                val result = pointFacade.usePoint(command)
                
                result shouldBe usedPointDto
                result.userId shouldBe command.userId
                result.amount shouldBe remainingAmount
                
                verify { usePointUseCase(command) }
            }
        }
    }
    
    given("포인트 이력 조회 요청이 주어졌을 때") {
        val pointId = PointId("test-point-id")
        val pointDto = createTestPointDto(pointId)
        val historyListDto = PointHistoryListDto(
            histories = listOf(
                createTestPointHistoryDto(
                    id = PointHistoryId("history-1"),
                    pointId = pointId,
                    type = PointHistoryType.CHARGE
                ),
                createTestPointHistoryDto(
                    id = PointHistoryId("history-2"),
                    pointId = pointId,
                    type = PointHistoryType.USE
                )
            ),
            total = 2
        )
        
        `when`("해당 포인트 ID가 존재하는 경우") {
            every { getPointByIdUseCase(pointId) } returns pointDto
            every { getPointHistoriesUseCase(pointId) } returns historyListDto
            
            then("포인트 이력 목록을 반환한다") {
                val result = pointFacade.getPointHistories(pointId)
                
                result shouldBe historyListDto
                result.histories.size shouldBe 2
                
                verify {
                    getPointByIdUseCase(pointId)
                    getPointHistoriesUseCase(pointId)
                }
            }
        }
        
        `when`("해당 포인트 ID가 존재하지 않는 경우") {
            every { getPointByIdUseCase(pointId) } returns null
            
            then("NoSuchElementException 예외가 발생한다") {
                val exception = shouldThrow<NoSuchElementException> {
                    pointFacade.getPointHistories(pointId)
                }
                
                exception.message shouldBe "포인트 정보를 찾을 수 없습니다."
                
                verify { getPointByIdUseCase(pointId) }
            }
        }
    }
}) {
    companion object {
        fun createTestPointDto(
            id: PointId = PointId("test-user"),
            userId: UserId = UserId("test-user"),
            amount: BigDecimal = BigDecimal.valueOf(1000)
        ): PointDto {
            return PointDto(
                id = id,
                userId = userId,
                amount = amount,
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now()
            )
        }
        
        fun createTestPointHistoryDto(
            id: PointHistoryId = PointHistoryId("test-history"),
            pointId: PointId = PointId("test-user"),
            type: PointHistoryType,
            amount: BigDecimal = BigDecimal.valueOf(1000),
            balance: BigDecimal = BigDecimal.valueOf(1000)
        ): PointHistoryDto {
            return PointHistoryDto(
                id = id,
                pointId = pointId,
                type = type,
                amount = if (type == PointHistoryType.USE) amount.negate() else amount,
                balance = balance,
                createdAt = LocalDateTime.now()
            )
        }
    }
} 