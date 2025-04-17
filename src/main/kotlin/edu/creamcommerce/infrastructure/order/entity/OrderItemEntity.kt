package edu.creamcommerce.infrastructure.order.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "order_items")
class OrderItemEntity(
    @Id
    @Column(name = "id")
    var id: String = UUID.randomUUID().toString(),
    
    @Column(name = "order_id", nullable = false)
    var orderId: String,
    
    @Column(name = "product_id", nullable = false)
    var productId: String,
    
    @Column(name = "product_name", nullable = false)
    var productName: String,
    
    @Column(name = "option_id", nullable = false)
    var optionId: String,
    
    @Column(name = "option_name", nullable = false)
    var optionName: String,
    
    @Column(name = "option_sku", nullable = false)
    var optionSku: String,
    
    @Column(name = "quantity", nullable = false)
    var quantity: Int,
    
    @Column(name = "price", nullable = false, precision = 19, scale = 2)
    var price: BigDecimal,
    
    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: LocalDateTime = LocalDateTime.now()
) 