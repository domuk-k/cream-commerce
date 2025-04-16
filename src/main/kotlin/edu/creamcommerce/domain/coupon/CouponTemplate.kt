package edu.creamcommerce.domain.coupon

import edu.creamcommerce.domain.common.Money
import java.time.LocalDateTime

class CouponTemplate private constructor(
    val id: CouponTemplateId,
    val name: String,
    val description: String,
    val discountType: DiscountType,
    val discountValue: Int, // 금액 또는 비율(%)
    val minimumOrderAmount: Money, // 최소 주문 금액
    val maximumDiscountAmount: Money?, // 최대 할인 금액 (비율 할인일 경우)
    val maxIssuanceCount: Int, // 최대 발급 수량
    val maxIssuancePerUser: Int, // 사용자당 최대 발급 가능 수량
    val validDurationHours: Int, // 발급 후 유효 기간(시간)
    val startAt: LocalDateTime, // 쿠폰 발급 시작 시간
    val endAt: LocalDateTime, // 쿠폰 발급 종료 시간
    issuedCount: Int = 0, // 발급된 수량
    status: CouponTemplateStatus = CouponTemplateStatus.ACTIVE
) {
    var issuedCount: Int = issuedCount
        private set
    
    var status: CouponTemplateStatus = status
        private set
    
    // 쿠폰 템플릿 발급 가능 여부 확인
    fun isIssuable(): Boolean {
        val now = LocalDateTime.now()
        
        return status == CouponTemplateStatus.ACTIVE &&
                now.isAfter(startAt) &&
                now.isBefore(endAt) &&
                (maxIssuanceCount == 0 || issuedCount < maxIssuanceCount)
    }
    
    // 쿠폰 발급
    fun issue(): Boolean {
        if (!isIssuable()) {
            return false
        }
        
        issuedCount++
        
        // 최대 발급 수량에 도달하면 DEPLETED 상태로 변경
        if (maxIssuanceCount > 0 && issuedCount >= maxIssuanceCount) {
            status = CouponTemplateStatus.DEPLETED
        }
        
        return true
    }
    
    // 쿠폰 템플릿 일시 중지
    fun suspend() {
        if (status == CouponTemplateStatus.ACTIVE) {
            status = CouponTemplateStatus.SUSPENDED
        }
    }
    
    // 쿠폰 템플릿 재개
    fun resume() {
        if (status == CouponTemplateStatus.SUSPENDED) {
            status = CouponTemplateStatus.ACTIVE
        }
    }
    
    // 쿠폰 템플릿 종료
    fun terminate() {
        if (status != CouponTemplateStatus.TERMINATED) {
            status = CouponTemplateStatus.TERMINATED
        }
    }
    
    // 쿠폰 유효기간 계산
    fun calculateValidUntil(): LocalDateTime {
        return LocalDateTime.now().plusHours(validDurationHours.toLong())
    }
    
    companion object {
        fun create(
            name: String,
            description: String,
            discountType: DiscountType,
            discountValue: Int,
            minimumOrderAmount: Money,
            maximumDiscountAmount: Money? = null,
            maxIssuanceCount: Int = 0, // 0일 경우 무제한
            maxIssuancePerUser: Int = 1,
            validDurationHours: Int = 24 * 7, // 기본 1주일
            startAt: LocalDateTime,
            endAt: LocalDateTime
        ): CouponTemplate {
            require(name.isNotBlank()) { "쿠폰 이름은 비어있을 수 없습니다." }
            require(discountValue > 0) { "할인 값은 0보다 커야 합니다." }
            require(minimumOrderAmount.amount.signum() >= 0) { "최소 주문 금액은 음수일 수 없습니다." }
            require(maxIssuanceCount >= 0) { "최대 발급 수량은 음수일 수 없습니다." }
            require(maxIssuancePerUser > 0) { "사용자당 최대 발급 수량은 0보다 커야 합니다." }
            require(validDurationHours > 0) { "유효 기간은 0보다 커야 합니다." }
            require(!startAt.isAfter(endAt)) { "시작 시간은 종료 시간보다 이후일 수 없습니다." }
            
            if (discountType == DiscountType.PERCENTAGE) {
                require(discountValue in 1..100) { "할인율은 1%에서 100% 사이여야 합니다." }
                require(maximumDiscountAmount != null) { "비율 할인의 경우 최대 할인 금액이 필요합니다." }
            }
            
            return CouponTemplate(
                id = CouponTemplateId.create(),
                name = name,
                description = description,
                discountType = discountType,
                discountValue = discountValue,
                minimumOrderAmount = minimumOrderAmount,
                maximumDiscountAmount = maximumDiscountAmount,
                maxIssuanceCount = maxIssuanceCount,
                maxIssuancePerUser = maxIssuancePerUser,
                validDurationHours = validDurationHours,
                startAt = startAt,
                endAt = endAt
            )
        }
    }
} 