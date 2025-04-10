package edu.creamcommerce.interfaces.web

import com.fasterxml.jackson.databind.ObjectMapper
import edu.creamcommerce.application.point.dto.PointDto
import edu.creamcommerce.application.point.dto.PointHistoryDto
import edu.creamcommerce.application.point.dto.PointHistoryListDto
import edu.creamcommerce.application.point.dto.command.ChargePointCommand
import edu.creamcommerce.application.point.dto.command.UsePointCommand
import edu.creamcommerce.application.point.facade.PointFacade
import edu.creamcommerce.domain.point.PointHistoryType
import edu.creamcommerce.domain.point.PointId
import edu.creamcommerce.interfaces.request.PointRequest
import io.kotest.core.spec.style.ShouldSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import java.math.BigDecimal
import java.time.LocalDateTime

class PointControllerTest : ShouldSpec({
    val pointFacade = mockk<PointFacade>()
    val pointController = PointController(pointFacade)
    val mockMvc = MockMvcBuilders.standaloneSetup(pointController).build()
    val objectMapper = ObjectMapper()
    
    context("포인트 조회") {
        should("GET /api/points/users/{userId} 요청 시 사용자의 포인트 정보를 반환한다") {
            // given
            val userId = "test-user-id"
            val pointDto = createTestPointDto("point-id", userId)
            
            every { pointFacade.getPointByUserId(userId) } returns pointDto
            
            // when & then
            mockMvc.perform(
                get("/api/points/users/$userId")
                    .contentType(MediaType.APPLICATION_JSON)
            )
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.data.userId").value(userId))
            
            verify { pointFacade.getPointByUserId(userId) }
        }
    }
    
    context("포인트 충전") {
        should("POST /api/points/charge 요청 시 포인트가 충전되고 정보를 반환한다") {
            // given
            val request = PointRequest.Charge(
                userId = "test-user-id",
                amount = BigDecimal.valueOf(1000)
            )
            
            val command = ChargePointCommand(
                userId = request.userId,
                amount = request.amount
            )
            
            val chargedPointDto = createTestPointDto(
                id = "point-id",
                userId = request.userId,
                amount = request.amount
            )
            
            every { pointFacade.chargePoint(command) } returns chargedPointDto
            
            // when & then
            mockMvc.perform(
                post("/api/points/charge")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.data.userId").value(request.userId))
                .andExpect(jsonPath("$.data.amount").value(1000))
            
            verify { pointFacade.chargePoint(command) }
        }
    }
    
    context("포인트 사용") {
        should("POST /api/points/use 요청 시 포인트가 사용되고 정보를 반환한다") {
            // given
            val request = PointRequest.Use(
                userId = "test-user-id",
                amount = BigDecimal.valueOf(500)
            )
            
            val command = UsePointCommand(
                userId = request.userId,
                amount = request.amount
            )
            
            val remainingAmount = BigDecimal.valueOf(500) // 1000 - 500
            val usedPointDto = createTestPointDto(
                id = "point-id",
                userId = request.userId,
                amount = remainingAmount
            )
            
            every { pointFacade.usePoint(command) } returns usedPointDto
            
            // when & then
            mockMvc.perform(
                post("/api/points/use")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.data.userId").value(request.userId))
                .andExpect(jsonPath("$.data.amount").value(500))
            
            verify { pointFacade.usePoint(command) }
        }
    }
    
    context("포인트 이력 조회") {
        should("GET /api/points/{pointId}/histories 요청 시 포인트 이력 목록을 반환한다") {
            // given
            val pointId = "test-point-id"
            val historyListDto = PointHistoryListDto(
                histories = listOf(
                    createTestPointHistoryDto(
                        id = "history-1",
                        pointId = pointId,
                        type = PointHistoryType.CHARGE
                    ),
                    createTestPointHistoryDto(
                        id = "history-2",
                        pointId = pointId,
                        type = PointHistoryType.USE
                    )
                ),
                total = 2
            )
            
            every { pointFacade.getPointHistories(PointId(pointId)) } returns historyListDto
            
            // when & then
            mockMvc.perform(
                get("/api/points/$pointId/histories")
                    .contentType(MediaType.APPLICATION_JSON)
            )
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.data.histories.length()").value(2))
                .andExpect(jsonPath("$.data.total").value(2))
            
            verify { pointFacade.getPointHistories(PointId(pointId)) }
        }
    }
}) {
    companion object {
        fun createTestPointDto(
            id: String,
            userId: String = "test-user",
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
            id: String,
            pointId: String,
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