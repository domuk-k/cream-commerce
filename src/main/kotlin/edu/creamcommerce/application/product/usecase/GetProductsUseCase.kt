package edu.creamcommerce.application.product.usecase

import edu.creamcommerce.application.product.dto.ProductListDto
import edu.creamcommerce.application.product.dto.query.GetProductsQuery
import edu.creamcommerce.application.product.dto.toDto
import edu.creamcommerce.domain.product.ProductRepository
import org.springframework.stereotype.Component

@Component
class GetProductsUseCase(
    private val productRepository: ProductRepository
) {
    operator fun invoke(query: GetProductsQuery): ProductListDto {
        val products = productRepository.findAll()
        val start = query.page * query.size
        val end = (start + query.size).coerceAtMost(products.size)
        
        val pagedProducts = if (start < products.size) {
            products.subList(start, end)
        } else {
            emptyList()
        }
        
        return ProductListDto(
            products = pagedProducts.map { it.toDto() },
            total = products.size,
            page = query.page,
            size = query.size
        )
    }
} 