package edu.creamcommerce.interfaces.web.product

import edu.creamcommerce.application.product.dto.ProductDto
import edu.creamcommerce.application.product.dto.ProductListDto
import edu.creamcommerce.application.product.dto.ProductOptionDto
import edu.creamcommerce.application.product.dto.query.GetProductsQuery
import edu.creamcommerce.application.product.dto.query.GetTopProductsQuery
import edu.creamcommerce.application.product.dto.query.TopProductPeriod
import edu.creamcommerce.application.product.facade.ProductFacade
import edu.creamcommerce.domain.product.OptionId
import edu.creamcommerce.domain.product.ProductId
import edu.creamcommerce.interfaces.web.ApiResponse
import edu.creamcommerce.interfaces.web.toSuccessResponse
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
    @Operation(summary = "상품 생성", description = "새로운 상품을 생성합니다.")
    fun createProduct(@RequestBody @Valid request: CreateProductRequest): ResponseEntity<ApiResponse<ProductDto>> {
        val createdProduct = productFacade.createProduct(request.toCommand())
        return createdProduct.toSuccessResponse("상품이 생성되었습니다.")
    }
    
    @PatchMapping("/{id}/status")
    @Operation(summary = "상품 상태 변경", description = "상품의 상태를 변경합니다. (Active, Suspended, Discontinued)")
    fun updateProductStatus(
        @PathVariable id: String,
        @RequestBody @Valid request: UpdateProductStatusRequest
    ): ResponseEntity<ApiResponse<ProductDto>> {
        val updatedProduct = productFacade.updateProductStatus(
            ProductId(id),
            request.toCommand()
        )
        return updatedProduct.toSuccessResponse("상품 상태가 변경되었습니다.")
    }
    
    @PostMapping("/{id}/options")
    @Operation(summary = "상품 옵션 추가", description = "상품에 새로운 옵션을 추가합니다.")
    fun addProductOption(
        @PathVariable id: String,
        @RequestBody @Valid request: AddProductOptionRequest
    ): ResponseEntity<ApiResponse<ProductOptionDto>> {
        val updatedProduct = productFacade.addProductOption(
            ProductId(id),
            request.toCommand()
        )
        return updatedProduct.toSuccessResponse("상품 옵션이 추가되었습니다.")
    }
    
    @DeleteMapping("/{id}/options/{optionId}")
    @Operation(summary = "상품 옵션 제거", description = "상품에서 지정된 옵션을 제거합니다.")
    fun removeProductOption(
        @PathVariable id: String,
        @PathVariable optionId: String
    ): ResponseEntity<ApiResponse<ProductDto>> {
        val updatedProduct = productFacade.removeProductOption(
            ProductId(id),
            OptionId(optionId)
        )
        return updatedProduct.toSuccessResponse("상품 옵션이 제거되었습니다.")
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "상품 정보 수정", description = "상품의 기본 정보(이름, 설명, 가격)를 수정합니다.")
    fun updateProduct(
        @PathVariable id: String,
        @RequestBody @Valid request: UpdateProductRequest
    ): ResponseEntity<ApiResponse<ProductDto>> {
        val updatedProduct = productFacade.updateProduct(
            ProductId(id),
            request.toCommand()
        )
        return updatedProduct.toSuccessResponse("상품 정보가 수정되었습니다.")
    }
    
    @GetMapping("/top")
    @Operation(summary = "인기 상품 조회", description = "판매량이 높은 인기 상품 목록을 조회합니다.")
    fun getTopProducts(
        @RequestParam(defaultValue = "10") limit: Int,
        @RequestParam(defaultValue = "ALL_TIME") period: String
    ): ResponseEntity<ApiResponse<List<ProductDto>>> {
        val topProductPeriod = try {
            TopProductPeriod.valueOf(period.uppercase())
        } catch (e: IllegalArgumentException) {
            TopProductPeriod.ALL_TIME
        }
        
        val topProducts = productFacade.getTopProducts(
            GetTopProductsQuery(limit, topProductPeriod)
        )
        return topProducts.toSuccessResponse()
    }
}