package edu.creamcommerce.domain.product

import org.springframework.stereotype.Repository

@Repository
interface ProductRepository {
    fun findById(id: ProductId): Product?
    fun findAll(): List<Product>
    fun save(product: Product): Product
} 