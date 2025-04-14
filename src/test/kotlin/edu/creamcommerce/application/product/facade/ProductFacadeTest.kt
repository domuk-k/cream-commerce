package edu.creamcommerce.application.product.facade

import edu.creamcommerce.application.product.dto.ProductDto
import edu.creamcommerce.application.product.dto.command.AddProductOptionCommand
import edu.creamcommerce.application.product.dto.command.UpdateProductCommand
import edu.creamcommerce.application.product.dto.command.UpdateProductStatusCommand
import edu.creamcommerce.application.product.dto.query.GetTopProductsQuery
import edu.creamcommerce.application.product.dto.query.TopProductPeriod
import edu.creamcommerce.application.product.usecase.*
import edu.creamcommerce.domain.product.OptionId
import edu.creamcommerce.domain.product.ProductId
import edu.creamcommerce.domain.product.ProductStatus
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.math.BigDecimal
import java.time.LocalDateTime

class ProductFacadeTest : BehaviorSpec({
    val getProductByIdUseCase = mockk<GetProductByIdUseCase>()
    val getProductsUseCase = mockk<GetProductsUseCase>()
    val createProductUseCase = mockk<CreateProductUseCase>()
    val updateProductStatusUseCase = mockk<UpdateProductStatusUseCase>()
    val addProductOptionUseCase = mockk<AddProductOptionUseCase>()
    val removeProductOptionUseCase = mockk<RemoveProductOptionUseCase>()
    val updateProductUseCase = mockk<UpdateProductUseCase>()
    val getTopProductsUseCase = mockk<GetTopProductsUseCase>()
    
    val productFacade = ProductFacade(
        getProductByIdUseCase = getProductByIdUseCase,
        getProductsUseCase = getProductsUseCase,
        createProductUseCase = createProductUseCase,
        updateProductStatusUseCase = updateProductStatusUseCase,
        addProductOptionUseCase = addProductOptionUseCase,
        removeProductOptionUseCase = removeProductOptionUseCase,
        updateProductUseCase = updateProductUseCase,
        getTopProductsUseCase = getTopProductsUseCase
    )
    
    given("상품 ID가 주어졌을 때") {
        val productId = ProductId("test-product-id")
        val productDto = mockProductDto(productId.value)
        
        `when`("getProductById 메서드가 호출되면") {
            every { getProductByIdUseCase(productId) } returns productDto
            
            then("상품 DTO를 반환해야 한다") {
                val result = productFacade.getProductById(productId)
                result shouldBe productDto
                verify { getProductByIdUseCase(productId) }
            }
        }
    }
    
    given("상품 상태 변경 요청이 들어왔을 때") {
        val productId = ProductId("test-product-id")
        val command = UpdateProductStatusCommand(ProductStatus.Suspended)
        val updatedProductDto = mockProductDto(productId.value, status = ProductStatus.Suspended)
        
        `when`("updateProductStatus 메서드가 호출되면") {
            every { updateProductStatusUseCase(productId, command) } returns updatedProductDto
            
            then("상태가 변경된 상품 DTO를 반환해야 한다") {
                val result = productFacade.updateProductStatus(productId, command)
                result shouldBe updatedProductDto
                verify { updateProductStatusUseCase(productId, command) }
            }
        }
    }
    
    given("상품 옵션 추가 요청이 들어왔을 때") {
        val productId = ProductId("test-product-id")
        val command = AddProductOptionCommand("옵션1", BigDecimal("1000"), 100)
        val updatedProductDto = mockProductDto(productId.value)
        
        `when`("addProductOption 메서드가 호출되면") {
            every { addProductOptionUseCase(productId, command) } returns updatedProductDto
            
            then("옵션이 추가된 상품 DTO를 반환해야 한다") {
                val result = productFacade.addProductOption(productId, command)
                result shouldBe updatedProductDto
                verify { addProductOptionUseCase(productId, command) }
            }
        }
    }
    
    given("상품 옵션 제거 요청이 들어왔을 때") {
        val productId = ProductId("test-product-id")
        val optionId = OptionId("test-option-id")
        val updatedProductDto = mockProductDto(productId.value)
        
        `when`("removeProductOption 메서드가 호출되면") {
            every { removeProductOptionUseCase(productId, optionId) } returns updatedProductDto
            
            then("옵션이 제거된 상품 DTO를 반환해야 한다") {
                val result = productFacade.removeProductOption(productId, optionId)
                result shouldBe updatedProductDto
                verify { removeProductOptionUseCase(productId, optionId) }
            }
        }
    }
    
    given("상품 정보 수정 요청이 들어왔을 때") {
        val productId = ProductId("test-product-id")
        val command = UpdateProductCommand(
            name = "수정된 상품명",
            description = "수정된 상품 설명",
            price = BigDecimal("15000")
        )
        val updatedProductDto = mockProductDto(
            id = productId.value,
            name = "수정된 상품명",
            description = "수정된 상품 설명",
            price = BigDecimal("15000")
        )
        
        `when`("updateProduct 메서드가 호출되면") {
            every { updateProductUseCase(productId, command) } returns updatedProductDto
            
            then("수정된 상품 DTO를 반환해야 한다") {
                val result = productFacade.updateProduct(productId, command)
                result shouldBe updatedProductDto
                verify { updateProductUseCase(productId, command) }
            }
        }
    }
    
    given("인기 상품 조회 요청이 들어왔을 때") {
        val query = GetTopProductsQuery(limit = 5, period = TopProductPeriod.WEEKLY)
        val topProducts = listOf(
            mockProductDto("product-1"),
            mockProductDto("product-2"),
            mockProductDto("product-3"),
            mockProductDto("product-4"),
            mockProductDto("product-5")
        )
        
        `when`("getTopProducts 메서드가 호출되면") {
            every { getTopProductsUseCase(query) } returns topProducts
            
            then("인기 상품 목록을 반환해야 한다") {
                val result = productFacade.getTopProducts(query)
                result shouldBe topProducts
                result.size shouldBe 5
                verify { getTopProductsUseCase(query) }
            }
        }
    }
})

// 테스트용 ProductDto를 생성하는 헬퍼 함수
private fun mockProductDto(
    id: String,
    name: String = "테스트 상품",
    description: String = "테스트 상품 설명",
    price: BigDecimal = BigDecimal("10000"),
    status: ProductStatus = ProductStatus.Active,
    stock: Int = 100,
    stockStatus: String = "InStock"
): ProductDto {
    return ProductDto(
        id = id,
        name = name,
        description = description,
        price = price,
        status = status,
        stock = stock,
        stockStatus = stockStatus,
        options = emptyList(),
        createdAt = LocalDateTime.now(),
        updatedAt = LocalDateTime.now()
    )
}