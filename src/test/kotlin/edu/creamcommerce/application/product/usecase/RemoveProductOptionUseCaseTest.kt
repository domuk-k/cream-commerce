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
import java.time.LocalDateTime
import java.util.*

class RemoveProductOptionUseCaseTest : BehaviorSpec({
    val productRepository = mockk<ProductRepository>()
    val useCase = RemoveProductOptionUseCase(productRepository)
    
    given("상품 옵션 제거 요청이 있을 때") {
        val productId = ProductId(UUID.randomUUID().toString())
        val optionId = OptionId(UUID.randomUUID().toString())
        
        // 테스트용 인벤토리 생성
        val inventory = mockk<Inventory>()
        every { inventory.quantity } returns 50
        every { inventory.status } returns InventoryStatus.NORMAL
        
        // 테스트용 옵션 생성
        val now = LocalDateTime.now()
        val option = mockk<ProductOption>()
        every { option.id } returns optionId
        every { option.name } returns "테스트 옵션"
        every { option.sku } returns "TEST-SKU-123"
        every { option.additionalPrice } returns Money(1000)
        every { option.inventory } returns inventory
        every { option.getStockStatus() } returns StockStatus.InStock
        every { option.getStockQuantity() } returns 50
        every { option.status } returns OptionStatus.ACTIVE
        every { option.createdAt } returns now
        every { option.updatedAt } returns now
        
        // 테스트용 Product 생성
        val product = mockk<Product>()
        every { product.id } returns productId
        every { product.options } returns listOf(option)
        every { product.removeOption(optionId) } returns product
        every { product.toDto() } returns mockk()
        
        `when`("상품이 존재하고 옵션도 존재하는 경우") {
            every { productRepository.findById(productId) } returns product
            every { productRepository.save(any()) } returns product
            
            then("상품에서 옵션이 제거되어야 한다") {
                val result = useCase(productId, optionId)
                
                verify { productRepository.findById(productId) }
                verify { product.removeOption(optionId) }
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
            
            // 옵션 제거 시 예외 발생하도록 설정
            every { productRepository.findById(productId) } returns product
            every { product.removeOption(nonExistentOptionId) } throws IllegalArgumentException("해당 ID의 옵션을 찾을 수 없습니다: ${nonExistentOptionId.value}")
            
            then("IllegalArgumentException이 발생해야 한다") {
                val exception = shouldThrow<IllegalArgumentException> {
                    useCase(productId, nonExistentOptionId)
                }
                
                exception.message shouldBe "해당 ID의 옵션을 찾을 수 없습니다: ${nonExistentOptionId.value}"
                verify { productRepository.findById(productId) }
            }
        }
        
        `when`("상품이 판매 중단 상태인 경우") {
            val discontinuedProduct = mockk<Product>()
            every { discontinuedProduct.removeOption(optionId) } throws IllegalStateException("활성 또는 초안 상태의 상품만 옵션을 제거할 수 있습니다.")
            
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