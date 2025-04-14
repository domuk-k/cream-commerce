package edu.creamcommerce.application.product.usecase

import edu.creamcommerce.application.product.dto.command.UpdateProductStatusCommand
import edu.creamcommerce.domain.common.Money
import edu.creamcommerce.domain.product.Product
import edu.creamcommerce.domain.product.ProductId
import edu.creamcommerce.domain.product.ProductRepository
import edu.creamcommerce.domain.product.ProductStatus
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.util.*

class UpdateProductStatusUseCaseTest : BehaviorSpec({
    val productRepository = mockk<ProductRepository>()
    val useCase = UpdateProductStatusUseCase(productRepository)
    
    given("상품 상태 변경 요청이 있을 때") {
        val productId = ProductId(UUID.randomUUID().toString())
        val product = createProduct(productId, ProductStatus.Active)
        val command = UpdateProductStatusCommand(ProductStatus.Suspended)
        
        `when`("상품이 존재하고 상태 변경이 가능한 경우") {
            every { productRepository.findById(productId) } returns product
            
            val suspendedProduct = product.suspend()
            every { productRepository.save(any()) } returns suspendedProduct
            
            then("상품 상태가 변경되어야 한다") {
                val result = useCase(productId, command)
                
                result.id shouldBe productId.value
                result.status shouldBe ProductStatus.Suspended
                
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
        
        `when`("판매 중단된 상품을 활성화하려고 하는 경우") {
            val discontinuedProduct = createProduct(productId, ProductStatus.Discontinued)
            every { productRepository.findById(productId) } returns discontinuedProduct
            
            val activateCommand = UpdateProductStatusCommand(ProductStatus.Active)
            
            then("IllegalStateException이 발생해야 한다") {
                val exception = shouldThrow<IllegalStateException> {
                    useCase(productId, activateCommand)
                }
                
                exception.message shouldBe "판매 중단된 상품은 활성화할 수 없습니다."
                verify { productRepository.findById(productId) }
            }
        }
        
        `when`("Draft 상태로 변경하려고 하는 경우") {
            every { productRepository.findById(productId) } returns product
            
            val draftCommand = UpdateProductStatusCommand(ProductStatus.Draft)
            
            then("IllegalStateException이 발생해야 한다") {
                val exception = shouldThrow<IllegalStateException> {
                    useCase(productId, draftCommand)
                }
                
                exception.message shouldBe "상품을 초안 상태로 변경할 수 없습니다."
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