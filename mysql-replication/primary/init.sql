-- MySQL 복제 사용자 생성
CREATE USER IF NOT EXISTS 'repl'@'%' IDENTIFIED WITH mysql_native_password BY 'replpass';
GRANT REPLICATION SLAVE ON *.* TO 'repl'@'%';
FLUSH PRIVILEGES;

-- 데이터베이스 선택
USE delivery;

-- 배달 시스템 데이터베이스 초기화 스크립트
-- Primary DB용 DDL

-- 고객 테이블
CREATE TABLE customers (
    customer_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    name VARCHAR(50) NOT NULL,
    phone VARCHAR(20),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 고객 주소 테이블
CREATE TABLE customer_addresses (
    address_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_id BIGINT NOT NULL,
    address TEXT NOT NULL,
    is_default BOOLEAN DEFAULT FALSE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id) ON DELETE CASCADE
);

-- 매장 카테고리 테이블
CREATE TABLE store_category (
    category_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

-- 매장 사장 테이블
CREATE TABLE store_owners (
    owner_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    name VARCHAR(50) NOT NULL,
    phone VARCHAR(20),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 매장 테이블
CREATE TABLE stores (
    store_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    owner_id BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    category_id BIGINT NOT NULL,
    address TEXT NOT NULL,
    phone VARCHAR(20),
    rating DECIMAL(3,2) DEFAULT 0.00,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (owner_id) REFERENCES store_owners(owner_id) ON DELETE CASCADE,
    FOREIGN KEY (category_id) REFERENCES store_category(category_id)
);

-- 매장 통계 테이블
CREATE TABLE store_statistics (
    store_id BIGINT PRIMARY KEY,
    total_sales DECIMAL(15,2) DEFAULT 0.00,
    total_orders INT DEFAULT 0,
    average_rating DECIMAL(3,2) DEFAULT 0.00,
    FOREIGN KEY (store_id) REFERENCES stores(store_id) ON DELETE CASCADE
);

-- 메뉴 카테고리 테이블
CREATE TABLE menu_categories (
    category_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    store_id BIGINT NOT NULL,
    name VARCHAR(50) NOT NULL,
    FOREIGN KEY (store_id) REFERENCES stores(store_id) ON DELETE CASCADE,
    UNIQUE KEY unique_category_per_store (store_id, name)
);

-- 메뉴 테이블
CREATE TABLE menus (
    menu_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    store_id BIGINT NOT NULL,
    category_id BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    price DECIMAL(10,2) NOT NULL,
    stock INT DEFAULT 0,
    is_recommended BOOLEAN DEFAULT FALSE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (store_id) REFERENCES stores(store_id) ON DELETE CASCADE,
    FOREIGN KEY (category_id) REFERENCES menu_categories(category_id) ON DELETE CASCADE
);

-- 메뉴 옵션 테이블
CREATE TABLE menu_options (
    option_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    menu_id BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL,
    additional_price DECIMAL(10,2) DEFAULT 0.00,
    FOREIGN KEY (menu_id) REFERENCES menus(menu_id) ON DELETE CASCADE
);

-- 세트 메뉴 테이블
CREATE TABLE set_menus (
    set_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    menu_id BIGINT NOT NULL,
    included_menu_id BIGINT NOT NULL,
    FOREIGN KEY (menu_id) REFERENCES menus(menu_id) ON DELETE CASCADE,
    FOREIGN KEY (included_menu_id) REFERENCES menus(menu_id) ON DELETE CASCADE,
    UNIQUE KEY unique_set_menu (menu_id, included_menu_id)
);

-- 배달원 테이블
CREATE TABLE delivery_persons (
    delivery_person_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    name VARCHAR(50) NOT NULL,
    phone VARCHAR(20),
    vehicle_type VARCHAR(30),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 주문 테이블
CREATE TABLE orders (
    order_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_id BIGINT NOT NULL,
    store_id BIGINT NOT NULL,
    delivery_person_id BIGINT,
    status VARCHAR(30) NOT NULL DEFAULT 'PENDING',
    order_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    total_price DECIMAL(10,2) NOT NULL,
    payment_method VARCHAR(30) NOT NULL,
    delivery_address TEXT NOT NULL,
    memo TEXT,
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id) ON DELETE CASCADE,
    FOREIGN KEY (store_id) REFERENCES stores(store_id) ON DELETE CASCADE,
    FOREIGN KEY (delivery_person_id) REFERENCES delivery_persons(delivery_person_id) ON DELETE SET NULL
);

-- 주문 아이템 테이블
CREATE TABLE order_items (
    order_item_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    menu_id BIGINT NOT NULL,
    option_id BIGINT,
    quantity INT NOT NULL DEFAULT 1,
    unit_price DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders(order_id) ON DELETE CASCADE,
    FOREIGN KEY (menu_id) REFERENCES menus(menu_id) ON DELETE CASCADE,
    FOREIGN KEY (option_id) REFERENCES menu_options(option_id) ON DELETE SET NULL
);

-- 주문 상태 이력 테이블
CREATE TABLE order_status_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    status VARCHAR(30) NOT NULL,
    changed_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (order_id) REFERENCES orders(order_id) ON DELETE CASCADE
);

-- 배달 테이블
CREATE TABLE deliveries (
    delivery_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    delivery_person_id BIGINT NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'ASSIGNED',
    pickup_time DATETIME,
    delivered_time DATETIME,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (order_id) REFERENCES orders(order_id) ON DELETE CASCADE,
    FOREIGN KEY (delivery_person_id) REFERENCES delivery_persons(delivery_person_id) ON DELETE CASCADE
);

-- 배달 통계 테이블
CREATE TABLE delivery_statistics (
    delivery_person_id BIGINT PRIMARY KEY,
    average_time INT DEFAULT 0,
    success_rate DECIMAL(5,2) DEFAULT 0.00,
    total_deliveries INT DEFAULT 0,
    FOREIGN KEY (delivery_person_id) REFERENCES delivery_persons(delivery_person_id) ON DELETE CASCADE
);

-- 배달 상태 로그 테이블
CREATE TABLE delivery_status_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    delivery_id BIGINT NOT NULL,
    status VARCHAR(30) NOT NULL,
    changed_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (delivery_id) REFERENCES deliveries(delivery_id) ON DELETE CASCADE
);

-- 배달원 수익 테이블
CREATE TABLE delivery_earnings (
    earning_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    delivery_person_id BIGINT NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    earned_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (delivery_person_id) REFERENCES delivery_persons(delivery_person_id) ON DELETE CASCADE
);

-- 배달원 리뷰 테이블
CREATE TABLE delivery_reviews (
    review_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    delivery_person_id BIGINT NOT NULL,
    customer_id BIGINT NOT NULL,
    content TEXT,
    rating INT CHECK (rating >= 1 AND rating <= 5),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (delivery_person_id) REFERENCES delivery_persons(delivery_person_id) ON DELETE CASCADE,
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id) ON DELETE CASCADE
);

-- 결제 테이블
CREATE TABLE payments (
    payment_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    customer_id BIGINT NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    method VARCHAR(30) NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'PENDING',
    paid_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (order_id) REFERENCES orders(order_id) ON DELETE CASCADE,
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id) ON DELETE CASCADE
);

-- 결제 취소 테이블
CREATE TABLE payment_cancellations (
    cancellation_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    payment_id BIGINT NOT NULL,
    cancel_reason TEXT,
    cancelled_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (payment_id) REFERENCES payments(payment_id) ON DELETE CASCADE
);

-- 결제 통계 테이블
CREATE TABLE payment_statistics (
    store_id BIGINT NOT NULL,
    total_sales_day DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    total_sales_month DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    date DATE NOT NULL,
    PRIMARY KEY (store_id, date),
    FOREIGN KEY (store_id) REFERENCES stores(store_id) ON DELETE CASCADE
);

-- 프로모션 테이블
CREATE TABLE promotions (
    promotion_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    store_id BIGINT NOT NULL,
    title VARCHAR(100) NOT NULL,
    description TEXT,
    discount_rate DECIMAL(5,2) NOT NULL,
    start_date DATETIME NOT NULL,
    end_date DATETIME NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (store_id) REFERENCES stores(store_id) ON DELETE CASCADE
);

-- 리뷰 테이블
CREATE TABLE reviews (
    review_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    customer_id BIGINT NOT NULL,
    store_id BIGINT NOT NULL,
    delivery_person_id BIGINT,
    content TEXT,
    rating INT CHECK (rating >= 1 AND rating <= 5),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (order_id) REFERENCES orders(order_id) ON DELETE CASCADE,
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id) ON DELETE CASCADE,
    FOREIGN KEY (store_id) REFERENCES stores(store_id) ON DELETE CASCADE,
    FOREIGN KEY (delivery_person_id) REFERENCES delivery_persons(delivery_person_id) ON DELETE SET NULL
);

-- 리뷰 답글 테이블
CREATE TABLE review_replies (
    reply_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    review_id BIGINT NOT NULL,
    store_owner_id BIGINT NOT NULL,
    content TEXT,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (review_id) REFERENCES reviews(review_id) ON DELETE CASCADE,
    FOREIGN KEY (store_owner_id) REFERENCES store_owners(owner_id) ON DELETE CASCADE
);

-- 알림 테이블
CREATE TABLE notifications (
    notification_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    user_type VARCHAR(30) NOT NULL,
    title VARCHAR(100) NOT NULL,
    message TEXT NOT NULL,
    is_read BOOLEAN DEFAULT FALSE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user_notifications (user_id, user_type, created_at)
);

-- 초기 데이터 삽입
INSERT INTO store_category (name) VALUES 
('한식'), ('중식'), ('일식'), ('양식'), ('치킨'), ('피자'), ('족발/보쌈'), ('야식'), ('분식'), ('카페/디저트');

-- 인덱스 생성 (성능 최적화)
CREATE INDEX idx_customers_email ON customers(email);
CREATE INDEX idx_stores_category ON stores(category_id);
CREATE INDEX idx_orders_customer ON orders(customer_id);
CREATE INDEX idx_orders_store ON orders(store_id);
CREATE INDEX idx_orders_status ON orders(status);
CREATE INDEX idx_orders_time ON orders(order_time);
CREATE INDEX idx_deliveries_person ON deliveries(delivery_person_id);
CREATE INDEX idx_deliveries_status ON deliveries(status);
CREATE INDEX idx_payments_status ON payments(status);
CREATE INDEX idx_reviews_store ON reviews(store_id);
CREATE INDEX idx_reviews_rating ON reviews(rating);

-- 샘플 데이터 삽입
INSERT INTO users (email, name, phone_number) VALUES
('admin@delivery.com', '관리자', '010-1234-5678'),
('user1@delivery.com', '김철수', '010-1111-2222'),
('user2@delivery.com', '이영희', '010-3333-4444'),
('user3@delivery.com', '박민수', '010-5555-6666')
ON DUPLICATE KEY UPDATE name = VALUES(name), phone_number = VALUES(phone_number);

INSERT INTO orders (user_id, restaurant_name, menu_items, total_amount, delivery_address) VALUES
ON DUPLICATE KEY UPDATE
 restaurant_name = VALUES(restaurant_name),
 menu_items = VALUES(menu_items),
 total_amount = VALUES(total_amount),
 delivery_address = VALUES(delivery_address);

COMMIT;