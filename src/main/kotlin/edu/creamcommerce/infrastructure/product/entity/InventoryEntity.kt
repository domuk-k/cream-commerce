package edu.creamcommerce.infrastructure.product.entity

import edu.creamcommerce.domain.product.InventoryStatus
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "inventories")
class InventoryEntity(
    @Id
    @Column(name = "option_id")
    var optionId: String,
    
    @Column(name = "quantity", nullable = false)
    var quantity: Int,
    
    @Column(name = "low_stock_threshold", nullable = false)
    var lowStockThreshold: Int,
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    var status: InventoryStatus,
    
    @Column(name = "last_updated", nullable = false)
    var lastUpdated: LocalDateTime = LocalDateTime.now(),
)