package edu.creamcommerce.domain.order

import edu.creamcommerce.domain.common.Money
import edu.creamcommerce.domain.product.OptionId
import edu.creamcommerce.domain.product.ProductId
import java.time.LocalDateTime
import java.util.*

class OrderItem private constructor(
    val id: OrderItemId,
    val productId: ProductId,
    val optionId: OptionId,
    val productName: String,
    val optionName: String,
    val optionSku: String,
    val price: Money,
    val quantity: Int,
    val createdAt: LocalDateTime
) {
    companion object {
        fun create(
            id: OrderItemId? = null,
            productId: ProductId,
            optionId: OptionId,
            productName: String,
            optionName: String,
            optionSku: String,
            price: Money,
            quantity: Int,
            createdAt: LocalDateTime? = null
        ): OrderItem {
            if (quantity <= 0) {
                throw IllegalArgumentException("주문 수량은 0보다 커야 합니다.")
            }
            
            return OrderItem(
                id = id ?: OrderItemId.create(),
                productId = productId,
                optionId = optionId,
                productName = productName,
                optionName = optionName,
                optionSku = optionSku,
                price = price,
                quantity = quantity,
                createdAt = createdAt ?: LocalDateTime.now()
            )
        }
    }
    
    val totalPrice: Money get() = price * quantity
}

@JvmInline
value class OrderItemId(val value: String) {
    companion object {
        fun create(): OrderItemId = OrderItemId(UUID.randomUUID().toString())
    }
} 