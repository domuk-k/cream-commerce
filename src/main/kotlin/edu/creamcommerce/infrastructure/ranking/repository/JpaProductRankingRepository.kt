package edu.creamcommerce.infrastructure.ranking.repository

import edu.creamcommerce.domain.ranking.PeriodType
import edu.creamcommerce.infrastructure.ranking.entity.ProductRankingEntity
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
interface JpaProductRankingRepository : JpaRepository<ProductRankingEntity, String> {
    fun findByRankingDateAndPeriodType(rankingDate: LocalDate, periodType: PeriodType): List<ProductRankingEntity>
    
    fun findByRankingDateAndPeriodTypeOrderByRankAsc(rankingDate: LocalDate, periodType: PeriodType, pageable: Pageable): List<ProductRankingEntity>
    
    fun findByProductIdAndPeriodTypeOrderByRankingDateDesc(productId: String, periodType: PeriodType, pageable: Pageable): List<ProductRankingEntity>
    
    fun findByProductIdAndRankingDateAndPeriodType(productId: String, rankingDate: LocalDate, periodType: PeriodType): ProductRankingEntity?
} 