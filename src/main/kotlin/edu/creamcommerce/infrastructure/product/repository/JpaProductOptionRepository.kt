package edu.creamcommerce.infrastructure.product.repository

import edu.creamcommerce.infrastructure.product.entity.ProductOptionEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface JpaProductOptionRepository : JpaRepository<ProductOptionEntity, String> {
    fun findAllByProductId(productId: String): List<ProductOptionEntity>
} 