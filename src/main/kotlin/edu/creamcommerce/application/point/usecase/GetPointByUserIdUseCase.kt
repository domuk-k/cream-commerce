package edu.creamcommerce.application.point.usecase

import edu.creamcommerce.application.point.dto.PointDto
import edu.creamcommerce.application.point.dto.toDto
import edu.creamcommerce.domain.coupon.UserId
import edu.creamcommerce.domain.point.PointRepository
import org.springframework.stereotype.Component

@Component
class GetPointByUserIdUseCase(
    private val pointRepository: PointRepository
) {
    operator fun invoke(userId: UserId): PointDto? = pointRepository.findByUserId(userId)?.toDto()
} 