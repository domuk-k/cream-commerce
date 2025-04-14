package edu.creamcommerce.application.product.usecase

import edu.creamcommerce.application.product.dto.query.GetTopProductsQuery
import edu.creamcommerce.application.product.dto.query.TopProductPeriod
import edu.creamcommerce.application.product.dto.toDto
import edu.creamcommerce.domain.common.Money
import edu.creamcommerce.domain.product.Product
import edu.creamcommerce.domain.product.ProductId
import edu.creamcommerce.domain.product.ProductRepository
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.util.*

class GetTopProductsUseCaseTest : BehaviorSpec({
    val productRepository = mockk<ProductRepository>()
    val useCase = GetTopProductsUseCase(productRepository)
    
    given("인기 상품 조회 요청이 있을 때") {
        val products = listOf(
            createProduct(ProductId(UUID.randomUUID().toString())),
            createProduct(ProductId(UUID.randomUUID().toString())),
            createProduct(ProductId(UUID.randomUUID().toString())),
            createProduct(ProductId(UUID.randomUUID().toString())),
            createProduct(ProductId(UUID.randomUUID().toString()))
        )
        
        `when`("주간 인기 상품을 요청하는 경우") {
            val query = GetTopProductsQuery(limit = 3, period = TopProductPeriod.WEEKLY)
            
            every { productRepository.findAll() } returns products
            
            then("요청한 수량만큼의 상품 목록을 반환해야 한다") {
                val result = useCase(query)
                
                result.size shouldBe 3
                verify { productRepository.findAll() }
            }
        }
        
        `when`("월간 인기 상품을 요청하는 경우") {
            val query = GetTopProductsQuery(limit = 5, period = TopProductPeriod.MONTHLY)
            
            every { productRepository.findAll() } returns products
            
            then("요청한 수량만큼의 상품 목록을 반환해야 한다") {
                val result = useCase(query)
                
                result.size shouldBe 5
                verify { productRepository.findAll() }
            }
        }
        
        `when`("전체 기간 인기 상품을 요청하는 경우") {
            val query = GetTopProductsQuery(limit = 10, period = TopProductPeriod.ALL_TIME)
            
            every { productRepository.findAll() } returns products
            
            then("전체 상품 목록을 반환해야 한다 (최대 요청 수량까지)") {
                val result = useCase(query)
                
                result.size shouldBe 5 // 전체 상품이 5개이므로 5개만 반환
                verify { productRepository.findAll() }
            }
        }
    }
})

// 테스트용 Product 객체 생성 함수
private fun createProduct(
    id: ProductId = ProductId.create()
): Product {
    return Product.create(
        id = id,
        name = "테스트 상품 ${id.value.takeLast(4)}",
        price = Money(10000)
    )
} 