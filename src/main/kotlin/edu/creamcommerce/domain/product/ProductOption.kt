package edu.creamcommerce.domain.product

import edu.creamcommerce.domain.common.Money
import java.util.*

class ProductOption(
    val id: OptionId,
    val name: String,
    val stock: Int,
    val additionalPrice: Money = Money(0),
) {
    companion object {
        fun create(name: String, additionalPrice: Money, stock: Int): ProductOption {
            return ProductOption(
                id = OptionId.create(),
                name = name,
                additionalPrice = additionalPrice,
                stock = stock
            )
        }
    }
}

@JvmInline
value class OptionId(val value: String) {
    companion object {
        fun create(): OptionId = OptionId(UUID.randomUUID().toString())
    }
}