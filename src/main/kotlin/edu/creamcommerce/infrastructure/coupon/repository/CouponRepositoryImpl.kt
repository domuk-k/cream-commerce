package edu.creamcommerce.infrastructure.coupon.repository

import edu.creamcommerce.domain.coupon.*
import edu.creamcommerce.infrastructure.coupon.mapper.CouponMapper
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Repository
@Transactional(readOnly = true)
class CouponRepositoryImpl(
    private val jpaCouponTemplateRepository: JpaCouponTemplateRepository,
    private val jpaUserCouponRepository: JpaUserCouponRepository,
    private val couponMapper: CouponMapper
) : CouponRepository {
    
    @Transactional
    override fun saveTemplate(template: CouponTemplate): CouponTemplate {
        val entity = if (template.id != null) {
            val existingEntity = jpaCouponTemplateRepository.findById(template.id!!.value).orElse(null)
            if (existingEntity != null) {
                existingEntity.apply {
                    name = template.name
                    description = template.description
                    discountType = template.discountType
                    discountValue = template.discountValue
                    minimumOrderAmount = template.minimumOrderAmount.amount
                    maximumDiscountAmount = template.maximumDiscountAmount?.amount
                    maxIssuanceCount = template.maxIssuanceCount
                    maxIssuancePerUser = template.maxIssuancePerUser
                    validDurationHours = template.validDurationHours
                    startAt = template.startAt
                    endAt = template.endAt
                    issuedCount = template.issuedCount
                    status = template.status
                }
            } else {
                couponMapper.toEntity(template)
            }
        } else {
            couponMapper.toEntity(template)
        }
        
        val savedEntity = jpaCouponTemplateRepository.save(entity)
        return couponMapper.toDomain(savedEntity)
    }
    
    override fun findTemplateById(id: CouponTemplateId): CouponTemplate? {
        return jpaCouponTemplateRepository.findById(id.value).orElse(null)?.let {
            couponMapper.toDomain(it)
        }
    }
    
    override fun findAllActiveTemplates(): List<CouponTemplate> {
        return jpaCouponTemplateRepository.findByStatus(CouponTemplateStatus.ACTIVE).map {
            couponMapper.toDomain(it)
        }
    }
    
    @Transactional
    override fun saveUserCoupon(userCoupon: UserCoupon): UserCoupon {
        val entity = if (userCoupon.id != null) {
            val existingEntity = jpaUserCouponRepository.findById(userCoupon.id!!.value).orElse(null)
            if (existingEntity != null) {
                existingEntity.apply {
                    status = userCoupon.status
                    usedAt = if (userCoupon.status == UserCouponStatus.USED) LocalDateTime.now() else null
                    orderId = userCoupon.orderId?.value
                }
            } else {
                couponMapper.toEntity(userCoupon)
            }
        } else {
            couponMapper.toEntity(userCoupon)
        }
        
        val savedEntity = jpaUserCouponRepository.save(entity)
        return couponMapper.toDomain(savedEntity)
    }
    
    override fun findUserCouponById(id: UserCouponId): UserCoupon? {
        return jpaUserCouponRepository.findById(id.value).orElse(null)?.let {
            couponMapper.toDomain(it)
        }
    }
    
    override fun findUserCouponsByUserId(userId: UserId): List<UserCoupon> {
        return jpaUserCouponRepository.findByUserId(userId.value).map {
            couponMapper.toDomain(it)
        }
    }
    
    override fun findValidUserCouponsByUserId(userId: UserId): List<UserCoupon> {
        return jpaUserCouponRepository.findByUserIdAndStatusAndValidUntilAfter(
            userId.value,
            UserCouponStatus.VALID,
            LocalDateTime.now()
        ).map {
            couponMapper.toDomain(it)
        }
    }
    
    override fun findUserCouponCount(userId: UserId, templateId: CouponTemplateId): Long {
        return jpaUserCouponRepository.countByUserIdAndTemplateId(userId.value, templateId.value)
    }
    
    override fun findExpiredUserCoupons(): List<UserCoupon> {
        return jpaUserCouponRepository.findByStatusAndValidUntilBefore(
            UserCouponStatus.VALID,
            LocalDateTime.now()
        ).map {
            couponMapper.toDomain(it)
        }
    }
} 