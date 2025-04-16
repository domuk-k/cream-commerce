package edu.creamcommerce.interfaces.web.point

import edu.creamcommerce.domain.coupon.UserId
import java.math.BigDecimal

class PointRequest {
    data class Charge(
        val userId: UserId,
        val amount: BigDecimal
    )
    
    data class Use(
        val userId: UserId,
        val amount: BigDecimal
    )
}