package edu.creamcommerce.domain.payment

import edu.creamcommerce.domain.common.Money
import edu.creamcommerce.domain.order.OrderId
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import java.util.*

class PaymentTest : BehaviorSpec({
    given("결제가 생성되었을 때") {
        val orderId = OrderId(UUID.randomUUID().toString())
        val amount = Money(2000)
        val payment = Payment.create(
            orderId = orderId,
            amount = amount
        )
        
        then("결제 상태는 READY 이어야 한다") {
            payment.status shouldBe PaymentStatus.READY
            payment.isReady() shouldBe true
        }
        
        `when`("결제 처리가 시작되면") {
            payment.process()
            
            then("결제 상태는 PROCESSING으로 변경되어야 한다") {
                payment.status shouldBe PaymentStatus.PROCESSING
                payment.isProcessing() shouldBe true
            }
            
            `when`("결제가 완료되면") {
                payment.complete()
                
                then("결제 상태는 COMPLETED로 변경되어야 한다") {
                    payment.status shouldBe PaymentStatus.COMPLETED
                    payment.isCompleted() shouldBe true
                }
                
                `when`("환불 요청이 들어오면") {
                    payment.requestRefund()
                    
                    then("결제 상태는 REFUNDING으로 변경되어야 한다") {
                        payment.status shouldBe PaymentStatus.REFUNDING
                        payment.isRefunding() shouldBe true
                    }
                    
                    `when`("환불이 완료되면") {
                        payment.refund()
                        
                        then("결제 상태는 REFUNDED로 변경되어야 한다") {
                            payment.status shouldBe PaymentStatus.REFUNDED
                            payment.isRefunded() shouldBe true
                        }
                    }
                }
            }
            
            `when`("결제가 실패하면") {
                val newPayment = Payment.create(
                    orderId = orderId,
                    amount = amount
                ).process()
                
                val failReason = "잔액 부족"
                val failure = newPayment.fail(failReason)
                
                then("결제 상태는 FAILED로 변경되어야 한다") {
                    newPayment.status shouldBe PaymentStatus.FAILED
                    newPayment.isFailed() shouldBe true
                }
                
                then("실패 정보가 생성되어야 한다") {
                    failure.shouldBeInstanceOf<PaymentFailure>()
                    failure.paymentId shouldBe newPayment.id
                    failure.reason shouldBe failReason
                }
                
                `when`("실패 후 재시도하면") {
                    newPayment.retry()
                    
                    then("결제 상태는 READY로 변경되어야 한다") {
                        newPayment.status shouldBe PaymentStatus.READY
                        newPayment.isReady() shouldBe true
                    }
                }
            }
        }
        
        `when`("결제를 취소하면") {
            val cancelPayment = Payment.create(
                orderId = orderId,
                amount = amount
            ).cancel()
            
            then("결제 상태는 CANCELED로 변경되어야 한다") {
                cancelPayment.status shouldBe PaymentStatus.CANCELED
                cancelPayment.isCanceled() shouldBe true
            }
        }
        
        `when`("잘못된 상태 전이를 시도하면") {
            then("예외가 발생해야 한다") {
                val exception = shouldThrow<IllegalStateException> {
                    // READY 상태에서 바로 완료 처리 시도
                    payment.complete()
                }
                exception.message shouldBe "결제 완료는 PROCESSING 상태에서만 가능합니다. 현재 상태: READY"
            }
        }
    }
}) 