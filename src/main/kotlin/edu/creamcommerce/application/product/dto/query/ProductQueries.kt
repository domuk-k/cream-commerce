package edu.creamcommerce.application.product.dto.query

data class GetProductsQuery(
    val page: Int = 0,
    val size: Int = 10
)

data class GetTopProductsQuery(
    val limit: Int = 10,
    val period: TopProductPeriod = TopProductPeriod.ALL_TIME
)

enum class TopProductPeriod {
    DAILY, WEEKLY, MONTHLY, ALL_TIME
}

