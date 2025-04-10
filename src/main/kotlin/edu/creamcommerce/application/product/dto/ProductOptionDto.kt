package edu.creamcommerce.application.product.dto

import edu.creamcommerce.domain.product.Money
import edu.creamcommerce.domain.product.ProductOption

data class ProductOptionDto(
    val id: String,
    val name: String,
    val additionalPrice: Money,
    val stock: Int
)

fun ProductOption.toDto(): ProductOptionDto = ProductOptionDto(
    id = this.id.value,
    name = this.name,
    additionalPrice = this.additionalPrice,
    stock = this.stock
)
