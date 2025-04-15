package edu.creamcommerce.application.point.dto

data class PointHistoryListDto(
    val histories: List<PointHistoryDto>,
    val total: Int
) 