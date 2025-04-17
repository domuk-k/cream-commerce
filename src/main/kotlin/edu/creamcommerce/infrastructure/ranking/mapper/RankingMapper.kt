package edu.creamcommerce.infrastructure.ranking.mapper

import edu.creamcommerce.domain.product.ProductId
import edu.creamcommerce.domain.ranking.ProductRanking
import edu.creamcommerce.domain.ranking.RankingId
import edu.creamcommerce.infrastructure.ranking.entity.ProductRankingEntity
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class RankingMapper {
    // ProductRanking 매핑
    fun toEntity(domain: ProductRanking): ProductRankingEntity {
        return ProductRankingEntity(
            id = domain.id.value,
            productId = domain.productId.value,
            periodType = domain.periodType,
            rankingDate = domain.rankingDate,
            rank = domain.rank,
            score = domain.score,
            salesCount = domain.salesCount,
            previousRank = domain.previousRank,
            status = domain.status,
            createdAt = domain.createdAt,
            updatedAt = domain.updatedAt
        )
    }

    fun toDomain(entity: ProductRankingEntity): ProductRanking {
        // 기본 객체 생성
        val ranking = ProductRanking.create(
            productId = ProductId(entity.productId),
            periodType = entity.periodType,
            rankingDate = entity.rankingDate,
            rank = entity.rank,
            score = entity.score,
            salesCount = entity.salesCount,
            previousRank = entity.previousRank
        )
        
        // id 및 상태 동기화
        syncState(ranking, entity)
        
        return ranking
    }
    
    // 상태 동기화 헬퍼 메서드
    private fun syncState(domain: ProductRanking, entity: ProductRankingEntity) {
        // 이미 같은 상태인지 확인
        if (entity.status == domain.status) return
        
        // 상태에 따라 적절한 메서드 호출
        when (entity.status) {
            edu.creamcommerce.domain.ranking.RankingStatus.ACTIVE -> domain.activate()
            edu.creamcommerce.domain.ranking.RankingStatus.ARCHIVED -> domain.archive()
            edu.creamcommerce.domain.ranking.RankingStatus.EXPIRED -> domain.expire()
            edu.creamcommerce.domain.ranking.RankingStatus.CREATED -> {} // 기본 상태이므로 변경 불필요
            edu.creamcommerce.domain.ranking.RankingStatus.UPDATED -> {
                // 업데이트 상태인 경우 현재 값으로 update 호출
                // 이미 값은 생성 시점에 설정되었으므로 동일한 값을 사용
                domain.update(domain.rank, domain.score, domain.salesCount)
            }
        }
    }
} 