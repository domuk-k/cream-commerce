package edu.creamcommerce.application.coupon.facade

import edu.creamcommerce.application.coupon.dto.command.*
import edu.creamcommerce.application.coupon.dto.query.*
import edu.creamcommerce.application.coupon.usecase.*
import edu.creamcommerce.domain.coupon.CouponTemplateStatus
import edu.creamcommerce.domain.coupon.UserId
import org.springframework.stereotype.Service

@Service
class CouponFacade(
    private val createCouponTemplateUseCase: CreateCouponTemplateUseCase,
    private val changeCouponTemplateStatusUseCase: ChangeCouponTemplateStatusUseCase,
    private val getActiveCouponTemplatesUseCase: GetActiveCouponTemplatesUseCase,
    private val issueCouponUseCase: IssueCouponUseCase,
    private val getUserCouponsUseCase: GetUserCouponsUseCase,
    private val getValidUserCouponsUseCase: GetValidUserCouponsUseCase,
    private val useCouponUseCase: UseCouponUseCase,
    private val revokeCouponUseCase: RevokeCouponUseCase,
    private val expireOutdatedCouponsUseCase: ExpireOutdatedCouponsUseCase
) {
    fun createCouponTemplate(command: CreateCouponTemplateCommand): CouponTemplateDto {
        return createCouponTemplateUseCase(command)
    }
    
    fun suspendCouponTemplate(command: ChangeCouponTemplateStatusCommand): CouponTemplateStatusDto {
        return changeCouponTemplateStatusUseCase(command.templateId, CouponTemplateStatus.SUSPENDED)
    }
    
    fun resumeCouponTemplate(command: ChangeCouponTemplateStatusCommand): CouponTemplateStatusDto {
        return changeCouponTemplateStatusUseCase(command.templateId, CouponTemplateStatus.ACTIVE)
    }
    
    fun terminateCouponTemplate(command: ChangeCouponTemplateStatusCommand): CouponTemplateStatusDto {
        return changeCouponTemplateStatusUseCase(command.templateId, CouponTemplateStatus.TERMINATED)
    }
    
    fun getActiveCouponTemplates(): List<CouponTemplateDto> {
        return getActiveCouponTemplatesUseCase()
    }
    
    // 쿠폰 발급 관련 메서드
    fun issueCoupon(command: IssueCouponCommand): UserCouponDto? {
        return issueCouponUseCase(command)
    }
    
    fun getUserCoupons(userId: UserId): List<UserCouponDto> {
        return getUserCouponsUseCase(userId)
    }
    
    fun getValidUserCoupons(userId: UserId): List<UserCouponDto> {
        return getValidUserCouponsUseCase(userId)
    }
    
    // 쿠폰 사용 관련 메서드
    fun useCoupon(command: UseCouponCommand): CouponUsageResultDto? {
        return useCouponUseCase(command)
    }
    
    fun revokeCoupon(command: RevokeCouponCommand): CouponRevokeResultDto? {
        return revokeCouponUseCase(command)
    }
    
    // 배치 작업 관련 메서드
    fun expireOutdatedCoupons(): ExpireCouponsResultDto {
        return expireOutdatedCouponsUseCase()
    }
} 