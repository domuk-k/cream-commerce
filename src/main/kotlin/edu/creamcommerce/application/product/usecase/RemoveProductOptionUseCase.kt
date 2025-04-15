package edu.creamcommerce.application.product.usecase

import edu.creamcommerce.application.product.dto.ProductDto
import edu.creamcommerce.application.product.dto.toDto
import edu.creamcommerce.domain.product.OptionId
import edu.creamcommerce.domain.product.ProductId
import edu.creamcommerce.domain.product.ProductRepository
import org.springframework.stereotype.Service

@Service
class RemoveProductOptionUseCase(
    private val productRepository: ProductRepository
) {
    operator fun invoke(productId: ProductId, optionId: OptionId): ProductDto {
        val product = productRepository.findById(productId)
            ?: throw NoSuchElementException("상품을 찾을 수 없습니다: ${productId.value}")
        
        val updatedProduct = product.removeOption(optionId)
        val savedProduct = productRepository.save(updatedProduct)
        return savedProduct.toDto()
    }
} 