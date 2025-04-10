package edu.creamcommerce.application.product.dto.command

import edu.creamcommerce.domain.product.Money
import edu.creamcommerce.domain.product.ProductOption
import java.math.BigDecimal

data class CreateProductCommand(
    val name: String,
    val price: BigDecimal,
    val options: List<ProductOptionCommand> = emptyList()
) {
    fun toMoney(): Money = Money(price)
}

data class ProductOptionCommand(
    val name: String,
    val additionalPrice: BigDecimal?,
    val stock: Int
)

fun ProductOptionCommand.toDomain(): ProductOption = ProductOption.create(
    name = this.name,
    additionalPrice = Money(this.additionalPrice ?: BigDecimal.ZERO),
    stock = this.stock
)