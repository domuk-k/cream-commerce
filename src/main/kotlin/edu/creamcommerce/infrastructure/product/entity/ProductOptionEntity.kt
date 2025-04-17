package edu.creamcommerce.infrastructure.product.entity

import edu.creamcommerce.domain.product.OptionStatus
import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "product_options")
class ProductOptionEntity(
    @Id
    @Column(name = "id")
    var id: String = UUID.randomUUID().toString(),
    
    @Column(name = "product_id", nullable = false)
    var productId: String,
    
    @Column(name = "name", nullable = false)
    var name: String,
    
    @Column(name = "additionalPrice", nullable = false, precision = 19, scale = 2)
    var additionalPrice: BigDecimal,
    
    @Column(name = "sku", nullable = false, unique = true)
    var sku: String,
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    var status: OptionStatus,
    
    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: LocalDateTime = LocalDateTime.now(),
    
    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now()
) 