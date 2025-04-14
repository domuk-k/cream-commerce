package edu.creamcommerce.domain.order

import edu.creamcommerce.domain.common.Money
import edu.creamcommerce.domain.product.ProductId
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import java.math.BigDecimal
import java.util.*

class OrderItemTest : BehaviorSpec({
    given("주문 항목이 생성될 때") {
        val productId = ProductId(UUID.randomUUID().toString())
        val productName = "테스트 상품"
        val price = Money(1000)
        val quantity = 2
        
        `when`("정상적인 수량으로 생성하면") {
            val orderItem = OrderItem.create(
                productId = productId,
                productName = productName,
                price = price,
                quantity = quantity
            )
            
            then("주문 항목이 올바르게 생성되어야 한다") {
                orderItem.productId shouldBe productId
                orderItem.productName shouldBe productName
                orderItem.price shouldBe price
                orderItem.quantity shouldBe quantity
            }
            
            then("총 가격이 올바르게 계산되어야 한다") {
                orderItem.totalPrice.amount shouldBe BigDecimal("2000")
            }
        }
        
        `when`("0 또는 음수 수량으로 생성하면") {
            then("예외가 발생해야 한다") {
                val exception = shouldThrow<IllegalArgumentException> {
                    OrderItem.create(
                        productId = productId,
                        productName = productName,
                        price = price,
                        quantity = 0
                    )
                }
                exception.message shouldBe "주문 수량은 0보다 커야 합니다."
                
                val exception2 = shouldThrow<IllegalArgumentException> {
                    OrderItem.create(
                        productId = productId,
                        productName = productName,
                        price = price,
                        quantity = -1
                    )
                }
                exception2.message shouldBe "주문 수량은 0보다 커야 합니다."
            }
        }
    }
}) 