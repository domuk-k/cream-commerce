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
    var updatedAt: LocalDateTime
) {
    companion object {
        fun create(
            id: ProductId? = null,
            name: String,
            price: Money,
            options: List<ProductOption> = emptyList()
        ): Product {
            val now = LocalDateTime.now()
            return Product(
                id = id ?: ProductId.create(),
                name = name,
                price = price,
                status = ProductStatus.Draft,
                _options = options.toMutableList(),
                createdAt = now,
                updatedAt = now
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
    
    val options: List<ProductOption> get() = _options.toList()
    
    // 상품의 재고 상태는 모든 옵션의 재고 상태를 기반으로 계산됩니다
    val stockStatus: StockStatus
        get() {
            if (_options.isEmpty()) {
                return StockStatus.OutOfStock
            }
            
            return when {
                _options.all { it.getStockStatus() == StockStatus.OutOfStock } -> StockStatus.OutOfStock
                _options.any { it.getStockStatus() == StockStatus.LowStock } -> StockStatus.LowStock
                else -> StockStatus.InStock
            }
        }
    
    fun isAvailable(): Boolean = status == ProductStatus.Active || status == ProductStatus.Suspended
    
    fun isActive(): Boolean = status == ProductStatus.Active
    
    fun isDraft(): Boolean = status == ProductStatus.Draft
    
    /**
     * 상품을 초안에서 활성화 상태로 변경합니다.
     * 이 과정에서 상품 데이터의 유효성 검증을 수행합니다.
     */
    fun publishFromDraft(): Product {
        if (status != ProductStatus.Draft) {
            throw IllegalStateException("초안 상태의 상품만 발행할 수 있습니다. 현재 상태: $status")
        }
        
        // 발행 전 유효성 검증
        validateForPublication()
        
        this.status = ProductStatus.Active
        this.updatedAt = LocalDateTime.now()
        return this
    }
    
    /**
     * 발행 전 상품 데이터의 유효성을 검증합니다.
     */
    private fun validateForPublication() {
        if (name.isBlank()) {
            throw IllegalStateException("상품명은 필수 항목입니다.")
        }
        
        if (_options.isEmpty()) {
            throw IllegalStateException("상품에는 최소 하나의 옵션이 필요합니다.")
        }
        
        // 추가 유효성 검사 가능
    }
    
    /**
     * 상품을 활성화 상태로 변경합니다.
     */
    fun activate(): Product {
        if (status == ProductStatus.Discontinued) {
            throw IllegalStateException("판매 중단된 상품은 활성화할 수 없습니다.")
        }
        
        if (status == ProductStatus.Draft) {
            throw IllegalStateException("초안 상태의 상품은 publishFromDraft()를 통해 활성화해야 합니다.")
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
        
        if (status == ProductStatus.Draft) {
            throw IllegalStateException("초안 상태의 상품은 일시 중지할 수 없습니다.")
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
     * 특정 옵션의 재고가 충분한지 확인합니다.
     */
    fun hasEnoughStock(optionId: OptionId, quantity: Int): Boolean {
        val option = findOption(optionId)
        return option.hasEnoughStock(quantity)
    }
    
    /**
     * 옵션의 재고를 차감합니다.
     */
    fun decreaseStock(optionId: OptionId, quantity: Int): Product {
        val option = findOption(optionId)
        option.decreaseStock(quantity)
        this.updatedAt = LocalDateTime.now()
        return this
    }
    
    /**
     * 옵션의 재고를 증가시킵니다.
     */
    fun increaseStock(optionId: OptionId, quantity: Int): Product {
        val option = findOption(optionId)
        option.increaseStock(quantity)
        this.updatedAt = LocalDateTime.now()
        return this
    }
    
    /**
     * 특정 ID의 옵션을 찾습니다.
     */
    private fun findOption(optionId: OptionId): ProductOption {
        return _options.find { it.id == optionId }
            ?: throw IllegalArgumentException("해당 ID의 옵션을 찾을 수 없습니다: ${optionId.value}")
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

