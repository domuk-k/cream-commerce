-- 포인트 테이블
CREATE TABLE IF NOT EXISTS points (
    amount decimal(19,2) not null,
    created_at datetime(6) not null,
    updated_at datetime(6) not null,
    id varchar(255) not null,
    user_id varchar(255) not null,
    primary key (id)
) engine=InnoDB;

-- 포인트 이력 테이블
CREATE TABLE IF NOT EXISTS point_histories (
    amount decimal(19,2) not null,
    balance decimal(19,2) not null,
    created_at datetime(6) not null,
    id varchar(255) not null,
    point_id varchar(255) not null,
    type enum ('CHARGE','USE') not null,
    primary key (id)
) engine=InnoDB;

-- 쿠폰 템플릿 테이블
CREATE TABLE IF NOT EXISTS coupon_templates (
    discount_value integer not null,
    issued_count integer not null,
    max_issuance_count integer not null,
    max_issuance_per_user integer not null,
    maximum_discount_amount decimal(38,2),
    minimum_order_amount decimal(38,2) not null,
    valid_duration_hours integer not null,
    created_at datetime(6) not null,
    end_at datetime(6) not null,
    start_at datetime(6) not null,
    description TEXT,
    id varchar(255) not null,
    name varchar(255) not null,
    discount_type enum ('FIXED_AMOUNT','PERCENTAGE') not null,
    status enum ('ACTIVE','DEPLETED','SUSPENDED','TERMINATED') not null,
    primary key (id)
) engine=InnoDB;

-- 사용자 쿠폰 테이블
CREATE TABLE IF NOT EXISTS user_coupons (
    discount_value integer not null,
    maximum_discount_amount decimal(19,2),
    minimum_order_amount decimal(19,2) not null,
    issued_at datetime(6) not null,
    used_at datetime(6),
    valid_until datetime(6) not null,
    description TEXT not null,
    id varchar(255) not null,
    name varchar(255) not null,
    order_id varchar(255),
    template_id varchar(255) not null,
    user_id varchar(255) not null,
    discount_type enum ('FIXED_AMOUNT','PERCENTAGE') not null,
    status enum ('EXPIRED','REVOKED','USED','VALID') not null,
    primary key (id)
) engine=InnoDB;

-- 상품 테이블
CREATE TABLE IF NOT EXISTS products (
    price decimal(19,2) not null,
    created_at datetime(6) not null,
    updated_at datetime(6) not null,
    description TEXT,
    id varchar(255) not null,
    name varchar(255) not null,
    status enum ('Active','Discontinued','Draft','Suspended') not null,
    stock_status enum ('InStock','LowStock','OutOfStock') not null,
    primary key (id)
) engine=InnoDB;

-- 상품 옵션 테이블
CREATE TABLE IF NOT EXISTS product_options (
    additional_price decimal(19,2) not null,
    created_at datetime(6) not null,
    updated_at datetime(6) not null,
    id varchar(255) not null,
    name varchar(255) not null,
    product_id varchar(255) not null,
    sku varchar(255) not null,
    status enum ('ACTIVE','INACTIVE','RESERVED','SOLD') not null,
    primary key (id)
) engine=InnoDB;

-- 상품 옵션 sku 유니크 인덱스
alter table product_options 
    add constraint UK2x57hqm68kdgocn8d1g8fltn3 unique (sku);

-- 재고 테이블
CREATE TABLE IF NOT EXISTS inventories (
    low_stock_threshold integer not null,
    quantity integer not null,
    last_updated datetime(6) not null,
    option_id varchar(255) not null,
    status enum ('LOW','NORMAL','ZERO') not null,
    primary key (option_id)
) engine=InnoDB;

-- 주문 테이블
CREATE TABLE IF NOT EXISTS orders (
    total_amount decimal(19,2) not null,
    created_at datetime(6) not null,
    updated_at datetime(6) not null,
    id varchar(255) not null,
    shipping_address varchar(255),
    user_id varchar(255) not null,
    status enum ('CANCELED','COMPLETED','DELIVERED','PAID','PENDING','PROCESSING','REFUNDED','REFUNDING','SHIPPED') not null,
    primary key (id)
) engine=InnoDB;

-- 주문 상품 테이블
CREATE TABLE IF NOT EXISTS order_items (
    price decimal(19,2) not null,
    quantity integer not null,
    created_at datetime(6) not null,
    id varchar(255) not null,
    option_id varchar(255) not null,
    option_name varchar(255) not null,
    option_sku varchar(255) not null,
    order_id varchar(255) not null,
    product_id varchar(255) not null,
    product_name varchar(255) not null,
    primary key (id)
) engine=InnoDB;

-- 결제 테이블
CREATE TABLE IF NOT EXISTS payments (
    amount decimal(19,2) not null,
    created_at datetime(6) not null,
    updated_at datetime(6) not null,
    id varchar(255) not null,
    order_id varchar(255) not null,
    status enum ('CANCELED','COMPLETED','FAILED','PROCESSING','READY','REFUNDED','REFUNDING') not null,
    primary key (id)
) engine=InnoDB;

-- 결제 실패 테이블
CREATE TABLE IF NOT EXISTS payment_failures (
    created_at datetime(6) not null,
    id varchar(255) not null,
    payment_id varchar(255) not null,
    reason TEXT not null,
    primary key (id)
) engine=InnoDB;

-- 상품 랭킹 테이블
CREATE TABLE IF NOT EXISTS product_rankings (
    previous_rank integer,
    `rank` integer not null,
    ranking_date date not null,
    sales_count integer not null,
    score float(53) not null,
    created_at datetime(6) not null,
    updated_at datetime(6) not null,
    id varchar(255) not null,
    product_id varchar(255) not null,
    period_type enum ('ALL_TIME','DAILY','MONTHLY','WEEKLY') not null,
    status enum ('ACTIVE','ARCHIVED','CREATED','EXPIRED','UPDATED') not null,
    primary key (id)
) engine=InnoDB;