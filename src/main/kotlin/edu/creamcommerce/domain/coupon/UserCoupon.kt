package edu.creamcommerce.domain.coupon

import edu.creamcommerce.domain.common.Money
import java.time.LocalDateTime

class UserCoupon private constructor(
    val id: UserCouponId,
    val userId: UserId,
    val templateId: CouponTemplateId,
    val name: String,
    val description: String,
    val discountType: DiscountType,
    val discountValue: Int,
    val minimumOrderAmount: Money,
    val maximumDiscountAmount: Money?,
    val validUntil: LocalDateTime,
    val issuedAt: LocalDateTime = LocalDateTime.now(),
    status: UserCouponStatus = UserCouponStatus.VALID,
    orderId: CouponOrderId? = null,
    usedAt: LocalDateTime? = null
) {
    var status: UserCouponStatus = status
        private set
    
    var orderId: CouponOrderId? = orderId
        private set
    
    var usedAt: LocalDateTime? = usedAt
        private set
    
    // 쿠폰 사용 가능 여부 확인
    fun isUsable(): Boolean {
        val now = LocalDateTime.now()
        return status == UserCouponStatus.VALID && usedAt == null && now.isBefore(validUntil)
    }
    
    // 쿠폰 사용
    fun use(orderId: CouponOrderId): Boolean {
        if (!isUsable()) {
            return false
        }
        
        this.usedAt = LocalDateTime.now()
        this.status = UserCouponStatus.USED
        this.orderId = orderId
        return true
    }
    
    // 쿠폰 만료
    fun expire() {
        if (status == UserCouponStatus.VALID) {
            status = UserCouponStatus.EXPIRED
        }
    }
    
    // 쿠폰 회수
    fun revoke() {
        if (status == UserCouponStatus.VALID) {
            status = UserCouponStatus.REVOKED
        }
    }
    
    // 할인 금액 계산
    fun calculateDiscount(orderAmount: Money): Money {
        if (!isUsable() || orderAmount < minimumOrderAmount) {
            return Money.ZERO
        }
        
        return when (discountType) {
            DiscountType.FIXED_AMOUNT -> Money(discountValue)
            DiscountType.PERCENTAGE -> {
                val discountAmount = Money((orderAmount.amount * (discountValue.toBigDecimal() / 100.toBigDecimal())))
                if (maximumDiscountAmount != null && discountAmount > maximumDiscountAmount) {
                    maximumDiscountAmount
                } else {
                    discountAmount
                }
            }
        }
    }
    
    companion object {
        fun create(
            id: UserCouponId = UserCouponId.create(),
            userId: UserId,
            templateId: CouponTemplateId,
            name: String,
            description: String,
            discountType: DiscountType,
            discountValue: Int,
            minimumOrderAmount: Money,
            maximumDiscountAmount: Money?,
            validUntil: LocalDateTime,
            status: UserCouponStatus = UserCouponStatus.VALID,
            orderId: CouponOrderId? = null,
            issuedAt: LocalDateTime = LocalDateTime.now(),
            usedAt: LocalDateTime? = null
        ): UserCoupon {
            return UserCoupon(
                id = id,
                userId = userId,
                templateId = templateId,
                name = name,
                description = description,
                discountType = discountType,
                discountValue = discountValue,
                minimumOrderAmount = minimumOrderAmount,
                maximumDiscountAmount = maximumDiscountAmount,
                validUntil = validUntil,
                status = status,
                orderId = orderId,
                issuedAt = issuedAt,
                usedAt = usedAt
            
            )
        }
        
        fun issue(userId: UserId, template: CouponTemplate): UserCoupon? {
            // 템플릿이 발급 가능한지 확인
            if (!template.isIssuable()) {
                return null
            }
            
            // 템플릿에서 발급 시도
            if (!template.issue()) {
                return null
            }
            
            return UserCoupon(
                id = UserCouponId.create(),
                userId = userId,
                templateId = template.id,
                name = template.name,
                description = template.description,
                discountType = template.discountType,
                discountValue = template.discountValue,
                minimumOrderAmount = template.minimumOrderAmount,
                maximumDiscountAmount = template.maximumDiscountAmount,
                validUntil = template.calculateValidUntil()
            )
        }
    }
} 