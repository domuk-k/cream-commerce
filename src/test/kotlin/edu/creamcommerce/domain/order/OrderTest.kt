package edu.creamcommerce.domain.order

import edu.creamcommerce.domain.product.ProductId
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import java.math.BigDecimal
import java.util.*

class OrderTest : BehaviorSpec({
    given("주문이 생성되었을 때") {
        val userId = "test-user"
        val orderItems = listOf(
            OrderItem.create(
                productId = ProductId(UUID.randomUUID().toString()),
                productName = "테스트 상품",
                price = Money(1000),
                quantity = 2
            )
        )
        val shippingAddress = "서울시 강남구 테스트로 123"
        val order = Order.create(
            userId = userId,
            orderItems = orderItems,
            shippingAddress = shippingAddress
        )
        
        then("주문 상태는 PENDING 이어야 한다") {
            order.status shouldBe OrderStatus.PENDING
            order.isPending() shouldBe true
        }
        
        then("주문 총액이 올바르게 계산되어야 한다") {
            order.totalAmount.amount shouldBe BigDecimal("2000")
        }
        
        `when`("결제가 완료되면") {
            order.pay()
            
            then("주문 상태는 PAID로 변경되어야 한다") {
                order.status shouldBe OrderStatus.PAID
                order.isPaid() shouldBe true
            }
            
            `when`("배송 처리를 시작하면") {
                order.processShipping()
                
                then("주문 상태는 PROCESSING으로 변경되어야 한다") {
                    order.status shouldBe OrderStatus.PROCESSING
                }
                
                `when`("배송이 시작되면") {
                    order.ship()
                    
                    then("주문 상태는 SHIPPED로 변경되어야 한다") {
                        order.status shouldBe OrderStatus.SHIPPED
                        order.isShipped() shouldBe true
                    }
                    
                    `when`("배송이 완료되면") {
                        order.deliver()
                        
                        then("주문 상태는 DELIVERED로 변경되어야 한다") {
                            order.status shouldBe OrderStatus.DELIVERED
                        }
                        
                        `when`("구매 확정이 되면") {
                            order.complete()
                            
                            then("주문 상태는 COMPLETED로 변경되어야 한다") {
                                order.status shouldBe OrderStatus.COMPLETED
                                order.isCompleted() shouldBe true
                            }
                        }
                    }
                }
            }
        }
        
        `when`("주문이 취소되면") {
            val newOrder = Order.create(
                userId = userId,
                orderItems = orderItems,
                shippingAddress = shippingAddress
            )
            
            newOrder.cancel()
            
            then("주문 상태는 CANCELED로 변경되어야 한다") {
                newOrder.status shouldBe OrderStatus.CANCELED
                newOrder.isCanceled() shouldBe true
            }
        }
        
        `when`("결제 완료 후 환불 요청을 하면") {
            val refundOrder = Order.create(
                userId = userId,
                orderItems = orderItems,
                shippingAddress = shippingAddress
            )
            
            refundOrder.pay()
            refundOrder.requestRefund()
            
            then("주문 상태는 REFUNDING으로 변경되어야 한다") {
                refundOrder.status shouldBe OrderStatus.REFUNDING
            }
            
            `when`("환불이 완료되면") {
                refundOrder.refund()
                
                then("주문 상태는 REFUNDED로 변경되어야 한다") {
                    refundOrder.status shouldBe OrderStatus.REFUNDED
                    refundOrder.isRefunded() shouldBe true
                }
            }
        }
        
        `when`("잘못된 상태 전이를 시도하면") {
            then("예외가 발생해야 한다") {
                val exception = shouldThrow<IllegalStateException> {
                    // PENDING 상태에서 바로 배송 처리 시도
                    order.processShipping()
                }
                exception.message shouldBe "배송 처리는 PAID 상태에서만 가능합니다. 현재 상태: PENDING"
            }
        }
    }
}) 