package edu.creamcommerce.application.product.dto

import edu.creamcommerce.domain.product.Product
import java.math.BigDecimal
import java.time.LocalDateTime

data class ProductDto(
    val id: String,
    val name: String,
    val description: String,
    val price: BigDecimal,
    val status: String,
    val stockStatus: String,
    val options: List<ProductOptionDto>,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val salesCount: Int? = null,
    val imageUrl: String? = null
)

fun Product.toDto(): ProductDto {
    return ProductDto(
        id = this.id.value,
        name = this.name,
        description = this.description,
        price = this.price.amount,
        status = this.status.name,
        stockStatus = this.stockStatus.name,
        options = this.options.map { it.toDto() },
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )
}


