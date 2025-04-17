package edu.creamcommerce.infrastructure.coupon.repository

import edu.creamcommerce.domain.coupon.UserCouponStatus
import edu.creamcommerce.infrastructure.coupon.entity.UserCouponEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface JpaUserCouponRepository : JpaRepository<UserCouponEntity, String> {
    
    fun findByUserIdAndStatusAndValidUntilAfter(
        userId: String,
        status: UserCouponStatus,
        validUntil: LocalDateTime
    ): List<UserCouponEntity>
    
    fun findByOrderId(orderId: String): List<UserCouponEntity>
    
    fun findByUserId(userId: String): List<UserCouponEntity>
    
    fun findByStatusAndValidUntilBefore(
        status: UserCouponStatus,
        validUntil: LocalDateTime
    ): List<UserCouponEntity>
    
    fun countByUserIdAndTemplateId(userId: String, templateId: String): Long
} 