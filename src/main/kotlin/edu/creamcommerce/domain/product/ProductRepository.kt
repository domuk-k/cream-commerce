package edu.creamcommerce.domain.product

import org.springframework.stereotype.Repository

@Repository
interface ProductRepository {
    // 상품 관련
    fun findById(id: ProductId): Product?
    fun findAll(): List<Product>
    fun save(product: Product): Product
    
    // 상품 옵션 관련
    fun findOptionById(id: OptionId): ProductOption?
    fun findOptionsByProductId(productId: ProductId): List<ProductOption>
    fun saveOption(productOption: ProductOption): ProductOption
    fun deleteOptionById(id: OptionId)
    
    // 재고 관련
    fun findInventoryByOptionId(optionId: OptionId): Inventory?
    fun saveInventory(inventory: Inventory): Inventory
    
}