package edu.creamcommerce.infrastructure.ranking.entity

import edu.creamcommerce.domain.ranking.PeriodType
import edu.creamcommerce.domain.ranking.RankingStatus
import jakarta.persistence.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "product_rankings")
class ProductRankingEntity(
    @Id
    @Column(name = "id")
    var id: String = UUID.randomUUID().toString(),
    
    @Column(name = "product_id", nullable = false)
    var productId: String,
    
    @Enumerated(EnumType.STRING)
    @Column(name = "period_type", nullable = false)
    var periodType: PeriodType,
    
    @Column(name = "ranking_date", nullable = false)
    var rankingDate: LocalDate,
    
    @Column(name = "`rank`", nullable = false)
    var rank: Int,
    
    @Column(name = "score", nullable = false)
    var score: Double,
    
    @Column(name = "sales_count", nullable = false)
    var salesCount: Int,
    
    @Column(name = "previous_rank")
    var previousRank: Int? = null,
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    var status: RankingStatus,
    
    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: LocalDateTime = LocalDateTime.now(),
    
    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now()
) 