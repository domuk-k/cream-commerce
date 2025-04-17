package edu.creamcommerce.infrastructure.coupon.repository

import edu.creamcommerce.domain.coupon.CouponTemplateStatus
import edu.creamcommerce.infrastructure.coupon.entity.CouponTemplateEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface JpaCouponTemplateRepository : JpaRepository<CouponTemplateEntity, String> {
    fun findByStatus(status: CouponTemplateStatus): List<CouponTemplateEntity>
} 