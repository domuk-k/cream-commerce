package edu.creamcommerce.application.point.dto.command

import edu.creamcommerce.domain.coupon.UserId
import java.math.BigDecimal

data class ChargePointCommand(
    val userId: UserId,
    val amount: BigDecimal
)

data class UsePointCommand(
    val userId: UserId,
    val amount: BigDecimal
) 