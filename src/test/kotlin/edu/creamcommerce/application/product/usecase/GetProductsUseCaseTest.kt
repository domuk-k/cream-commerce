package edu.creamcommerce.application.product.usecase

import edu.creamcommerce.application.product.dto.query.GetProductsQuery
import edu.creamcommerce.domain.product.Money
import edu.creamcommerce.domain.product.Product
import edu.creamcommerce.domain.product.ProductRepository
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class GetProductsUseCaseTest : BehaviorSpec({
    val productRepository = mockk<ProductRepository>()
    val getProductsUseCase = GetProductsUseCase(productRepository)
    
    given("페이징 정보가 주어졌을 때") {
        val pageSize = 2
        val query = GetProductsQuery(page = 0, size = pageSize)
        
        `when`("상품이 존재하는 경우") {
            val products = listOf(
                Product.create(name = "product-1", price = Money(10000)),
                Product.create(name = "product-2", price = Money(20000)),
                Product.create(name = "product-3", price = Money(30000))
            )
            
            every { productRepository.findAll() } returns products
            
            then("페이지 크기에 맞게 상품 목록을 반환한다") {
                val result = getProductsUseCase(query)
                
                result.products shouldHaveSize pageSize
                result.products[0].name shouldBe "product-1"
                result.products[1].name shouldBe "product-2"
                result.total shouldBe 3
                result.page shouldBe 0
                result.size shouldBe pageSize
                
                verify { productRepository.findAll() }
            }
        }
        
        `when`("두 번째 페이지를 요청하는 경우") {
            val page1Query = GetProductsQuery(page = 1, size = pageSize)
            val products = listOf(
                Product.create(name = "product-1", price = Money(10000)),
                Product.create(name = "product-2", price = Money(20000)),
                Product.create(name = "product-3", price = Money(30000))
            )
            
            every { productRepository.findAll() } returns products
            
            then("두 번째 페이지의 상품을 반환한다") {
                val result = getProductsUseCase(page1Query)
                
                result.products shouldHaveSize 1
                result.products[0].name shouldBe "product-3"
                result.total shouldBe 3
                result.page shouldBe 1
                result.size shouldBe pageSize
                
                verify { productRepository.findAll() }
            }
        }
        
        `when`("상품이 없는 경우") {
            every { productRepository.findAll() } returns emptyList()
            
            then("빈 목록을 반환한다") {
                val result = getProductsUseCase(query)
                
                result.products shouldHaveSize 0
                result.total shouldBe 0
                result.page shouldBe 0
                result.size shouldBe pageSize
                
                verify { productRepository.findAll() }
            }
        }
        
        `when`("존재하지 않는 페이지를 요청하는 경우") {
            val invalidPageQuery = GetProductsQuery(page = 10, size = pageSize)
            val products = listOf(
                Product.create(name = "product-1", price = Money(10000)),
                Product.create(name = "product-2", price = Money(20000)),
            )
            
            every { productRepository.findAll() } returns products
            
            then("빈 목록을 반환한다") {
                val result = getProductsUseCase(invalidPageQuery)
                
                result.products shouldHaveSize 0
                result.total shouldBe 2
                result.page shouldBe 10
                result.size shouldBe pageSize
                
                verify { productRepository.findAll() }
            }
        }
    }
})