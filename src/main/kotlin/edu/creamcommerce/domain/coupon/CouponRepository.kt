package edu.creamcommerce.domain.coupon

import org.springframework.stereotype.Repository

@Repository
interface CouponRepository {
    fun saveTemplate(template: CouponTemplate): CouponTemplate
    fun findTemplateById(id: CouponTemplateId): CouponTemplate?
    fun findAllActiveTemplates(): List<CouponTemplate>
    
    fun saveUserCoupon(userCoupon: UserCoupon): UserCoupon
    fun findUserCouponById(id: UserCouponId): UserCoupon?
    fun findUserCouponsByUserId(userId: UserId): List<UserCoupon>
    fun findValidUserCouponsByUserId(userId: UserId): List<UserCoupon>
    fun findUserCouponCount(userId: UserId, templateId: CouponTemplateId): Long
    fun findExpiredUserCoupons(): List<UserCoupon>
} 