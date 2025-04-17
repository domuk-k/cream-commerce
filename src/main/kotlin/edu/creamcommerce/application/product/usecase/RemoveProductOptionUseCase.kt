package edu.creamcommerce.application.product.usecase

import edu.creamcommerce.application.product.dto.ProductDto
import edu.creamcommerce.application.product.dto.toDto
import edu.creamcommerce.domain.product.OptionId
import edu.creamcommerce.domain.product.ProductId
import edu.creamcommerce.domain.product.ProductRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class RemoveProductOptionUseCase(
    private val productRepository: ProductRepository,
) {
    @Transactional
    operator fun invoke(productId: ProductId, optionId: OptionId): ProductDto {
        val product = productRepository.findById(productId)
            ?: throw NoSuchElementException("상품을 찾을 수 없습니다: ${productId.value}")
        
        // 먼저 DB에서 옵션을 삭제 (이렇게 하면 도메인 모델 변경과 DB 변경이 불일치할 가능성이 없음)
        productRepository.deleteOptionById(optionId)
        
        // 그 다음 도메인 모델에서 옵션 제거
        val updatedProduct = product.removeOption(optionId)
        
        // 변경된 상품 저장
        val savedProduct = productRepository.save(updatedProduct)
        
        return savedProduct.toDto()
    }
} 