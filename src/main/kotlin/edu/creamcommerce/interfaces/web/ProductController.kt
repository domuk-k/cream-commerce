package edu.creamcommerce.interfaces.web

import edu.creamcommerce.application.product.dto.ProductDto
import edu.creamcommerce.application.product.dto.ProductListDto
import edu.creamcommerce.application.product.dto.query.GetProductsQuery
import edu.creamcommerce.application.product.facade.ProductFacade
import edu.creamcommerce.domain.product.ProductId
import edu.creamcommerce.interfaces.request.CreateProductRequest
import edu.creamcommerce.interfaces.response.ApiResponse
import edu.creamcommerce.interfaces.response.toSuccessResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/products")
@Tag(name = "상품 API", description = "상품 조회 관련 API")
class ProductController(
    private val productFacade: ProductFacade
) {
    @GetMapping("/{id}")
    @Operation(summary = "상품 상세 조회", description = "상품 ID에 해당하는 상세 정보를 반환합니다.")
    fun getProduct(@PathVariable id: String): ResponseEntity<ApiResponse<ProductDto>> {
        val product = productFacade.getProductById(ProductId(id))
        return product.toSuccessResponse()
    }
    
    @GetMapping
    @Operation(summary = "상품 목록 조회", description = "페이지 정보에 따른 상품 목록을 반환합니다.")
    fun listProducts(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int
    ): ResponseEntity<ApiResponse<ProductListDto>> {
        val productsDto = productFacade.getProducts(GetProductsQuery(page, size))
        return productsDto.toSuccessResponse()
    }
    
    @PostMapping
    fun createProduct(@RequestBody @Valid request: CreateProductRequest): ResponseEntity<ApiResponse<ProductDto>> {
        val createdProduct = productFacade.createProduct(request.toCommand())
        return createdProduct.toSuccessResponse("상품이 생성되었습니다.")
    }
}