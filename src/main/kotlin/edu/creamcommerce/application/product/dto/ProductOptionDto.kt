package edu.creamcommerce.application.product.dto

import edu.creamcommerce.domain.product.InventoryStatus
import edu.creamcommerce.domain.product.ProductOption
import java.math.BigDecimal

data class ProductOptionDto(
    val id: String,
    val name: String,
    val productId: String,
    val additionalPrice: BigDecimal,
    val stock: Int,
    val stockStatus: InventoryStatus
)

fun ProductOption.toDto(): ProductOptionDto {
    return ProductOptionDto(
        id = id.value,
        productId = productId.value,
        name = name,
        additionalPrice = additionalPrice.amount,
        stock = inventory.quantity,
        stockStatus = inventory.status
    )
}
