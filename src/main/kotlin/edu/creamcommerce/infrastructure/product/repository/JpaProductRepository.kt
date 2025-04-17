package edu.creamcommerce.infrastructure.product.repository

import edu.creamcommerce.infrastructure.product.entity.ProductEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface JpaProductRepository : JpaRepository<ProductEntity, String> 