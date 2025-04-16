package edu.creamcommerce.domain.common

import java.math.BigDecimal

@JvmInline
value class Money(val amount: BigDecimal) {
    companion object {
        val ZERO = Money(BigDecimal.ZERO)
    }
    
    constructor(amount: Int) : this(BigDecimal.valueOf(amount.toLong()))
    
    operator fun plus(other: Money): Money = Money(amount + other.amount)
    operator fun minus(other: Money): Money = Money(amount - other.amount)
    operator fun times(quantity: Int): Money = Money(amount * BigDecimal(quantity))
    operator fun compareTo(other: Money): Int = amount.compareTo(other.amount)
}
