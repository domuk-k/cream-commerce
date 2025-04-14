package edu.creamcommerce.interfaces.web.point

import java.math.BigDecimal

class PointRequest(
    val userId: String,
    val amount: BigDecimal
) {
    data class Charge(
        val userId: String,
        val amount: BigDecimal
    )
    
    data class Use(
        val userId: String,
        val amount: BigDecimal
    )
}