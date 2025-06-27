-- MySQL 복제 사용자 생성
CREATE USER IF NOT EXISTS 'repl'@'%' IDENTIFIED WITH mysql_native_password BY 'replpass';
GRANT REPLICATION SLAVE ON *.* TO 'repl'@'%';
FLUSH PRIVILEGES;

-- 데이터베이스 선택
USE delivery;

-- 사용자 테이블 생성
CREATE TABLE IF NOT EXISTS users (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  email VARCHAR(100) UNIQUE NOT NULL,
  name VARCHAR(50) NOT NULL,
  phone_number VARCHAR(20),
  status ENUM('ACTIVE', 'INACTIVE', 'SUSPENDED') DEFAULT 'ACTIVE',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX idx_email (email),
  INDEX idx_status (status)
);

-- 배달 주문 테이블 생성
CREATE TABLE IF NOT EXISTS orders (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  restaurant_name VARCHAR(100) NOT NULL,
  menu_items TEXT NOT NULL,
  total_amount DECIMAL(10,2) NOT NULL,
  status ENUM('PENDING', 'CONFIRMED', 'PREPARING', 'DELIVERING', 'COMPLETED', 'CANCELLED') DEFAULT 'PENDING',
  delivery_address TEXT NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (user_id) REFERENCES users(id),
  INDEX idx_user_id (user_id),
  INDEX idx_status (status),
  INDEX idx_created_at (created_at)
);

-- 샘플 데이터 삽입
INSERT INTO users (email, name, phone_number) VALUES 
  ('admin@delivery.com', '관리자', '010-1234-5678'),
  ('user1@delivery.com', '김철수', '010-1111-2222'),
  ('user2@delivery.com', '이영희', '010-3333-4444'),
  ('user3@delivery.com', '박민수', '010-5555-6666')
ON DUPLICATE KEY UPDATE name = VALUES(name), phone_number = VALUES(phone_number);

INSERT INTO orders (user_id, restaurant_name, menu_items, total_amount, delivery_address) VALUES 
  (2, '맛있는 치킨', '후라이드 치킨 1마리, 콜라 1개', 25000.00, '서울시 강남구 테헤란로 123'),
  (3, '신선한 피자', '페퍼로니 피자 L, 감자튀김 1개', 35000.00, '서울시 서초구 서초대로 456'),
  (2, '한식당', '김치찌개 1개, 밥 2개, 반찬 3개', 18000.00, '서울시 강남구 테헤란로 123')
ON DUPLICATE KEY UPDATE 
  restaurant_name = VALUES(restaurant_name),
  menu_items = VALUES(menu_items),
  total_amount = VALUES(total_amount),
  delivery_address = VALUES(delivery_address);