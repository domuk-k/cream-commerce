package edu.creamcommerce.domain.point

import edu.creamcommerce.domain.coupon.UserId
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import java.math.BigDecimal

class PointTest : BehaviorSpec({
    given("포인트가 생성되었을 때") {
        val userId = UserId("test-user")
        val initialAmount = BigDecimal.valueOf(1000)
        val point = Point.create(userId = userId, amount = initialAmount)
        
        `when`("양수 금액을 충전하면") {
            val chargeAmount = BigDecimal.valueOf(500)
            val pointHistory = point.charge(chargeAmount)
            
            then("포인트 잔액이 증가하고 히스토리가 생성된다") {
                point.amount shouldBe BigDecimal.valueOf(1500)
                pointHistory.pointId shouldBe point.id
                pointHistory.type shouldBe PointHistoryType.CHARGE
                pointHistory.amount shouldBe chargeAmount
                pointHistory.balance shouldBe point.amount
            }
        }
        
        `when`("0 또는 음수 금액을 충전하면") {
            then("예외가 발생한다") {
                val exception = shouldThrow<IllegalArgumentException> {
                    point.charge(BigDecimal.ZERO)
                }
                exception.message shouldBe "충전 금액은 0보다 커야 합니다."
                
                val exception2 = shouldThrow<IllegalArgumentException> {
                    point.charge(BigDecimal.valueOf(-100))
                }
                exception2.message shouldBe "충전 금액은 0보다 커야 합니다."
            }
        }
        
        `when`("보유 금액보다 적은 금액을 사용하면") {
            val useAmount = BigDecimal.valueOf(300)
            val pointHistory = point.use(useAmount)
            
            then("포인트 잔액이 감소하고 히스토리가 생성된다") {
                point.amount shouldBe BigDecimal.valueOf(1200)
                pointHistory.pointId shouldBe point.id
                pointHistory.type shouldBe PointHistoryType.USE
                pointHistory.amount shouldBe useAmount.negate()
                pointHistory.balance shouldBe point.amount
            }
        }
        
        `when`("보유 금액보다 많은 금액을 사용하면") {
            then("예외가 발생한다") {
                val exception = shouldThrow<IllegalArgumentException> {
                    point.use(BigDecimal.valueOf(2000))
                }
                exception.message shouldBe "포인트가 부족합니다."
            }
        }
        
        `when`("0 또는 음수 금액을 사용하면") {
            then("예외가 발생한다") {
                val exception = shouldThrow<IllegalArgumentException> {
                    point.use(BigDecimal.ZERO)
                }
                exception.message shouldBe "사용 금액은 0보다 커야 합니다."
                
                val exception2 = shouldThrow<IllegalArgumentException> {
                    point.use(BigDecimal.valueOf(-100))
                }
                exception2.message shouldBe "사용 금액은 0보다 커야 합니다."
            }
        }
    }
}) 