package edu.creamcommerce.application.product.dto.query

import edu.creamcommerce.domain.product.ProductId

data class GetProductByIdQuery(val productId: ProductId)

data class GetProductsQuery(val page: Int = 0, val size: Int = 10) 