package edu.creamcommerce.infrastructure.ranking.repository

import edu.creamcommerce.domain.product.ProductId
import edu.creamcommerce.domain.ranking.*
import edu.creamcommerce.infrastructure.ranking.mapper.RankingMapper
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Repository
@Transactional(readOnly = true)
class ProductRankingRepositoryImpl(
    private val jpaProductRankingRepository: JpaProductRankingRepository,
    private val rankingMapper: RankingMapper
) : ProductRankingRepository {

    override fun findById(id: RankingId): ProductRanking? {
        return jpaProductRankingRepository.findById(id.value).orElse(null)?.let {
            rankingMapper.toDomain(it)
        }
    }

    override fun findByDateAndPeriod(date: LocalDate, periodType: PeriodType): List<ProductRanking> {
        return jpaProductRankingRepository.findByRankingDateAndPeriodType(date, periodType).map {
            rankingMapper.toDomain(it)
        }
    }

    override fun findTopRankingsByDateAndPeriod(date: LocalDate, periodType: PeriodType, limit: Int): List<ProductRanking> {
        val pageable = PageRequest.of(0, limit)
        return jpaProductRankingRepository.findByRankingDateAndPeriodTypeOrderByRankAsc(date, periodType, pageable).map {
            rankingMapper.toDomain(it)
        }
    }

    override fun findLatestRankingForProduct(productId: ProductId, periodType: PeriodType): ProductRanking? {
        val pageable = PageRequest.of(0, 1)
        return jpaProductRankingRepository.findByProductIdAndPeriodTypeOrderByRankingDateDesc(
            productId.value, periodType, pageable
        ).firstOrNull()?.let {
            rankingMapper.toDomain(it)
        }
    }

    override fun findRankingForProductAndDate(productId: ProductId, date: LocalDate, periodType: PeriodType): ProductRanking? {
        return jpaProductRankingRepository.findByProductIdAndRankingDateAndPeriodType(
            productId.value, date, periodType
        )?.let {
            rankingMapper.toDomain(it)
        }
    }

    @Transactional
    override fun save(productRanking: ProductRanking): ProductRanking {
        val entity = rankingMapper.toEntity(productRanking)
        val savedEntity = jpaProductRankingRepository.save(entity)
        return rankingMapper.toDomain(savedEntity)
    }

    @Transactional
    override fun saveAll(productRankings: List<ProductRanking>): List<ProductRanking> {
        val entities = productRankings.map { rankingMapper.toEntity(it) }
        val savedEntities = jpaProductRankingRepository.saveAll(entities)
        return savedEntities.map { rankingMapper.toDomain(it) }
    }
} 