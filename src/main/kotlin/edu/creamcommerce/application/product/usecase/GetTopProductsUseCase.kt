package edu.creamcommerce.application.product.usecase

import edu.creamcommerce.application.product.dto.ProductDto
import edu.creamcommerce.application.product.dto.query.GetTopProductsQuery
import edu.creamcommerce.application.product.dto.toDto
import edu.creamcommerce.domain.product.ProductRepository
import org.springframework.stereotype.Service

@Service
class GetTopProductsUseCase(
    private val productRepository: ProductRepository
) {
    operator fun invoke(query: GetTopProductsQuery): List<ProductDto> {
        // 판매량에 따른 상위 상품 조회
        // 실제 구현에서는 ProductStatisticsRepository 등을 통해 판매량 데이터를 가져와야 함
        // 여기서는 임시로 모든 상품을 가져와서 리턴
        val products = productRepository.findAll()
            .sortedByDescending { it.id.value }  // 임시 정렬 기준
            .take(query.limit)
        
        return products.map { it.toDto() }
    }
} 