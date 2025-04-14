package edu.creamcommerce.application.product.usecase

import edu.creamcommerce.application.product.dto.command.AddProductOptionCommand
import edu.creamcommerce.application.product.dto.toDto
import edu.creamcommerce.domain.common.Money
import edu.creamcommerce.domain.product.*
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.math.BigDecimal
import java.util.*

class AddProductOptionUseCaseTest : BehaviorSpec({
    val productRepository = mockk<ProductRepository>()
    val useCase = AddProductOptionUseCase(productRepository)
    
    given("상품 옵션 추가 요청이 있을 때") {
        val productId = ProductId(UUID.randomUUID().toString())
        val product = createProduct(productId, ProductStatus.Active)
        val command = AddProductOptionCommand(
            name = "새 옵션",
            additionalPrice = BigDecimal("1000"),
            stock = 50
        )
        
        `when`("상품이 존재하고 활성 상태인 경우") {
            every { productRepository.findById(productId) } returns product
            
            // 옵션이 추가된 상품
            val productWithOption = product.addOption(
                ProductOption.create(
                    name = "새 옵션",
                    additionalPrice = Money(1000),
                    stock = 50
                )
            )
            every { productRepository.save(any()) } returns productWithOption
            
            then("상품에 옵션이 추가되어야 한다") {
                val result = useCase(productId, command)
                
                result.id shouldBe productId.value
                verify { productRepository.findById(productId) }
                verify { productRepository.save(any()) }
            }
        }
        
        `when`("상품이 존재하지 않는 경우") {
            every { productRepository.findById(productId) } returns null
            
            then("NoSuchElementException이 발생해야 한다") {
                val exception = shouldThrow<NoSuchElementException> {
                    useCase(productId, command)
                }
                
                exception.message shouldBe "상품을 찾을 수 없습니다: ${productId.value}"
                verify { productRepository.findById(productId) }
            }
        }
        
        `when`("상품이 판매 중단 상태인 경우") {
            val discontinuedProduct = createProduct(productId, ProductStatus.Discontinued)
            every { productRepository.findById(productId) } returns discontinuedProduct
            
            then("IllegalStateException이 발생해야 한다") {
                val exception = shouldThrow<IllegalStateException> {
                    useCase(productId, command)
                }
                
                exception.message shouldBe "활성 또는 초안 상태의 상품만 옵션을 추가할 수 있습니다."
                verify { productRepository.findById(productId) }
            }
        }
    }
})

// 테스트용 Product 객체 생성 함수
private fun createProduct(
    id: ProductId = ProductId.create(),
    status: ProductStatus = ProductStatus.Active
): Product {
    val product = Product.create(
        id = id,
        name = "테스트 상품",
        price = Money(10000)
    )
    
    // 상태 변경
    when (status) {
        ProductStatus.Active -> {}  // 기본값이 Active이므로 변경 불필요
        ProductStatus.Suspended -> product.suspend()
        ProductStatus.Discontinued -> product.discontinue()
        ProductStatus.Draft -> {} // Draft 상태로는 변경 불가
    }
    
    return product
} 