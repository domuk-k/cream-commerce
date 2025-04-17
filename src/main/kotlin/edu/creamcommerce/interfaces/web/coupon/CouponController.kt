package edu.creamcommerce.interfaces.web.coupon

import edu.creamcommerce.application.coupon.dto.command.*
import edu.creamcommerce.application.coupon.dto.query.*
import edu.creamcommerce.application.coupon.facade.CouponFacade
import edu.creamcommerce.domain.common.Money
import edu.creamcommerce.domain.coupon.CouponOrderId
import edu.creamcommerce.domain.coupon.CouponTemplateId
import edu.creamcommerce.domain.coupon.UserCouponId
import edu.creamcommerce.domain.coupon.UserId
import edu.creamcommerce.interfaces.web.ApiResponse
import edu.creamcommerce.interfaces.web.toSuccessResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/coupons")
@Tag(name = "쿠폰 관리 API", description = "쿠폰 템플릿 생성, 상태 변경, 쿠폰 발급, 사용 API")
class CouponController(
    private val couponFacade: CouponFacade
) {
    // 쿠폰 템플릿 관련 API
    @PostMapping("/templates")
    @Operation(summary = "쿠폰 템플릿 생성", description = "새로운 쿠폰 템플릿을 생성합니다.")
    fun createCouponTemplate(@RequestBody request: CreateCouponTemplateRequest): ResponseEntity<ApiResponse<CouponTemplateDto>> {
        val command = CreateCouponTemplateCommand(
            name = request.name,
            description = request.description,
            discountType = request.discountType,
            discountValue = request.discountValue,
            minimumOrderAmount = Money(request.minimumOrderAmount),
            maximumDiscountAmount = request.maximumDiscountAmount?.let { Money(it) },
            maxIssuanceCount = request.maxIssuanceCount,
            maxIssuancePerUser = request.maxIssuancePerUser,
            validDurationHours = request.validDurationHours,
            startAt = request.startAt,
            endAt = request.endAt
        )
        
        val result = couponFacade.createCouponTemplate(command)
        return result.toSuccessResponse()
    }
    
    @GetMapping("/templates/active")
    @Operation(summary = "활성 쿠폰 템플릿 조회", description = "현재 활성 상태인 쿠폰 템플릿 목록을 조회합니다.")
    fun getActiveCouponTemplates(): ResponseEntity<ApiResponse<List<CouponTemplateDto>>> {
        val templates = couponFacade.getActiveCouponTemplates()
        return templates.toSuccessResponse()
    }
    
    @PutMapping("/templates/{templateId}/suspend")
    @Operation(summary = "쿠폰 템플릿 일시 중지", description = "쿠폰 템플릿을 일시 중지 상태로 변경합니다.")
    fun suspendCouponTemplate(@PathVariable templateId: String): ResponseEntity<ApiResponse<CouponTemplateStatusDto>> {
        val command = ChangeCouponTemplateStatusCommand(CouponTemplateId(templateId))
        val result = couponFacade.suspendCouponTemplate(command)
        return result.toSuccessResponse()
    }
    
    @PutMapping("/templates/{templateId}/resume")
    @Operation(summary = "쿠폰 템플릿 재개", description = "쿠폰 템플릿을 활성 상태로 변경합니다.")
    fun resumeCouponTemplate(@PathVariable templateId: String): ResponseEntity<ApiResponse<CouponTemplateStatusDto>> {
        val command = ChangeCouponTemplateStatusCommand(CouponTemplateId(templateId))
        val result = couponFacade.resumeCouponTemplate(command)
        return result.toSuccessResponse()
    }
    
    @PutMapping("/templates/{templateId}/terminate")
    @Operation(summary = "쿠폰 템플릿 종료", description = "쿠폰 템플릿을 종료 상태로 변경합니다.")
    fun terminateCouponTemplate(@PathVariable templateId: String): ResponseEntity<ApiResponse<CouponTemplateStatusDto>> {
        val command = ChangeCouponTemplateStatusCommand(CouponTemplateId(templateId))
        val result = couponFacade.terminateCouponTemplate(command)
        return result.toSuccessResponse()
    }
    
    // 쿠폰 발급 관련 API
    @PostMapping("/issue")
    @Operation(summary = "쿠폰 발급", description = "사용자에게 쿠폰을 발급합니다.")
    fun issueCoupon(@RequestBody request: IssueCouponRequest): ResponseEntity<ApiResponse<UserCouponDto>> {
        val command = IssueCouponCommand(
            userId = UserId(request.userId),
            templateId = CouponTemplateId(request.templateId)
        )
        
        val result = couponFacade.issueCoupon(command)
            ?: return ResponseEntity.badRequest().body(ApiResponse.error("쿠폰 발급에 실패했습니다."))
        
        return result.toSuccessResponse()
    }
    
    @GetMapping("/users/{userId}")
    @Operation(summary = "사용자의 모든 쿠폰 조회", description = "사용자가 소유한 모든 쿠폰을 조회합니다.")
    fun getUserCoupons(@PathVariable userId: String): ResponseEntity<ApiResponse<List<UserCouponDto>>> {
        val coupons = couponFacade.getUserCoupons(UserId(userId))
        return coupons.toSuccessResponse()
    }
    
    @GetMapping("/users/{userId}/valid")
    @Operation(summary = "사용자의 유효 쿠폰 조회", description = "사용자가 소유한 유효한 쿠폰을 조회합니다.")
    fun getValidUserCoupons(@PathVariable userId: String): ResponseEntity<ApiResponse<List<UserCouponDto>>> {
        val coupons = couponFacade.getValidUserCoupons(UserId(userId))
        return coupons.toSuccessResponse()
    }
    
    // 쿠폰 사용 관련 API
    @PostMapping("/use")
    @Operation(summary = "쿠폰 사용", description = "주문 시 쿠폰을 사용합니다.")
    fun useCoupon(@RequestBody request: UseCouponRequest): ResponseEntity<ApiResponse<CouponUsageResultDto>> {
        val command = UseCouponCommand(
            userId = UserId(request.userId),
            couponId = UserCouponId(request.couponId),
            orderAmount = Money(request.orderAmount),
            orderId = CouponOrderId(request.orderId)
        )
        
        val result = couponFacade.useCoupon(command)
            ?: return ResponseEntity.badRequest().body(ApiResponse.error("쿠폰 사용에 실패했습니다."))
        
        return result.toSuccessResponse()
    }
    
    @PostMapping("/{couponId}/revoke")
    @Operation(summary = "쿠폰 회수", description = "발급된 쿠폰을 회수합니다.")
    fun revokeCoupon(@PathVariable couponId: String): ResponseEntity<ApiResponse<CouponRevokeResultDto>> {
        val command = RevokeCouponCommand(UserCouponId(couponId))
        
        val result = couponFacade.revokeCoupon(command)
            ?: return ResponseEntity.badRequest().body(ApiResponse.error("쿠폰 회수에 실패했습니다."))
        
        return result.toSuccessResponse()
    }
    
    // 배치 작업 관련 API (관리자용)
    @PostMapping("/batch/expire")
    @Operation(summary = "만료 쿠폰 처리", description = "만료된 쿠폰을 자동으로 처리합니다. (관리자용)")
    fun expireOutdatedCoupons(): ResponseEntity<ApiResponse<ExpireCouponsResultDto>> {
        val result = couponFacade.expireOutdatedCoupons()
        return result.toSuccessResponse()
    }
}