package edu.creamcommerce.domain.point

import edu.creamcommerce.domain.coupon.UserId
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

class Point private constructor(
    val id: PointId,
    val userId: UserId,
    amount: BigDecimal,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
) {
    companion object {
        fun create(
            id: PointId? = null,
            userId: UserId,
            amount: BigDecimal = BigDecimal.ZERO
        ): Point {
            val now = LocalDateTime.now()
            return Point(
                id = id ?: PointId.create(),
                userId = userId,
                amount = amount,
                createdAt = now,
                updatedAt = now
            )
        }
    }
    
    var amount: BigDecimal = amount
        private set
    
    fun charge(chargeAmount: BigDecimal): PointHistory {
        if (chargeAmount <= BigDecimal.ZERO) {
            throw IllegalArgumentException("충전 금액은 0보다 커야 합니다.")
        }
        
        this.amount = this.amount.add(chargeAmount)
        
        return PointHistory.create(
            pointId = this.id,
            type = PointHistoryType.CHARGE,
            amount = chargeAmount,
            balance = this.amount
        )
    }
    
    fun use(useAmount: BigDecimal): PointHistory {
        if (useAmount <= BigDecimal.ZERO) {
            throw IllegalArgumentException("사용 금액은 0보다 커야 합니다.")
        }
        
        if (this.amount < useAmount) {
            throw IllegalArgumentException("포인트가 부족합니다.")
        }
        
        this.amount = this.amount.subtract(useAmount)
        
        return PointHistory.create(
            pointId = this.id,
            type = PointHistoryType.USE,
            amount = useAmount.negate(),
            balance = this.amount
        )
    }
}

@JvmInline
value class PointId(val value: String) {
    companion object {
        fun create(): PointId = PointId(UUID.randomUUID().toString())
    }
}

enum class PointHistoryType {
    CHARGE, USE
} 