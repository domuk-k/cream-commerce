package edu.creamcommerce.domain.product

import java.time.LocalDateTime

class Inventory private constructor(
    val optionId: OptionId,
    quantity: Int,
    lowStockThreshold: Int = 5,
    var lastUpdated: LocalDateTime
) {
    companion object {
        fun create(optionId: OptionId, quantity: Int, lowStockThreshold: Int = 5): Inventory {
            return Inventory(
                optionId = optionId,
                quantity = quantity,
                lowStockThreshold = lowStockThreshold,
                lastUpdated = LocalDateTime.now()
            )
        }
    }
    
    var quantity: Int = quantity
        private set
    
    var lowStockThreshold: Int = lowStockThreshold
        private set
    
    var status: InventoryStatus = calculateStatus(quantity, lowStockThreshold)
        private set
    
    fun hasEnoughStock(requestedQuantity: Int): Boolean {
        return quantity >= requestedQuantity
    }
    
    fun decreaseQuantity(amount: Int): Inventory {
        if (amount <= 0) {
            throw IllegalArgumentException("차감할 재고 수량은 0보다 커야 합니다.")
        }
        
        if (!hasEnoughStock(amount)) {
            throw IllegalStateException("재고가 부족합니다. 현재 재고: $quantity, 요청 수량: $amount")
        }
        
        quantity -= amount
        lastUpdated = LocalDateTime.now()
        updateStatus()
        return this
    }
    
    fun increaseQuantity(amount: Int): Inventory {
        if (amount <= 0) {
            throw IllegalArgumentException("증가시킬 재고 수량은 0보다 커야 합니다.")
        }
        
        quantity += amount
        lastUpdated = LocalDateTime.now()
        updateStatus()
        return this
    }
    
    private fun updateStatus() {
        status = calculateStatus(quantity, lowStockThreshold)
    }
    
    private fun calculateStatus(currentQuantity: Int, threshold: Int): InventoryStatus {
        return when {
            currentQuantity <= 0 -> InventoryStatus.ZERO
            currentQuantity <= threshold -> InventoryStatus.LOW
            else -> InventoryStatus.NORMAL
        }
    }
}

enum class InventoryStatus {
    NORMAL,  // 정상 재고
    LOW,     // 부족 재고
    ZERO     // 재고 없음
} 