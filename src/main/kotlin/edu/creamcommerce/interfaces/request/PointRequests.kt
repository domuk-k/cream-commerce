package edu.creamcommerce.interfaces.request

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