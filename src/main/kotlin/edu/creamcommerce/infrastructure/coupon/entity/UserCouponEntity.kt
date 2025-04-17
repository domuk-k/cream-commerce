package edu.creamcommerce.infrastructure.coupon.entity

import edu.creamcommerce.domain.coupon.DiscountType
import edu.creamcommerce.domain.coupon.UserCouponStatus
import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
@Table(name = "user_coupons")
class UserCouponEntity(
    @Id
    @Column(name = "id", nullable = false)
    var id: String,
    
    @Column(name = "user_id", nullable = false)
    var userId: String,
    
    @Column(name = "template_id", nullable = false)
    var templateId: String,
    
    @Column(name = "order_id")
    var orderId: String? = null,
    
    @Column(name = "name", nullable = false)
    var name: String,
    
    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    var description: String,
    
    @Enumerated(EnumType.STRING)
    @Column(name = "discount_type", nullable = false)
    var discountType: DiscountType,
    
    @Column(name = "discount_value", nullable = false)
    var discountValue: Int,
    
    @Column(name = "minimum_order_amount", nullable = false, precision = 19, scale = 2)
    var minimumOrderAmount: BigDecimal,
    
    @Column(name = "maximum_discount_amount", precision = 19, scale = 2)
    var maximumDiscountAmount: BigDecimal?,
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    var status: UserCouponStatus = UserCouponStatus.VALID,
    
    @Column(name = "issued_at", nullable = false, updatable = false)
    var issuedAt: LocalDateTime = LocalDateTime.now(),
    
    @Column(name = "valid_until", nullable = false)
    var validUntil: LocalDateTime,
    
    @Column(name = "used_at")
    var usedAt: LocalDateTime? = null,
)