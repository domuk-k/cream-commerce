package edu.creamcommerce.application.point.dto.query

import edu.creamcommerce.domain.point.PointId

data class GetPointByIdQuery(val pointId: PointId)

data class GetPointByUserIdQuery(val userId: String)

data class GetPointHistoriesByPointIdQuery(val pointId: PointId) 