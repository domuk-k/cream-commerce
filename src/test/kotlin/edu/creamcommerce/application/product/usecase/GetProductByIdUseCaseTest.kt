package edu.creamcommerce.application.product.usecase

import edu.creamcommerce.application.product.dto.toDto
import edu.creamcommerce.domain.product.Money
import edu.creamcommerce.domain.product.Product
import edu.creamcommerce.domain.product.ProductId
import edu.creamcommerce.domain.product.ProductRepository
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class GetProductByIdUseCaseTest : BehaviorSpec({
    val productRepository = mockk<ProductRepository>()
    val getProductByIdUseCase = GetProductByIdUseCase(productRepository)
    
    given("상품 ID가 주어졌을 때") {
        val productId = ProductId("test-product-id")
        
        `when`("해당 ID로 상품을 찾을 수 있는 경우") {
            val product = Product.create(
                id = productId, name = "테스트 상품 $productId",
                price = Money(10000)
            )
            every { productRepository.findById(productId) } returns product
            
            then("상품 정보가 DTO로 변환되어 반환된다") {
                val result = getProductByIdUseCase(productId)
                
                result shouldBe product.toDto()
                result?.id shouldBe productId.value
                result?.name shouldBe product.name
                
                verify { productRepository.findById(productId) }
            }
        }
        
        `when`("해당 ID로 상품을 찾을 수 없는 경우") {
            every { productRepository.findById(productId) } returns null
            
            then("null을 반환한다") {
                val result = getProductByIdUseCase(productId)
                
                result shouldBe null
                
                verify { productRepository.findById(productId) }
            }
        }
    }
})