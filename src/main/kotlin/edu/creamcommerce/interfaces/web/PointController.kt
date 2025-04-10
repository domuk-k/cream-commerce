package edu.creamcommerce.interfaces.web

import edu.creamcommerce.application.point.dto.PointDto
import edu.creamcommerce.application.point.dto.PointHistoryListDto
import edu.creamcommerce.application.point.dto.command.ChargePointCommand
import edu.creamcommerce.application.point.dto.command.UsePointCommand
import edu.creamcommerce.application.point.facade.PointFacade
import edu.creamcommerce.domain.point.PointId
import edu.creamcommerce.interfaces.request.PointRequest
import edu.creamcommerce.interfaces.response.ApiResponse
import edu.creamcommerce.interfaces.response.toSuccessResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/points")
@Tag(name = "포인트 API", description = "포인트 충전, 사용 및 조회 관련 API")
class PointController(
    private val pointFacade: PointFacade
) {
    @GetMapping("/users/{userId}")
    @Operation(summary = "사용자 포인트 조회", description = "사용자 ID에 해당하는 포인트 정보를 반환합니다.")
    fun getPointByUserId(@PathVariable userId: String): ResponseEntity<ApiResponse<PointDto>> {
        return pointFacade.getPointByUserId(userId).toSuccessResponse()
    }
    
    @PostMapping("/charge")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "포인트 충전", description = "사용자 포인트를 충전합니다.")
    fun chargePoint(@RequestBody @Valid request: PointRequest.Charge): ResponseEntity<ApiResponse<PointDto>> {
        return pointFacade.chargePoint(ChargePointCommand(request.userId, request.amount)).toSuccessResponse()
    }
    
    @PostMapping("/use")
    @Operation(summary = "포인트 사용", description = "사용자 포인트를 사용합니다.")
    fun usePoint(@RequestBody @Valid request: PointRequest.Use): ResponseEntity<ApiResponse<PointDto>> {
        return pointFacade.usePoint(UsePointCommand(request.userId, request.amount)).toSuccessResponse()
    }
    
    @GetMapping("/{pointId}/histories")
    @Operation(summary = "포인트 이력 조회", description = "포인트 ID에 해당하는 이력 목록을 반환합니다.")
    fun getPointHistories(@PathVariable pointId: String): ResponseEntity<ApiResponse<PointHistoryListDto>> {
        return pointFacade.getPointHistories(PointId(pointId)).toSuccessResponse()
    }
} 