package edu.creamcommerce.domain.ranking

import edu.creamcommerce.domain.product.ProductId
import java.time.LocalDate

interface ProductRankingRepository {
    fun findById(id: RankingId): ProductRanking?
    
    fun findByDateAndPeriod(date: LocalDate, periodType: PeriodType): List<ProductRanking>
    
    /**
     * 특정 날짜의 특정 기간 타입에 대한 랭킹을 상위 N개 조회합니다.
     */
    fun findTopRankingsByDateAndPeriod(date: LocalDate, periodType: PeriodType, limit: Int): List<ProductRanking>
    
    /**
     * 특정 상품의 최근 랭킹 정보를 조회합니다.
     */
    fun findLatestRankingForProduct(productId: ProductId, periodType: PeriodType): ProductRanking?
    
    /**
     * 특정 상품의 특정 날짜에 대한 랭킹 정보를 조회합니다.
     */
    fun findRankingForProductAndDate(productId: ProductId, date: LocalDate, periodType: PeriodType): ProductRanking?
    
    /**
     * 랭킹을 저장합니다.
     */
    fun save(productRanking: ProductRanking): ProductRanking
    
    /**
     * 여러 랭킹을 일괄 저장합니다.
     */
    fun saveAll(productRankings: List<ProductRanking>): List<ProductRanking>
}