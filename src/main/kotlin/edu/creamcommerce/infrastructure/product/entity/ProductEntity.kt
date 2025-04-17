package edu.creamcommerce.infrastructure.product.entity

import edu.creamcommerce.domain.product.ProductStatus
import edu.creamcommerce.domain.product.StockStatus
import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "products")
class ProductEntity(
    @Id
    @Column(name = "id")
    var id: String = UUID.randomUUID().toString(),
    
    @Column(name = "name", nullable = false)
    var name: String,
    
    @Column(name = "description", columnDefinition = "TEXT")
    var description: String,
    
    @Column(name = "price", nullable = false, precision = 19, scale = 2)
    var price: BigDecimal,
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    var status: ProductStatus,
    
    @Enumerated(EnumType.STRING)
    @Column(name = "stock_status", nullable = false)
    var stockStatus: StockStatus,
    
    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: LocalDateTime = LocalDateTime.now(),
    
    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now(),
)