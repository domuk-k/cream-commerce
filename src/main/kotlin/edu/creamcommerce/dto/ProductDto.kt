package edu.creamcommerce.dto

import java.math.BigDecimal
import java.time.LocalDateTime

data class ProductDto(
    val id: Long? = null,
    val name: String,
    val description: String,
    val price: BigDecimal,
    val stockQuantity: Int,
    val category: String,
    val imageUrl: String,
    val salesCount: Int = 0,
    val createdAt: LocalDateTime = LocalDateTime.now()
)

data class ProductListResponse(
    val products: List<ProductDto>,
    val totalCount: Int,
    val currentPage: Int,
    val totalPages: Int
)

data class TopSellingProductsResponse(
    val products: List<ProductDto>,
    val period: String
)