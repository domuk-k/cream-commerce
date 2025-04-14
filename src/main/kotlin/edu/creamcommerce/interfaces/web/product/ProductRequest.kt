package edu.creamcommerce.interfaces.web.product

import edu.creamcommerce.application.product.dto.command.CreateProductCommand
import edu.creamcommerce.application.product.dto.command.ProductOptionCommand
import jakarta.validation.Valid
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.PositiveOrZero
import java.math.BigDecimal

data class CreateProductRequest(
    @field:NotBlank(message = "상품명은 필수입니다.")
    val name: String,
    
    @field:Min(value = 0, message = "가격은 0 이상이어야 합니다.")
    val price: BigDecimal,
    
    @field:Valid
    val options: List<ProductOptionRequest> = emptyList()
) {
    fun toCommand(): CreateProductCommand {
        return CreateProductCommand(
            name = name,
            price = price,
            options = options.map { it.toCommand() }
        )
    }
}

data class ProductOptionRequest(
    @field:NotBlank(message = "옵션명은 필수입니다.")
    val name: String,
    
    @field:Min(value = 0, message = "추가 가격은 0 이상이어야 합니다.")
    val additionalPrice: BigDecimal,
    
    @field:PositiveOrZero(message = "재고는 0 이상이어야 합니다.")
    val stock: Int
) {
    fun toCommand(): ProductOptionCommand {
        return ProductOptionCommand(
            name = name,
            additionalPrice = additionalPrice,
            stock = stock
        )
    }
} 