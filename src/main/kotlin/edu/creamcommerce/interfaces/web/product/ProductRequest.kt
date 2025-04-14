package edu.creamcommerce.interfaces.web.product

import edu.creamcommerce.application.product.dto.command.*
import edu.creamcommerce.domain.product.ProductStatus
import jakarta.validation.Valid
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
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

data class UpdateProductStatusRequest(
    @field:NotNull(message = "상태는 필수입니다.")
    val status: String
) {
    fun toCommand(): UpdateProductStatusCommand {
        return UpdateProductStatusCommand(
            status = when (status.uppercase()) {
                "ACTIVE" -> ProductStatus.Active
                "SUSPENDED" -> ProductStatus.Suspended
                "DISCONTINUED" -> ProductStatus.Discontinued
                else -> throw IllegalArgumentException("유효하지 않은 상품 상태입니다: $status")
            }
        )
    }
}

data class AddProductOptionRequest(
    @field:NotBlank(message = "옵션명은 필수입니다.")
    val name: String,
    
    @field:Min(value = 0, message = "추가 가격은 0 이상이어야 합니다.")
    val additionalPrice: BigDecimal,
    
    @field:PositiveOrZero(message = "재고는 0 이상이어야 합니다.")
    val stock: Int
) {
    fun toCommand(): AddProductOptionCommand {
        return AddProductOptionCommand(
            name = name,
            additionalPrice = additionalPrice,
            stock = stock
        )
    }
}

data class UpdateProductRequest(
    val name: String? = null,
    val description: String? = null,
    
    @field:Min(value = 0, message = "가격은 0 이상이어야 합니다.")
    val price: BigDecimal? = null
) {
    fun toCommand(): UpdateProductCommand {
        return UpdateProductCommand(
            name = name,
            description = description,
            price = price
        )
    }
} 