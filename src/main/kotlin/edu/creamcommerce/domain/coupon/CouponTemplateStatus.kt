package edu.creamcommerce.domain.coupon

enum class CouponTemplateStatus {
    ACTIVE,     // 활성 상태
    DEPLETED,   // 수량 소진
    SUSPENDED,  // 일시 중지
    TERMINATED  // 이벤트 종료
} 