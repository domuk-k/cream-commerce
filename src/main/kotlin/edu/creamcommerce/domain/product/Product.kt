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
    val updatedAt: LocalDateTime
) {
    companion object {
        fun create(
            id: ProductId? = null,
            name: String,
            price: Money,
            options: List<ProductOption> = emptyList(),
        ): Product {
            val now = LocalDateTime.now()
            return Product(
                id = id ?: ProductId.create(),
                name = name,
                price = price,
                status = ProductStatus.Active, // TODO: correct Default status
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
    
    fun isActive(): Boolean = status == ProductStatus.Active
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
