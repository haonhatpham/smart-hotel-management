-- Smart Hotel Management - Database Schema
-- Course-level schema with multi-room per reservation

CREATE DATABASE IF NOT EXISTS smarthoteldb
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;
USE smarthoteldb;

SET NAMES utf8mb4;
SET time_zone = '+00:00';

-- 1) Người dùng + hồ sơ khách hàng
CREATE TABLE IF NOT EXISTS users (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  email VARCHAR(255) NOT NULL UNIQUE,
  username VARCHAR(50) NOT NULL UNIQUE,
  password VARCHAR(255) NOT NULL,
  full_name VARCHAR(255),
  phone VARCHAR(30),
  role ENUM('ADMIN','RECEPTION','HOUSEKEEPING','ACCOUNTANT','CUSTOMER') NOT NULL DEFAULT 'CUSTOMER',
  enabled BOOLEAN NOT NULL DEFAULT TRUE,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS customer_profiles (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL UNIQUE,
  dob DATE NULL,
  address VARCHAR(500),
  loyalty_point INT NOT NULL DEFAULT 0,
  notes TEXT,
  CONSTRAINT fk_customer_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 2) Danh mục phòng
CREATE TABLE IF NOT EXISTS room_types (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(120) NOT NULL UNIQUE,
  price DECIMAL(12,2) NOT NULL DEFAULT 0,
  capacity INT NOT NULL,
  description TEXT,
  active BOOLEAN NOT NULL DEFAULT TRUE
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS rooms (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  room_number VARCHAR(20) NOT NULL UNIQUE,
  room_type_id BIGINT NOT NULL,
  floor INT,
  status ENUM('AVAILABLE','OCCUPIED','CLEANING','MAINTENANCE') NOT NULL DEFAULT 'AVAILABLE',
  note TEXT,
  CONSTRAINT fk_room_type FOREIGN KEY (room_type_id) REFERENCES room_types(id) ON DELETE RESTRICT
) ENGINE=InnoDB;
CREATE INDEX idx_rooms_type_status ON rooms(room_type_id, status);

-- 3) Đặt phòng (nhiều phòng/đơn)
CREATE TABLE IF NOT EXISTS reservations (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  customer_id BIGINT NOT NULL,
  check_in DATE NOT NULL,
  check_out DATE NOT NULL,
  status ENUM('HELD','CONFIRMED','CHECKED_IN','CHECKED_OUT','CANCELLED') NOT NULL DEFAULT 'HELD',
  created_by BIGINT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NULL,
  CONSTRAINT fk_res_customer FOREIGN KEY (customer_id) REFERENCES users(id) ON DELETE RESTRICT,
  CONSTRAINT fk_res_created_by FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE SET NULL
) ENGINE=InnoDB;
CREATE INDEX idx_res_customer ON reservations(customer_id);
CREATE INDEX idx_res_dates ON reservations(check_in, check_out);
CREATE INDEX idx_res_status ON reservations(status);

CREATE TABLE IF NOT EXISTS reservation_rooms (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  reservation_id BIGINT NOT NULL,
  room_id BIGINT NOT NULL,
  price_per_night DECIMAL(12,2) NOT NULL DEFAULT 0,
  notes TEXT,
  CONSTRAINT fk_rr_res FOREIGN KEY (reservation_id) REFERENCES reservations(id) ON DELETE CASCADE,
  CONSTRAINT fk_rr_room FOREIGN KEY (room_id) REFERENCES rooms(id) ON DELETE RESTRICT,
  CONSTRAINT uq_reservation_room UNIQUE (reservation_id, room_id)
) ENGINE=InnoDB;
CREATE INDEX idx_rr_res ON reservation_rooms(reservation_id);
CREATE INDEX idx_rr_room ON reservation_rooms(room_id);

-- 4) Dịch vụ và đặt dịch vụ theo đơn
CREATE TABLE IF NOT EXISTS services (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(120) NOT NULL,
  price DECIMAL(12,2) NOT NULL DEFAULT 0,
  description TEXT,
  active BOOLEAN NOT NULL DEFAULT TRUE
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS service_orders (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  reservation_id BIGINT NOT NULL,
  service_id BIGINT NOT NULL,
  qty INT NOT NULL DEFAULT 1,
  unit_price DECIMAL(12,2) NOT NULL DEFAULT 0,
  amount DECIMAL(12,2) NOT NULL DEFAULT 0,
  ordered_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  notes TEXT,
  CONSTRAINT fk_srv_order_res FOREIGN KEY (reservation_id) REFERENCES reservations(id) ON DELETE CASCADE,
  CONSTRAINT fk_srv_order_service FOREIGN KEY (service_id) REFERENCES services(id) ON DELETE RESTRICT
) ENGINE=InnoDB;
CREATE INDEX idx_service_orders_res ON service_orders(reservation_id);
CREATE INDEX idx_srv_service ON service_orders(service_id);

-- 5) Thanh toán, hoá đơn (tối giản)
CREATE TABLE IF NOT EXISTS payments (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  reservation_id BIGINT NOT NULL,
  amount DECIMAL(12,2) NOT NULL,
  method ENUM('CARD','WALLET','CASH') NOT NULL,
  transaction_id VARCHAR(100) NULL,
  status ENUM('SUCCESS','FAILED') NOT NULL DEFAULT 'SUCCESS',
  paid_at TIMESTAMP NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_payment_res FOREIGN KEY (reservation_id) REFERENCES reservations(id) ON DELETE CASCADE
) ENGINE=InnoDB;
CREATE INDEX idx_payments_res ON payments(reservation_id);
CREATE INDEX idx_payments_status ON payments(status);

CREATE TABLE IF NOT EXISTS invoices (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  reservation_id BIGINT NOT NULL UNIQUE,
  total_amount DECIMAL(12,2) NOT NULL DEFAULT 0,
  issued_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_invoice_res FOREIGN KEY (reservation_id) REFERENCES reservations(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 6) Housekeeping và đánh giá
CREATE TABLE IF NOT EXISTS housekeeping_tasks (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  room_id BIGINT NOT NULL,
  task VARCHAR(255) NOT NULL,
  status ENUM('PENDING','IN_PROGRESS','DONE') NOT NULL DEFAULT 'PENDING',
  assignee_id BIGINT NULL,
  due_time TIMESTAMP NULL,
  notes TEXT,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NULL,
  CONSTRAINT fk_hk_room FOREIGN KEY (room_id) REFERENCES rooms(id) ON DELETE CASCADE,
  CONSTRAINT fk_hk_user FOREIGN KEY (assignee_id) REFERENCES users(id) ON DELETE SET NULL
) ENGINE=InnoDB;
CREATE INDEX idx_hk_room_status ON housekeeping_tasks(room_id, status);
CREATE INDEX idx_hk_assignee ON housekeeping_tasks(assignee_id);

CREATE TABLE IF NOT EXISTS reviews (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  reservation_id BIGINT NOT NULL,
  rating INT NOT NULL,
  comment TEXT,
  visible BOOLEAN NOT NULL DEFAULT TRUE,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT chk_rating CHECK (rating BETWEEN 1 AND 5),
  CONSTRAINT fk_review_res FOREIGN KEY (reservation_id) REFERENCES reservations(id) ON DELETE CASCADE
) ENGINE=InnoDB;
CREATE INDEX idx_reviews_res ON reviews(reservation_id);


