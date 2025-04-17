package edu.creamcommerce.application.product.usecase

import edu.creamcommerce.application.product.dto.ProductDto
import edu.creamcommerce.application.product.dto.command.CreateProductCommand
import edu.creamcommerce.application.product.dto.toDto
import edu.creamcommerce.domain.common.Money
import edu.creamcommerce.domain.product.Product
import edu.creamcommerce.domain.product.ProductOption
import edu.creamcommerce.domain.product.ProductRepository
import org.springframework.stereotype.Component
import java.math.BigDecimal

@Component
class CreateProductUseCase(
    private val productRepository: ProductRepository
) {
    operator fun invoke(command: CreateProductCommand): ProductDto {
        
        val product = Product.create(
            name = command.name,
            price = command.toMoney(),
        )
        
        command.options.forEach { optionCommand ->
            product.addOption(
                ProductOption.create(
                    name = optionCommand.name,
                    additionalPrice = Money(optionCommand.additionalPrice ?: BigDecimal.ZERO),
                    stock = optionCommand.stock,
                    sku = optionCommand.sku,
                    productId = product.id
                )
            )
        }
        
        val savedProduct = productRepository.save(product)
        
        return savedProduct.toDto()
    }
} 