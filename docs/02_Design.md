## 🧠 도메인별 책임

- Point 충전/사용/조회, 최대한도 및 부족검증
- Product 가격/재고 확인, 재고 감소
- Coupon 발급/귀속/사용 처리, 중복/재사용 방지
- Order 주문 생성
- Payment 자원 차감
- Queue 키 단위 락, 선착순 보장
- Statistic 인기 상품 집계
- ExternalPublisher 주문 데이터 외부 시스템 전송 (Mock)

## 설계 문서

- [포인트](./designs/포인트.md)
- [주문/결제](./designs/주문결제.md)
- [선착순 쿠폰](./designs/선착순쿠폰.md)
- [상위 상품 순위](./designs/상위상품순위.md)
