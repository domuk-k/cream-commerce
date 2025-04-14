package edu.creamcommerce.domain.product

import edu.creamcommerce.domain.common.Money
import java.time.LocalDateTime
import java.util.*

class Product private constructor(
    val id: ProductId,
    name: String,
    description: String = "",
    price: Money,
    status: ProductStatus,
    private val _options: MutableList<ProductOption> = mutableListOf(),
    val createdAt: LocalDateTime,
    var updatedAt: LocalDateTime,
    stock: Int = 0,
    lowStockThreshold: Int = 10
) {
    companion object {
        fun create(
            id: ProductId? = null,
            name: String,
            price: Money,
            options: List<ProductOption> = emptyList(),
            stock: Int = 0,
            lowStockThreshold: Int = 10
        ): Product {
            val now = LocalDateTime.now()
            return Product(
                id = id ?: ProductId.create(),
                name = name,
                price = price,
                status = ProductStatus.Active,
                _options = options.toMutableList(),
                createdAt = now,
                updatedAt = now,
                stock = stock,
                lowStockThreshold = lowStockThreshold
            )
        }
    }
    
    var price: Money = price
        private set
    
    var description: String = description
        private set
    
    var name: String = name
        private set
    
    var status: ProductStatus = status
        private set
    
    var stock: Int = stock
        private set
    
    var lowStockThreshold: Int = lowStockThreshold
        private set
    
    var stockStatus: StockStatus = calculateStockStatus(stock, lowStockThreshold)
        private set
    
    val options: List<ProductOption> get() = _options.toList()
    
    fun isActive(): Boolean = status == ProductStatus.Active
    
    /**
     * 상품을 활성화 상태로 변경합니다.
     */
    fun activate(): Product {
        if (status == ProductStatus.Discontinued) {
            throw IllegalStateException("판매 중단된 상품은 활성화할 수 없습니다.")
        }
        
        this.status = ProductStatus.Active
        this.updatedAt = LocalDateTime.now()
        return this
    }
    
    /**
     * 상품을 일시 중지 상태로 변경합니다.
     */
    fun suspend(): Product {
        if (status == ProductStatus.Discontinued) {
            throw IllegalStateException("판매 중단된 상품은 일시 중지할 수 없습니다.")
        }
        
        this.status = ProductStatus.Suspended
        this.updatedAt = LocalDateTime.now()
        return this
    }
    
    /**
     * 상품을 판매 중단 상태로 변경합니다.
     */
    fun discontinue(): Product {
        this.status = ProductStatus.Discontinued
        this.updatedAt = LocalDateTime.now()
        return this
    }
    
    /**
     * 재고가 충분한지 확인합니다.
     */
    fun hasEnoughStock(quantity: Int): Boolean {
        return stock >= quantity
    }
    
    /**
     * 재고를 차감합니다.
     */
    fun decreaseStock(quantity: Int): Product {
        if (!hasEnoughStock(quantity)) {
            throw IllegalStateException("재고가 부족합니다. 현재 재고: $stock, 요청 수량: $quantity")
        }
        
        this.stock -= quantity
        this.updatedAt = LocalDateTime.now()
        updateStockStatus()
        return this
    }
    
    /**
     * 재고를 증가시킵니다.
     */
    fun increaseStock(quantity: Int): Product {
        if (quantity <= 0) {
            throw IllegalArgumentException("증가시킬 재고 수량은 0보다 커야 합니다.")
        }
        
        this.stock += quantity
        this.updatedAt = LocalDateTime.now()
        updateStockStatus()
        return this
    }
    
    /**
     * 재고 상태를 갱신합니다.
     */
    private fun updateStockStatus() {
        this.stockStatus = calculateStockStatus(this.stock, this.lowStockThreshold)
    }
    
    /**
     * 재고와 임계치를 기준으로 재고 상태를 계산합니다.
     */
    private fun calculateStockStatus(currentStock: Int, threshold: Int): StockStatus {
        return when {
            currentStock <= 0 -> StockStatus.OutOfStock
            currentStock <= threshold -> StockStatus.LowStock
            else -> StockStatus.InStock
        }
    }
    
    /**
     * 옵션을 추가합니다.
     */
    fun addOption(option: ProductOption): Product {
        if (!isModifiable()) {
            throw IllegalStateException("활성 또는 초안 상태의 상품만 옵션을 추가할 수 있습니다.")
        }
        
        _options.add(option)
        this.updatedAt = LocalDateTime.now()
        return this
    }
    
    /**
     * 옵션을 제거합니다.
     */
    fun removeOption(optionId: OptionId): Product {
        if (!isModifiable()) {
            throw IllegalStateException("활성 또는 초안 상태의 상품만 옵션을 제거할 수 있습니다.")
        }
        
        val optionToRemove = _options.find { it.id == optionId }
            ?: throw IllegalArgumentException("해당 ID의 옵션을 찾을 수 없습니다: ${optionId.value}")
        
        _options.remove(optionToRemove)
        this.updatedAt = LocalDateTime.now()
        return this
    }
    
    /**
     * 상품이 수정 가능한 상태인지 확인합니다.
     */
    private fun isModifiable(): Boolean {
        return status == ProductStatus.Active || status == ProductStatus.Draft
    }
    
    /**
     * 상품 정보를 업데이트합니다.
     */
    fun update(name: String? = null, description: String? = null, price: Money? = null): Product {
        if (status == ProductStatus.Discontinued) {
            throw IllegalStateException("판매 중단된 상품은 수정할 수 없습니다.")
        }
        
        name?.let { this.name = it }
        description?.let { this.description = it }
        price?.let { this.price = it }
        
        this.updatedAt = LocalDateTime.now()
        return this
    }
}

@JvmInline
value class ProductId(val value: String) {
    companion object {
        fun create(): ProductId = ProductId(UUID.randomUUID().toString())
    }
}

