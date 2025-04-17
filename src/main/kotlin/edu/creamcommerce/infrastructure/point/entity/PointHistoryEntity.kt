package edu.creamcommerce.infrastructure.point.entity

import edu.creamcommerce.domain.point.PointHistoryType
import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "point_histories")
class PointHistoryEntity(
    @Id
    @Column(name = "id")
    var id: String = UUID.randomUUID().toString(),
    
    @Column(name = "point_id", nullable = false)
    var pointId: String,
    
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    var type: PointHistoryType,
    
    @Column(name = "amount", nullable = false, precision = 19, scale = 2)
    var amount: BigDecimal,
    
    @Column(name = "balance", nullable = false, precision = 19, scale = 2)
    var balance: BigDecimal,
    
    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: LocalDateTime = LocalDateTime.now()
) 