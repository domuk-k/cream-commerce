package edu.creamcommerce.application.product.usecase

import edu.creamcommerce.application.product.dto.ProductDto
import edu.creamcommerce.application.product.dto.command.UpdateProductStatusCommand
import edu.creamcommerce.application.product.dto.toDto
import edu.creamcommerce.domain.product.ProductId
import edu.creamcommerce.domain.product.ProductRepository
import edu.creamcommerce.domain.product.ProductStatus
import org.springframework.stereotype.Service

@Service
class UpdateProductStatusUseCase(
    private val productRepository: ProductRepository
) {
    operator fun invoke(productId: ProductId, command: UpdateProductStatusCommand): ProductDto {
        val product = productRepository.findById(productId)
            ?: throw NoSuchElementException("상품을 찾을 수 없습니다: ${productId.value}")
        
        val updatedProduct = when (command.status) {
            ProductStatus.Active -> product.activate()
            ProductStatus.Suspended -> product.suspend()
            ProductStatus.Discontinued -> product.discontinue()
            ProductStatus.Draft -> throw IllegalStateException("상품을 초안 상태로 변경할 수 없습니다.")
        }
        
        val savedProduct = productRepository.save(updatedProduct)
        return savedProduct.toDto()
    }
} 