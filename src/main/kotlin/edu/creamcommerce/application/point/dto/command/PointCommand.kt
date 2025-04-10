package edu.creamcommerce.application.point.dto.command

import java.math.BigDecimal

data class ChargePointCommand(
    val userId: String,
    val amount: BigDecimal
)

data class UsePointCommand(
    val userId: String,
    val amount: BigDecimal
) 