package edu.creamcommerce.domain.ranking

import edu.creamcommerce.domain.product.ProductId
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

/**
 * 상품 랭킹의 기간 타입을 정의합니다.
 */
enum class PeriodType {
    DAILY,    // 일간
    WEEKLY,   // 주간
    MONTHLY,  // 월간
    ALL_TIME  // 전체 기간
}

/**
 * 상품 랭킹의 상태를 정의합니다.
 */
enum class RankingStatus {
    CREATED,   // 생성됨
    ACTIVE,    // 활성화
    UPDATED,   // 업데이트됨
    ARCHIVED,  // 보관됨
    EXPIRED    // 만료됨
}

/**
 * 상품의 랭킹 정보를 관리하는 엔티티입니다.
 */
class ProductRanking private constructor(
    val id: RankingId,
    val productId: ProductId,
    val periodType: PeriodType,
    val rankingDate: LocalDate,
    rank: Int,
    score: Double,
    salesCount: Int,
    previousRank: Int? = null,
    status: RankingStatus = RankingStatus.CREATED,
    val createdAt: LocalDateTime,
    var updatedAt: LocalDateTime
) {
    companion object {
        fun create(
            productId: ProductId,
            periodType: PeriodType,
            rankingDate: LocalDate,
            rank: Int,
            score: Double,
            salesCount: Int,
            previousRank: Int? = null
        ): ProductRanking {
            val now = LocalDateTime.now()
            return ProductRanking(
                id = RankingId.create(),
                productId = productId,
                periodType = periodType,
                rankingDate = rankingDate,
                rank = rank,
                score = score,
                salesCount = salesCount,
                previousRank = previousRank,
                status = RankingStatus.CREATED,
                createdAt = now,
                updatedAt = now
            )
        }
    }

    var rank: Int = rank
        private set

    var score: Double = score
        private set

    var salesCount: Int = salesCount
        private set

    var previousRank: Int? = previousRank
        private set

    var status: RankingStatus = status
        private set

    /**
     * 랭킹 정보를 활성화 상태로 변경합니다.
     */
    fun activate(): ProductRanking {
        if (status == RankingStatus.ARCHIVED || status == RankingStatus.EXPIRED) {
            throw IllegalStateException("보관되거나 만료된 랭킹은 활성화할 수 없습니다.")
        }

        this.status = RankingStatus.ACTIVE
        this.updatedAt = LocalDateTime.now()
        return this
    }

    /**
     * 랭킹 정보를 업데이트합니다.
     */
    fun update(newRank: Int, newScore: Double, newSalesCount: Int): ProductRanking {
        if (status == RankingStatus.ARCHIVED || status == RankingStatus.EXPIRED) {
            throw IllegalStateException("보관되거나 만료된 랭킹은 업데이트할 수 없습니다.")
        }

        this.previousRank = this.rank
        this.rank = newRank
        this.score = newScore
        this.salesCount = newSalesCount
        this.status = RankingStatus.UPDATED
        this.updatedAt = LocalDateTime.now()
        return this
    }

    /**
     * 랭킹 정보를 보관 상태로 변경합니다.
     */
    fun archive(): ProductRanking {
        if (status == RankingStatus.ARCHIVED) {
            return this
        }

        this.status = RankingStatus.ARCHIVED
        this.updatedAt = LocalDateTime.now()
        return this
    }

    /**
     * 랭킹 정보를 만료 상태로 변경합니다.
     */
    fun expire(): ProductRanking {
        if (status == RankingStatus.EXPIRED) {
            return this
        }

        this.status = RankingStatus.EXPIRED
        this.updatedAt = LocalDateTime.now()
        return this
    }

    /**
     * 이 랭킹이 특정 날짜의 것인지 확인합니다.
     */
    fun isForDate(date: LocalDate): Boolean {
        return this.rankingDate == date
    }

    /**
     * 이 랭킹이 특정 날짜와 기간 타입에 대한 것인지 확인합니다.
     */
    fun isFor(date: LocalDate, period: PeriodType): Boolean {
        return this.rankingDate == date && this.periodType == period
    }
}

@JvmInline
value class RankingId(val value: String) {
    companion object {
        fun create(): RankingId = RankingId(UUID.randomUUID().toString())
    }
} 