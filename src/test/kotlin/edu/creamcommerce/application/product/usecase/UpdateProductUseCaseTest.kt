package edu.creamcommerce.application.product.usecase

import edu.creamcommerce.application.product.dto.command.UpdateProductCommand
import edu.creamcommerce.application.product.dto.toDto
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
import java.math.BigDecimal
import java.util.*

class UpdateProductUseCaseTest : BehaviorSpec({
    val productRepository = mockk<ProductRepository>()
    val useCase = UpdateProductUseCase(productRepository)
    
    given("상품 정보 수정 요청이 있을 때") {
        val productId = ProductId(UUID.randomUUID().toString())
        val product = createProduct(productId, ProductStatus.Active)
        val command = UpdateProductCommand(
            name = "수정된 상품명",
            description = "수정된 상품 설명",
            price = BigDecimal("15000")
        )
        
        `when`("상품이 존재하고 활성 상태인 경우") {
            every { productRepository.findById(productId) } returns product
            
            // 수정된 상품
            val updatedProduct = product.update(
                name = "수정된 상품명",
                description = "수정된 상품 설명",
                price = Money(15000)
            )
            every { productRepository.save(any()) } returns updatedProduct
            
            then("상품 정보가 수정되어야 한다") {
                val result = useCase(productId, command)
                
                result.id shouldBe productId.value
                result.name shouldBe "수정된 상품명"
                result.description shouldBe "수정된 상품 설명"
                result.price shouldBe BigDecimal("15000")
                
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
                
                exception.message shouldBe "판매 중단된 상품은 수정할 수 없습니다."
                verify { productRepository.findById(productId) }
            }
        }
        
        `when`("일부 필드만 수정하려는 경우") {
            every { productRepository.findById(productId) } returns product
            
            val partialCommand = UpdateProductCommand(
                name = "수정된 상품명",
                description = null,
                price = null
            )
            
            val partiallyUpdatedProduct = product.update(name = "수정된 상품명")
            every { productRepository.save(any()) } returns partiallyUpdatedProduct
            
            then("지정한 필드만 수정되어야 한다") {
                val result = useCase(productId, partialCommand)
                
                result.id shouldBe productId.value
                result.name shouldBe "수정된 상품명"
                
                verify { productRepository.findById(productId) }
                verify { productRepository.save(any()) }
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