package edu.creamcommerce.application.product.usecase

import edu.creamcommerce.application.product.dto.ProductDto
import edu.creamcommerce.application.product.dto.toDto
import edu.creamcommerce.domain.product.ProductId
import edu.creamcommerce.domain.product.ProductRepository
import org.springframework.stereotype.Component

@Component
class GetProductByIdUseCase(
    private val productRepository: ProductRepository
) {
    operator fun invoke(id: ProductId): ProductDto? = productRepository.findById(id)?.toDto()
}