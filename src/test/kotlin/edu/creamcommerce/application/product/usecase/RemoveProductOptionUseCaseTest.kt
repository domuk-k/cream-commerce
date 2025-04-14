package edu.creamcommerce.application.product.usecase

import edu.creamcommerce.application.product.dto.toDto
import edu.creamcommerce.domain.common.Money
import edu.creamcommerce.domain.product.*
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.util.*

class RemoveProductOptionUseCaseTest : BehaviorSpec({
    val productRepository = mockk<ProductRepository>()
    val useCase = RemoveProductOptionUseCase(productRepository)
    
    given("상품 옵션 제거 요청이 있을 때") {
        val productId = ProductId(UUID.randomUUID().toString())
        val optionId = OptionId(UUID.randomUUID().toString())
        
        val option = ProductOption.create(
            name = "테스트 옵션",
            additionalPrice = Money(1000),
            stock = 50
        )
        
        // 리플렉션으로 optionId 설정
        val idField = option.javaClass.getDeclaredField("id")
        idField.isAccessible = true
        idField.set(option, optionId)
        
        val product = createProductWithOption(productId, option)
        
        `when`("상품이 존재하고 옵션도 존재하는 경우") {
            every { productRepository.findById(productId) } returns product
            
            // 옵션이 제거된 상품
            val productWithoutOption = product.removeOption(optionId)
            every { productRepository.save(any()) } returns productWithoutOption
            
            then("상품에서 옵션이 제거되어야 한다") {
                val result = useCase(productId, optionId)
                
                result.id shouldBe productId.value
                verify { productRepository.findById(productId) }
                verify { productRepository.save(any()) }
            }
        }
        
        `when`("상품이 존재하지 않는 경우") {
            every { productRepository.findById(productId) } returns null
            
            then("NoSuchElementException이 발생해야 한다") {
                val exception = shouldThrow<NoSuchElementException> {
                    useCase(productId, optionId)
                }
                
                exception.message shouldBe "상품을 찾을 수 없습니다: ${productId.value}"
                verify { productRepository.findById(productId) }
            }
        }
        
        `when`("상품은 존재하지만 옵션이 존재하지 않는 경우") {
            val nonExistentOptionId = OptionId(UUID.randomUUID().toString())
            every { productRepository.findById(productId) } returns product
            
            then("IllegalArgumentException이 발생해야 한다") {
                val exception = shouldThrow<IllegalArgumentException> {
                    useCase(productId, nonExistentOptionId)
                }
                
                exception.message shouldBe "해당 ID의 옵션을 찾을 수 없습니다: ${nonExistentOptionId.value}"
                verify { productRepository.findById(productId) }
            }
        }
        
        `when`("상품이 판매 중단 상태인 경우") {
            val discontinuedProduct = createProductWithOption(productId, option)
            discontinuedProduct.discontinue()
            
            every { productRepository.findById(productId) } returns discontinuedProduct
            
            then("IllegalStateException이 발생해야 한다") {
                val exception = shouldThrow<IllegalStateException> {
                    useCase(productId, optionId)
                }
                
                exception.message shouldBe "활성 또는 초안 상태의 상품만 옵션을 제거할 수 있습니다."
                verify { productRepository.findById(productId) }
            }
        }
    }
})

// 테스트용 Product 객체 생성 함수 (옵션 포함)
private fun createProductWithOption(
    id: ProductId = ProductId.create(),
    option: ProductOption
): Product {
    val product = Product.create(
        id = id,
        name = "테스트 상품",
        price = Money(10000)
    )
    
    // 옵션 추가
    product.addOption(option)
    
    return product
} 