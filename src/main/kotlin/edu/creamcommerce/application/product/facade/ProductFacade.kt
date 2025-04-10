package edu.creamcommerce.application.product.facade

import edu.creamcommerce.application.product.dto.ProductDto
import edu.creamcommerce.application.product.dto.ProductListDto
import edu.creamcommerce.application.product.dto.command.CreateProductCommand
import edu.creamcommerce.application.product.dto.query.GetProductsQuery
import edu.creamcommerce.application.product.usecase.CreateProductUseCase
import edu.creamcommerce.application.product.usecase.GetProductByIdUseCase
import edu.creamcommerce.application.product.usecase.GetProductsUseCase
import edu.creamcommerce.domain.product.ProductId
import org.springframework.stereotype.Service

@Service
class ProductFacade(
    private val getProductByIdUseCase: GetProductByIdUseCase,
    private val getProductsUseCase: GetProductsUseCase,
    private val createProductUseCase: CreateProductUseCase
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
} 