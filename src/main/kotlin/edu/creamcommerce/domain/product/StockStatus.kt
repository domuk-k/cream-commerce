package edu.creamcommerce.domain.product

enum class StockStatus {
    InStock,     // 재고 충분
    LowStock,    // 재고 부족 (임계치 이하)
    OutOfStock   // 품절
} 