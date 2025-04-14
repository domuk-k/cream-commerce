package edu.creamcommerce.application.product.usecase

import edu.creamcommerce.application.product.dto.ProductDto
import edu.creamcommerce.application.product.dto.command.UpdateProductCommand
import edu.creamcommerce.application.product.dto.toDto
import edu.creamcommerce.domain.common.Money
import edu.creamcommerce.domain.product.ProductId
import edu.creamcommerce.domain.product.ProductRepository
import org.springframework.stereotype.Service

@Service
class UpdateProductUseCase(
    private val productRepository: ProductRepository
) {
    operator fun invoke(productId: ProductId, command: UpdateProductCommand): ProductDto {
        val product = productRepository.findById(productId)
            ?: throw NoSuchElementException("상품을 찾을 수 없습니다: ${productId.value}")
        
        val moneyPrice = command.price?.let { Money(it) }
        val updatedProduct = product.update(
            name = command.name,
            description = command.description,
            price = moneyPrice
        )
        
        val savedProduct = productRepository.save(updatedProduct)
        return savedProduct.toDto()
    }
} 