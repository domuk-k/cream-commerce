package edu.creamcommerce.application.product.facade

import edu.creamcommerce.application.product.dto.ProductDto
import edu.creamcommerce.application.product.dto.ProductListDto
import edu.creamcommerce.application.product.dto.ProductOptionDto
import edu.creamcommerce.application.product.dto.command.AddProductOptionCommand
import edu.creamcommerce.application.product.dto.command.CreateProductCommand
import edu.creamcommerce.application.product.dto.command.UpdateProductCommand
import edu.creamcommerce.application.product.dto.command.UpdateProductStatusCommand
import edu.creamcommerce.application.product.dto.query.GetProductsQuery
import edu.creamcommerce.application.product.dto.query.GetTopProductsQuery
import edu.creamcommerce.application.product.usecase.*
import edu.creamcommerce.domain.product.OptionId
import edu.creamcommerce.domain.product.ProductId
import org.springframework.stereotype.Service

@Service
class ProductFacade(
    private val getProductByIdUseCase: GetProductByIdUseCase,
    private val getProductsUseCase: GetProductsUseCase,
    private val createProductUseCase: CreateProductUseCase,
    private val updateProductStatusUseCase: UpdateProductStatusUseCase,
    private val addProductOptionUseCase: AddProductOptionUseCase,
    private val removeProductOptionUseCase: RemoveProductOptionUseCase,
    private val updateProductUseCase: UpdateProductUseCase,
    private val getTopProductsUseCase: GetTopProductsUseCase
) {
    fun getProductById(id: ProductId): ProductDto {
        return getProductByIdUseCase(id) ?: throw NoSuchElementException("상품을 찾을 수 없습니다.")
    }
    
    fun getProducts(query: GetProductsQuery): ProductListDto {
        return getProductsUseCase(query)
    }
    
    fun createProduct(command: CreateProductCommand): ProductDto {
        return createProductUseCase(command)
    }
    
    fun updateProductStatus(id: ProductId, command: UpdateProductStatusCommand): ProductDto {
        return updateProductStatusUseCase(id, command)
    }
    
    fun addProductOption(id: ProductId, command: AddProductOptionCommand): ProductOptionDto {
        return addProductOptionUseCase(id, command)
    }
    
    fun removeProductOption(productId: ProductId, optionId: OptionId): ProductDto {
        return removeProductOptionUseCase(productId, optionId)
    }
    
    fun updateProduct(id: ProductId, command: UpdateProductCommand): ProductDto {
        return updateProductUseCase(id, command)
    }
    
    fun getTopProducts(query: GetTopProductsQuery): List<ProductDto> {
        return getTopProductsUseCase(query)
    }
} 