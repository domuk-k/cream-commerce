package edu.creamcommerce.application.product.dto

data class ProductListDto(
    val products: List<ProductDto>,
    val total: Int,
    val page: Int,
    val size: Int
)