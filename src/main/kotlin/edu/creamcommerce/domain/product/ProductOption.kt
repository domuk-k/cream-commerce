package edu.creamcommerce.domain.product

import edu.creamcommerce.domain.common.Money
import java.time.LocalDateTime
import java.util.*

enum class OptionStatus {
    ACTIVE,     // 활성 상태 (판매 가능)
    RESERVED,   // 주문 진행 중
    SOLD,       // 판매 완료
    INACTIVE    // 비활성 상태 (판매 불가)
}

class ProductOption private constructor(
    val id: OptionId,
    val productId: ProductId,
    val name: String,
    val sku: String,
    val additionalPrice: Money = Money(0),
    status: OptionStatus = OptionStatus.ACTIVE,
    val createdAt: LocalDateTime,
    var updatedAt: LocalDateTime,
    inventory: Inventory
) {
    companion object {
        fun create(
            id: OptionId = OptionId.create(),
            name: String,
            sku: String,
            productId: ProductId,
            additionalPrice: Money,
            stock: Int,
            lowStockThreshold: Int = 5
        ): ProductOption {
            val now = LocalDateTime.now()
            
            val option = ProductOption(
                id = id,
                productId = productId,
                name = name,
                sku = sku,
                additionalPrice = additionalPrice,
                status = OptionStatus.ACTIVE,
                createdAt = now,
                updatedAt = now,
                inventory = Inventory.create(
                    optionId = id,
                    quantity = stock,
                    lowStockThreshold = lowStockThreshold
                )
            )
            
            return option
        }
    }
    
    var status: OptionStatus = status
        private set
    
    var inventory: Inventory = inventory
        private set
    
    fun hasEnoughStock(quantity: Int): Boolean {
        if (status != OptionStatus.ACTIVE) {
            return false
        }
        
        return inventory.hasEnoughStock(quantity)
    }
    
    fun decreaseStock(quantity: Int): ProductOption {
        if (status != OptionStatus.ACTIVE) {
            throw IllegalStateException("활성 상태가 아닌 옵션의 재고를 차감할 수 없습니다. 현재 상태: $status")
        }
        
        inventory.decreaseQuantity(quantity)
        this.updatedAt = LocalDateTime.now()
        return this
    }
    
    fun increaseStock(quantity: Int): ProductOption {
        if (quantity <= 0) {
            throw IllegalArgumentException("증가시킬 재고 수량은 0보다 커야 합니다.")
        }
        
        inventory.increaseQuantity(quantity)
        this.updatedAt = LocalDateTime.now()
        return this
    }
    
    fun getStockStatus(): StockStatus {
        val inventoryStatus = inventory.status
        
        return when (inventoryStatus) {
            InventoryStatus.NORMAL -> StockStatus.InStock
            InventoryStatus.LOW -> StockStatus.LowStock
            InventoryStatus.ZERO -> StockStatus.OutOfStock
        }
    }
    
    fun getStockQuantity(): Int {
        return inventory.quantity
    }
    
    fun reserve(): ProductOption {
        if (status != OptionStatus.ACTIVE) {
            throw IllegalStateException("활성 상태가 아닌 옵션을 예약할 수 없습니다. 현재 상태: $status")
        }
        
        this.status = OptionStatus.RESERVED
        this.updatedAt = LocalDateTime.now()
        return this
    }
    
    fun cancelReservation(): ProductOption {
        if (status != OptionStatus.RESERVED) {
            throw IllegalStateException("예약 상태가 아닌 옵션의 예약을 취소할 수 없습니다. 현재 상태: $status")
        }
        
        this.status = OptionStatus.ACTIVE
        this.updatedAt = LocalDateTime.now()
        return this
    }
    
    fun markAsSold(): ProductOption {
        if (status != OptionStatus.RESERVED) {
            throw IllegalStateException("예약 상태가 아닌 옵션을 판매 완료로 변경할 수 없습니다. 현재 상태: $status")
        }
        
        this.status = OptionStatus.SOLD
        this.updatedAt = LocalDateTime.now()
        return this
    }
    
    fun deactivate(): ProductOption {
        if (status == OptionStatus.INACTIVE) {
            return this
        }
        
        this.status = OptionStatus.INACTIVE
        this.updatedAt = LocalDateTime.now()
        return this
    }
    
    fun activate(): ProductOption {
        if (status == OptionStatus.ACTIVE) {
            return this
        }
        
        if (status != OptionStatus.INACTIVE) {
            throw IllegalStateException("비활성 상태가 아닌 옵션을 활성화할 수 없습니다. 현재 상태: $status")
        }
        
        this.status = OptionStatus.ACTIVE
        this.updatedAt = LocalDateTime.now()
        return this
    }
}

@JvmInline
value class OptionId(val value: String) {
    companion object {
        fun create(): OptionId = OptionId(UUID.randomUUID().toString())
    }
}