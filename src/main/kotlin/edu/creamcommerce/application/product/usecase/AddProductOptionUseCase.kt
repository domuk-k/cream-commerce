package edu.creamcommerce.application.product.usecase

import edu.creamcommerce.application.product.dto.ProductOptionDto
import edu.creamcommerce.application.product.dto.command.AddProductOptionCommand
import edu.creamcommerce.application.product.dto.toDto
import edu.creamcommerce.domain.common.Money
import edu.creamcommerce.domain.product.ProductId
import edu.creamcommerce.domain.product.ProductOption
import edu.creamcommerce.domain.product.ProductRepository
import org.springframework.stereotype.Service

@Service
class AddProductOptionUseCase(
    private val productRepository: ProductRepository
) {
    operator fun invoke(productId: ProductId, command: AddProductOptionCommand): ProductOptionDto {
        val product = productRepository.findById(productId)
            ?: throw NoSuchElementException("상품을 찾을 수 없습니다: ${productId.value}")
        
        val option = ProductOption.create(
            name = command.name,
            additionalPrice = Money(command.additionalPrice),
            stock = command.stock,
            sku = command.sku,
            productId = productId
        )
        
        val updatedProduct = product.addOption(option)
        productRepository.save(updatedProduct)
        return option.toDto()
    }
} 