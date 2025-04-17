package edu.creamcommerce.application.product.facade

import edu.creamcommerce.application.product.dto.ProductDto
import edu.creamcommerce.application.product.dto.ProductListDto
import edu.creamcommerce.application.product.dto.ProductOptionDto
import edu.creamcommerce.application.product.dto.command.CreateProductCommand
import edu.creamcommerce.application.product.dto.command.ProductOptionCommand
import edu.creamcommerce.application.product.dto.query.GetProductsQuery
import edu.creamcommerce.application.product.usecase.*
import edu.creamcommerce.domain.common.Money
import edu.creamcommerce.domain.product.*
import io.kotest.assertions.throwables.shouldThrow
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
        val productDto = createTestProductDto(productId.value)
        
        `when`("해당 ID의 상품이 존재하는 경우") {
            every { getProductByIdUseCase(productId) } returns productDto
            
            then("상품 정보를 반환한다") {
                val result = productFacade.getProductById(productId)
                
                result shouldBe productDto
                verify { getProductByIdUseCase(productId) }
            }
        }
        
        `when`("해당 ID의 상품이 존재하지 않는 경우") {
            every { getProductByIdUseCase(productId) } returns null
            
            then("NoSuchElementException 예외가 발생한다") {
                val exception = shouldThrow<NoSuchElementException> {
                    productFacade.getProductById(productId)
                }
                
                exception.message shouldBe "상품을 찾을 수 없습니다."
                verify { getProductByIdUseCase(productId) }
            }
        }
    }
    
    given("페이징 정보가 주어졌을 때") {
        val query = GetProductsQuery(page = 0, size = 10)
        val productListDto = ProductListDto(
            products = listOf(
                createTestProductDto("product-1"),
                createTestProductDto("product-2")
            ),
            total = 2,
            page = 0,
            size = 10
        )
        
        `when`("상품 목록을 조회하면") {
            every { getProductsUseCase(query) } returns productListDto
            
            then("페이징된 상품 목록을 반환한다") {
                val result = productFacade.getProducts(query)
                
                result shouldBe productListDto
                result.products.size shouldBe 2
                result.total shouldBe 2
                result.page shouldBe 0
                result.size shouldBe 10
                
                verify { getProductsUseCase(query) }
            }
        }
    }
    
    given("상품 생성 명령이 주어졌을 때") {
        val command = CreateProductCommand(
            name = "새 상품",
            price = BigDecimal.valueOf(10000),
            options = listOf(
                ProductOptionCommand(
                    name = "옵션1",
                    additionalPrice = BigDecimal.valueOf(1000),
                    stock = 10
                )
            )
        )
        
        val createdProductDto = createTestProductDto(
            id = "new-product-id",
            name = "새 상품",
            price = BigDecimal.valueOf(10000)
        )
        
        `when`("상품 생성을 요청하면") {
            every { createProductUseCase(command) } returns createdProductDto
            
            then("생성된 상품 정보를 반환한다") {
                val result = productFacade.createProduct(command)
                
                result shouldBe createdProductDto
                result.name shouldBe "새 상품"
                result.price shouldBe BigDecimal.valueOf(10000)
                
                verify { createProductUseCase(command) }
            }
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
                description = "테스트 상품 설명",
                price = price,
                status = ProductStatus.Active,
                stockStatus = "InStock",
                options = listOf(
                    ProductOptionDto(
                        id = "option-1",
                        productId = id,
                        name = "기본 옵션",
                        additionalPrice = BigDecimal.ZERO,
                        stock = 100,
                        stockStatus = InventoryStatus.NORMAL
                    )
                ),
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now()
            )
        }
        
        fun createTestProduct(id: ProductId): Product {
            val product = Product.create(
                name = "테스트 상품 ${id.value}",
                price = Money(10000)
            )
            
            // 테스트 옵션 추가
            val option = ProductOption.create(
                name = "기본 옵션",
                sku = "TEST-SKU-001",
                additionalPrice = Money(0),
                stock = 100,
                productId = id
            )
            
            val optionsField = Product::class.java.getDeclaredField("_options")
            optionsField.isAccessible = true
            val options = optionsField.get(product) as MutableList<ProductOption>
            options.add(option)
            
            val idField = Product::class.java.getDeclaredField("id")
            idField.isAccessible = true
            idField.set(product, id)
            
            return product
        }
    }
}