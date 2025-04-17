package edu.creamcommerce.interfaces.web

import com.fasterxml.jackson.databind.ObjectMapper
import edu.creamcommerce.application.product.dto.ProductDto
import edu.creamcommerce.application.product.dto.ProductListDto
import edu.creamcommerce.application.product.dto.query.GetProductsQuery
import edu.creamcommerce.application.product.facade.ProductFacade
import edu.creamcommerce.domain.product.ProductId
import edu.creamcommerce.domain.product.ProductStatus
import edu.creamcommerce.interfaces.web.product.CreateProductRequest
import edu.creamcommerce.interfaces.web.product.ProductController
import edu.creamcommerce.interfaces.web.product.ProductOptionRequest
import io.kotest.core.spec.style.ShouldSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import java.math.BigDecimal
import java.time.LocalDateTime

class ProductControllerTest : ShouldSpec({
    val productFacade = mockk<ProductFacade>()
    val productController = ProductController(productFacade)
    val mockMvc = MockMvcBuilders.standaloneSetup(productController).build()
    val objectMapper = ObjectMapper()
    
    context("상품 목록 조회") {
        should("GET /api/products 요청 시 상품 목록을 반환한다") {
            // given
            val productsDto = ProductListDto(
                products = listOf(
                    createTestProductDto("1"),
                    createTestProductDto("2")
                ),
                total = 2,
                page = 0,
                size = 10
            )
            
            every { productFacade.getProducts(GetProductsQuery(0, 10)) } returns productsDto
            
            // when & then
            mockMvc.perform(
                get("/api/products")
                    .param("page", "0")
                    .param("size", "10")
                    .contentType(MediaType.APPLICATION_JSON)
            )
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.data.products.length()").value(2))
                .andExpect(jsonPath("$.data.total").value(2))
                .andExpect(jsonPath("$.data.page").value(0))
                .andExpect(jsonPath("$.data.size").value(10))
            
            verify { productFacade.getProducts(GetProductsQuery(0, 10)) }
        }
    }
    
    context("상품 상세 조회") {
        should("GET /api/products/{id} 요청 시 상품 상세 정보를 반환한다") {
            // given
            val productId = "test-id"
            val productDto = createTestProductDto(productId)
            
            every { productFacade.getProductById(ProductId(productId)) } returns productDto
            
            // when & then
            mockMvc.perform(
                get("/api/products/{id}", productId)
                    .contentType(MediaType.APPLICATION_JSON)
            )
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.data.id").value(productId))
                .andExpect(jsonPath("$.data.name").value("테스트 상품"))
            
            verify { productFacade.getProductById(ProductId(productId)) }
        }
    }
    
    context("상품 생성") {
        should("POST /api/products 요청 시 상품을 생성하고 생성된 상품 정보를 반환한다") {
            // given
            val request = CreateProductRequest(
                name = "새 상품",
                price = BigDecimal.valueOf(20000),
                options = listOf(
                    ProductOptionRequest(
                        name = "옵션1",
                        additionalPrice = BigDecimal.valueOf(1000),
                        stock = 10
                    )
                )
            )
            
            val createdProductDto = createTestProductDto("new-id", "새 상품", BigDecimal.valueOf(20000))
            
            every { productFacade.createProduct(any()) } returns createdProductDto
            
            // when & then
            mockMvc.perform(
                post("/api/products")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.data.id").value("new-id"))
                .andExpect(jsonPath("$.data.name").value("새 상품"))
                .andExpect(jsonPath("$.data.price").value(20000))
            
            verify { productFacade.createProduct(any()) }
        }
    }
}) {
    companion object {
        fun createTestProductDto(
            id: String,
            name: String = "테스트 상품",
            price: BigDecimal = BigDecimal.valueOf(10000)
        ): ProductDto {
            return ProductDto(
                id = id,
                name = name,
                description = "",
                price = price,
                status = ProductStatus.Active,
                options = emptyList(),
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now(),
                stockStatus = "InStock"
            )
        }
    }
}

infix fun <T> T.shouldBe(expected: T) {
    if (this != expected) {
        throw AssertionError("Expected: $expected but was: $this")
    }
}