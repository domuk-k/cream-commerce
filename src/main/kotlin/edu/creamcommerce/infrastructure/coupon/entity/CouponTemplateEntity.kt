package edu.creamcommerce.infrastructure.coupon.entity

import edu.creamcommerce.domain.coupon.CouponTemplateStatus
import edu.creamcommerce.domain.coupon.DiscountType
import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "coupon_templates")
class CouponTemplateEntity(
    @Id
    @Column(name = "id")
    var id: String = UUID.randomUUID().toString(),
    
    @Column(name = "name", nullable = false)
    var name: String,
    
    @Column(name = "description", columnDefinition = "TEXT")
    var description: String,
    
    @Enumerated(EnumType.STRING)
    @Column(name = "discount_type", nullable = false)
    var discountType: DiscountType,
    
    @Column(name = "discount_value", nullable = false)
    var discountValue: Int,
    
    @Column(name = "minimum_order_amount", nullable = false)
    var minimumOrderAmount: BigDecimal,
    
    @Column(name = "maximum_discount_amount", nullable = true)
    var maximumDiscountAmount: BigDecimal?,
    
    @Column(name = "max_issuance_count", nullable = false)
    var maxIssuanceCount: Int,
    
    @Column(name = "max_issuance_per_user", nullable = false)
    var maxIssuancePerUser: Int,
    
    @Column(name = "valid_duration_hours", nullable = false)
    var validDurationHours: Int,
    
    @Column(name = "start_at", nullable = false)
    var startAt: LocalDateTime,
    
    @Column(name = "end_at", nullable = false)
    var endAt: LocalDateTime,
    
    @Column(name = "issued_count", nullable = false)
    var issuedCount: Int = 0,
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    var status: CouponTemplateStatus,
    
    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: LocalDateTime = LocalDateTime.now()
) 