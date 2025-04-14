package edu.creamcommerce.application.product.usecase

import edu.creamcommerce.application.product.dto.command.CreateProductCommand
import edu.creamcommerce.application.product.dto.command.ProductOptionCommand
import edu.creamcommerce.application.product.dto.command.toDomain
import edu.creamcommerce.domain.common.Money
import edu.creamcommerce.domain.product.*
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import java.math.BigDecimal

class CreateProductUseCaseTest : BehaviorSpec({
    val productRepository = mockk<ProductRepository>()
    val createProductUseCase = CreateProductUseCase(productRepository)
    
    given("상품 생성 명령이 주어졌을 때") {
        `when`("기본 정보만 제공하는 경우") {
            val command = CreateProductCommand(
                name = "새 상품",
                price = BigDecimal.valueOf(10000)
            )
            
            val productSlot = slot<Product>()
            
            // Product.create()로 생성한 객체를 저장하고 반환하도록 모킹
            val savedProduct = Product.create(
                name = command.name,
                price = command.toMoney()
            )
            
            every { productRepository.save(capture(productSlot)) } returns savedProduct
            
            then("상품이 생성되고 저장된 상품 정보를 반환한다") {
                val result = createProductUseCase(command)
                
                verify { productRepository.save(any()) }
                
                // 저장 요청된 Product 검증
                val capturedProduct = productSlot.captured
                capturedProduct.name shouldBe command.name
                capturedProduct.price shouldBe Money(command.price)
                capturedProduct.options shouldHaveSize 0
                
                // 반환된 DTO 검증
                result.name shouldBe command.name
                result.price shouldBe command.price
                result.options shouldHaveSize 0
            }
        }
        
        `when`("옵션을 포함하는 경우") {
            val command = CreateProductCommand(
                name = "옵션 있는 상품",
                price = BigDecimal.valueOf(20000),
                options = listOf(
                    ProductOptionCommand(
                        name = "옵션1",
                        additionalPrice = BigDecimal.valueOf(1000),
                        stock = 10
                    ),
                    ProductOptionCommand(
                        name = "옵션2",
                        additionalPrice = BigDecimal.valueOf(2000),
                        stock = 5
                    )
                )
            )
            
            val createdProductId = ProductId("new-product-with-options-id")
            val productSlot = slot<Product>()
            
            val savedProduct = Product.create(
                id = createdProductId,
                name = command.name,
                price = command.toMoney(),
                options = command.options.map { it.toDomain() }
            )
            
            every { productRepository.save(capture(productSlot)) } returns savedProduct
            
            then("옵션이 포함된 상품이 생성되고 저장된 상품 정보를 반환한다") {
                val result = createProductUseCase(command)
                
                verify { productRepository.save(any()) }
                
                // 저장 요청된 Product 검증
                val capturedProduct = productSlot.captured
                capturedProduct.name shouldBe command.name
                capturedProduct.price shouldBe Money(command.price)
                
                // 반환된 DTO 검증 (productOptions는 mock에서 설정된 값을 가짐)
                result.name shouldBe command.name
                result.price shouldBe command.price
                result.options shouldHaveSize 2
                result.options[0].name shouldBe "옵션1"
                result.options[1].name shouldBe "옵션2"
            }
        }
    }
}) {
    companion object {
        
        fun createTestProductOption(
            id: OptionId,
            name: String,
            stock: Int,
            additionalPrice: Int
        ): ProductOption {
            return ProductOption(
                id = id,
                name = name,
                stock = stock,
                additionalPrice = Money(additionalPrice)
            )
        }
    }
} 