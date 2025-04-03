package edu.creamcommerce.interfaces.product

import edu.creamcommerce.dto.ProductDto
import edu.creamcommerce.dto.ProductListResponse
import edu.creamcommerce.dto.TopSellingProductsResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.math.BigDecimal
import java.time.LocalDateTime

@RestController
@RequestMapping("/api/products")
@Tag(name = "상품 API", description = "상품 조회 관련 API")
class ProductController {

    private val mockProducts = listOf(
        ProductDto(
            id = 1L,
            name = "프리미엄 티셔츠",
            description = "고품질 면 소재로 제작된 편안한 티셔츠",
            price = BigDecimal("29.99"),
            stockQuantity = 100,
            category = "의류",
            salesCount = 120,
            imageUrl = "https://example.com/images/tshirt.jpg"
        ),
        ProductDto(
            id = 2L,
            name = "디자이너 청바지",
            description = "최신 트렌드의 슬림핏 청바지",
            price = BigDecimal("79.99"),
            stockQuantity = 50,
            category = "의류",
            salesCount = 85,
            imageUrl = "https://example.com/images/jeans.jpg"
        ),
        ProductDto(
            id = 3L,
            name = "스마트워치",
            description = "건강 모니터링 기능이 탑재된 스마트워치",
            price = BigDecimal("159.99"),
            stockQuantity = 30,
            category = "전자기기",
            salesCount = 200,
            imageUrl = "https://example.com/images/watch.jpg"
        )
    )

    @GetMapping
    @Operation(summary = "상품 목록 조회", description = "카테고리와 페이지 정보에 따른 상품 목록을 반환합니다.")
    fun getProducts(
        @RequestParam(required = false) category: String?,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int
    ): ResponseEntity<ProductListResponse> {
        val filteredProducts = if (category != null) {
            mockProducts.filter { it.category == category }
        } else {
            mockProducts
        }

        return ResponseEntity.ok(
            ProductListResponse(
                products = filteredProducts,
                totalCount = filteredProducts.size,
                currentPage = page,
                totalPages = (filteredProducts.size + size - 1) / size
            )
        )
    }

    @GetMapping("/{id}")
    @Operation(summary = "상품 상세 조회", description = "상품 ID에 해당하는 상세 정보를 반환합니다.")
    fun getProduct(@PathVariable id: Long): ResponseEntity<ProductDto> {
        val product = mockProducts.find { it.id == id }
        return if (product != null) {
            ResponseEntity.ok(product)
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @GetMapping("/top-selling")
    @Operation(summary = "인기 판매 상품 조회", description = "판매량이 많은 인기 상품을 조회합니다.")
    fun getTopSellingProducts(
        @RequestParam(defaultValue = "10") limit: Int,
        @RequestParam(defaultValue = "WEEKLY") period: String
    ): ResponseEntity<TopSellingProductsResponse> {
        val sortedProducts = mockProducts.sortedByDescending { it.salesCount }
            .take(limit)

        return ResponseEntity.ok(
            TopSellingProductsResponse(
                products = sortedProducts,
                period = period
            )
        )
    }
}