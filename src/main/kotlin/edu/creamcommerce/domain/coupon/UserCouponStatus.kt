package edu.creamcommerce.domain.coupon

enum class UserCouponStatus {
    VALID,    // 발급 완료
    USED,     // 사용 완료
    EXPIRED,  // 유효기간 만료
    REVOKED   // 관리자 회수
} 