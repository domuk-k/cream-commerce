package edu.creamcommerce.domain.product

import java.math.BigDecimal
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
    stock: Int = 0
) {
    companion object {
        fun create(
            id: ProductId? = null,
            name: String,
            price: Money,
            options: List<ProductOption> = emptyList(),
            stock: Int = 0
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
                stock = stock
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
    
    val options: List<ProductOption> get() = _options.toList()
    
    fun isActive(): Boolean = status == ProductStatus.Active
    
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
        return this
    }
}

@JvmInline
value class ProductId(val value: String) {
    companion object {
        fun create(): ProductId = ProductId(UUID.randomUUID().toString())
    }
}

@JvmInline
value class Money(val amount: BigDecimal) {
    constructor(amount: Int) : this(BigDecimal.valueOf(amount.toLong()))
    
    operator fun plus(other: Money): Money = Money(amount + other.amount)
    operator fun minus(other: Money): Money = Money(amount - other.amount)
    operator fun times(quantity: Int): Money = Money(amount * BigDecimal(quantity))
}
