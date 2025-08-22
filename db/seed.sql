-- Smart Hotel Management - Sample Seed Data
-- Run AFTER db/schema.sql

USE smarthoteldb;

-- 1) Users
INSERT INTO users (email, username, password, full_name, phone, role)
VALUES
  ('admin@example.com', 'admin', '$2a$10$5Cj0Hc1J0J5b8t3Gm3qH8uPYo9b2kqf2p7sJv4t1U0ZJwJ2Yx6k1a', 'System Admin', '0900000000', 'ADMIN'),
  ('reception1@example.com', 'reception1', '$2a$10$5Cj0Hc1J0J5b8t3Gm3qH8uPYo9b2kqf2p7sJv4t1U0ZJwJ2Yx6k1a', 'Reception One', '0900000001', 'RECEPTION'),
  ('housekeeping1@example.com', 'hk1', '$2a$10$5Cj0Hc1J0J5b8t3Gm3qH8uPYo9b2kqf2p7sJv4t1U0ZJwJ2Yx6k1a', 'Housekeeping One', '0900000002', 'HOUSEKEEPING'),
  ('accountant1@example.com', 'acc1', '$2a$10$5Cj0Hc1J0J5b8t3Gm3qH8uPYo9b2kqf2p7sJv4t1U0ZJwJ2Yx6k1a', 'Accountant One', '0900000003', 'ACCOUNTANT'),
  ('alice@example.com', 'alice', '$2a$10$5Cj0Hc1J0J5b8t3Gm3qH8uPYo9b2kqf2p7sJv4t1U0ZJwJ2Yx6k1a', 'Alice Nguyen', '0900001001', 'CUSTOMER'),
  ('bob@example.com', 'bob', '$2a$10$5Cj0Hc1J0J5b8t3Gm3qH8uPYo9b2kqf2p7sJv4t1U0ZJwJ2Yx6k1a', 'Bob Tran', '0900001002', 'CUSTOMER');
-- Ghi chú: mật khẩu là chuỗi bcrypt mẫu; có thể thay bằng của bạn

-- 1b) Customer profiles
INSERT INTO customer_profiles (user_id, dob, address, loyalty_point, notes)
VALUES
  ((SELECT id FROM users WHERE username='alice'), '1997-05-20', 'HCM, Vietnam', 120, 'Allergy: none'),
  ((SELECT id FROM users WHERE username='bob'), '1995-11-02', 'HN, Vietnam', 60, 'Vegan');

-- 2) Room types
INSERT INTO room_types (name, price, capacity, description, active)
VALUES
  ('Standard', 800000.00, 2, 'Phòng tiêu chuẩn 2 khách', TRUE),
  ('Deluxe', 1200000.00, 3, 'Phòng deluxe rộng rãi', TRUE),
  ('Suite', 2200000.00, 4, 'Phòng suite cao cấp', TRUE);

-- 2b) Rooms (dùng room_number; id tự tăng)
INSERT INTO rooms (room_number, room_type_id, floor, status, note)
VALUES
  ('101', (SELECT id FROM room_types WHERE name='Standard'), 1, 'AVAILABLE', 'Standard 101'),
  ('102', (SELECT id FROM room_types WHERE name='Standard'), 1, 'AVAILABLE', 'Standard 102'),
  ('201', (SELECT id FROM room_types WHERE name='Deluxe'), 2, 'AVAILABLE', 'Deluxe 201'),
  ('202', (SELECT id FROM room_types WHERE name='Deluxe'), 2, 'AVAILABLE', 'Deluxe 202'),
  ('301', (SELECT id FROM room_types WHERE name='Suite'), 3, 'AVAILABLE', 'Suite 301');

-- 4) Services
INSERT INTO services (name, price, description, active)
VALUES
  ('Spa 60 phút', 400000.00, 'Gói spa thư giãn 60 phút', TRUE),
  ('Đưa đón sân bay', 300000.00, 'Đưa đón 1 chiều sân bay', TRUE),
  ('Nhà hàng', 0.00, 'Dịch vụ nhà hàng theo món', TRUE);

-- 3) Reservations (2 đơn ví dụ)
INSERT INTO reservations (customer_id, check_in, check_out, status, created_by)
VALUES
  ((SELECT id FROM users WHERE username='alice'), '2025-09-01', '2025-09-03', 'CONFIRMED', (SELECT id FROM users WHERE username='reception1')),
  ((SELECT id FROM users WHERE username='bob'), '2025-09-05', '2025-09-07', 'HELD', (SELECT id FROM users WHERE username='reception1'));

-- 3b) Reservation rooms (mỗi đơn nhiều phòng)
INSERT INTO reservation_rooms (reservation_id, room_id, price_per_night, notes)
VALUES
  ((SELECT r.id FROM reservations r JOIN users u ON u.id=r.customer_id WHERE u.username='alice' AND r.check_in='2025-09-01' AND r.check_out='2025-09-03'), (SELECT rm.id FROM rooms rm WHERE rm.room_number='101'), 800000.00, 'Standard 101'),
  ((SELECT r.id FROM reservations r JOIN users u ON u.id=r.customer_id WHERE u.username='alice' AND r.check_in='2025-09-01' AND r.check_out='2025-09-03'), (SELECT rm.id FROM rooms rm WHERE rm.room_number='201'), 1200000.00, 'Deluxe 201'),
  ((SELECT r.id FROM reservations r JOIN users u ON u.id=r.customer_id WHERE u.username='bob' AND r.check_in='2025-09-05' AND r.check_out='2025-09-07'), (SELECT rm.id FROM rooms rm WHERE rm.room_number='102'), 800000.00, 'Standard 102');

-- 4b) Service orders cho đơn của Alice
INSERT INTO service_orders (reservation_id, service_id, qty, unit_price, amount, notes)
VALUES
  (
    (SELECT r.id FROM reservations r JOIN users u ON u.id=r.customer_id WHERE u.username='alice' AND r.check_in='2025-09-01' AND r.check_out='2025-09-03'),
    (SELECT id FROM services WHERE name='Spa 60 phút'),
    2, 400000.00, 800000.00, '2 suất spa'
  ),
  (
    (SELECT r.id FROM reservations r JOIN users u ON u.id=r.customer_id WHERE u.username='alice' AND r.check_in='2025-09-01' AND r.check_out='2025-09-03'),
    (SELECT id FROM services WHERE name='Đưa đón sân bay'),
    1, 300000.00, 300000.00, 'Đón sân bay'
  );

-- 5) Payments
INSERT INTO payments (reservation_id, amount, method, transaction_id, status, paid_at)
VALUES
  (
    (SELECT r.id FROM reservations r JOIN users u ON u.id=r.customer_id WHERE u.username='alice' AND r.check_in='2025-09-01' AND r.check_out='2025-09-03'),
    3100000.00, 'CARD', 'TXN-ALICE-0001', 'SUCCESS', '2025-08-25 10:00:00'
  ),
  (
    (SELECT r.id FROM reservations r JOIN users u ON u.id=r.customer_id WHERE u.username='bob' AND r.check_in='2025-09-05' AND r.check_out='2025-09-07'),
    1600000.00, 'CASH', NULL, 'SUCCESS', NULL
  );

-- 5b) Invoices
INSERT INTO invoices (reservation_id, total_amount)
VALUES
  ((SELECT r.id FROM reservations r JOIN users u ON u.id=r.customer_id WHERE u.username='alice' AND r.check_in='2025-09-01' AND r.check_out='2025-09-03'), 3100000.00),
  ((SELECT r.id FROM reservations r JOIN users u ON u.id=r.customer_id WHERE u.username='bob' AND r.check_in='2025-09-05' AND r.check_out='2025-09-07'), 1600000.00);

-- 6) Housekeeping tasks
INSERT INTO housekeeping_tasks (room_id, task, status, assignee_id, due_time, notes)
VALUES
  ((SELECT rm.id FROM rooms rm WHERE rm.room_number='101'), 'Vệ sinh sau check-out', 'PENDING', (SELECT id FROM users WHERE username='hk1'), '2025-09-03 12:00:00', 'Ưu tiên'),
  ((SELECT rm.id FROM rooms rm WHERE rm.room_number='201'), 'Bổ sung minibar', 'IN_PROGRESS', (SELECT id FROM users WHERE username='hk1'), '2025-09-01 14:00:00', '');

-- 6b) Reviews (sau khi check-out)
INSERT INTO reviews (reservation_id, rating, comment, visible)
VALUES
  ((SELECT r.id FROM reservations r JOIN users u ON u.id=r.customer_id WHERE u.username='alice' AND r.check_in='2025-09-01' AND r.check_out='2025-09-03'), 5, 'Phòng sạch, dịch vụ tốt', TRUE);


-- ========= BỔ SUNG DỮ LIỆU MẪU MỞ RỘNG =========

-- Users bổ sung
INSERT INTO users (email, username, password, full_name, phone, role)
VALUES
  ('reception2@example.com', 'reception2', '$2a$10$5Cj0Hc1J0J5b8t3Gm3qH8uPYo9b2kqf2p7sJv4t1U0ZJwJ2Yx6k1a', 'Reception Two', '0900000004', 'RECEPTION'),
  ('housekeeping2@example.com', 'hk2', '$2a$10$5Cj0Hc1J0J5b8t3Gm3qH8uPYo9b2kqf2p7sJv4t1U0ZJwJ2Yx6k1a', 'Housekeeping Two', '0900000005', 'HOUSEKEEPING'),
  ('accountant2@example.com', 'acc2', '$2a$10$5Cj0Hc1J0J5b8t3Gm3qH8uPYo9b2kqf2p7sJv4t1U0ZJwJ2Yx6k1a', 'Accountant Two', '0900000006', 'ACCOUNTANT'),
  ('charlie@example.com', 'charlie', '$2a$10$5Cj0Hc1J0J5b8t3Gm3qH8uPYo9b2kqf2p7sJv4t1U0ZJwJ2Yx6k1a', 'Charlie Pham', '0900001003', 'CUSTOMER'),
  ('diana@example.com', 'diana', '$2a$10$5Cj0Hc1J0J5b8t3Gm3qH8uPYo9b2kqf2p7sJv4t1U0ZJwJ2Yx6k1a', 'Diana Le', '0900001004', 'CUSTOMER'),
  ('eric@example.com', 'eric', '$2a$10$5Cj0Hc1J0J5b8t3Gm3qH8uPYo9b2kqf2p7sJv4t1U0ZJwJ2Yx6k1a', 'Eric Vo', '0900001005', 'CUSTOMER');

-- Customer profiles bổ sung
INSERT INTO customer_profiles (user_id, dob, address, loyalty_point, notes)
VALUES
  ((SELECT id FROM users WHERE username='charlie'), '1994-03-10', 'Da Nang, Vietnam', 40, ''),
  ((SELECT id FROM users WHERE username='diana'), '1998-07-22', 'Can Tho, Vietnam', 25, 'Yêu cầu tầng cao'),
  ((SELECT id FROM users WHERE username='eric'), '1992-12-05', 'Hue, Vietnam', 10, 'Không hút thuốc');

-- Thêm nhiều phòng
INSERT INTO rooms (room_number, room_type_id, floor, status, note)
VALUES
  ('103', (SELECT id FROM room_types WHERE name='Standard'), 1, 'AVAILABLE', 'Standard 103'),
  ('104', (SELECT id FROM room_types WHERE name='Standard'), 1, 'AVAILABLE', 'Standard 104'),
  ('105', (SELECT id FROM room_types WHERE name='Standard'), 1, 'AVAILABLE', 'Standard 105'),
  ('106', (SELECT id FROM room_types WHERE name='Standard'), 1, 'AVAILABLE', 'Standard 106'),
  ('203', (SELECT id FROM room_types WHERE name='Deluxe'), 2, 'AVAILABLE', 'Deluxe 203'),
  ('204', (SELECT id FROM room_types WHERE name='Deluxe'), 2, 'AVAILABLE', 'Deluxe 204'),
  ('205', (SELECT id FROM room_types WHERE name='Deluxe'), 2, 'AVAILABLE', 'Deluxe 205'),
  ('206', (SELECT id FROM room_types WHERE name='Deluxe'), 2, 'AVAILABLE', 'Deluxe 206'),
  ('302', (SELECT id FROM room_types WHERE name='Suite'), 3, 'AVAILABLE', 'Suite 302'),
  ('303', (SELECT id FROM room_types WHERE name='Suite'), 3, 'AVAILABLE', 'Suite 303'),
  ('304', (SELECT id FROM room_types WHERE name='Suite'), 3, 'AVAILABLE', 'Suite 304');

-- Dịch vụ bổ sung
INSERT INTO services (name, price, description, active)
VALUES
  ('Giặt ủi', 100000.00, 'Giặt ủi quần áo theo ký', TRUE),
  ('Thuê xe', 500000.00, 'Thuê xe 4 chỗ trong thành phố', TRUE),
  ('Bữa sáng buffet', 150000.00, 'Buffet sáng tại nhà hàng', TRUE);

-- Reservations mới
INSERT INTO reservations (customer_id, check_in, check_out, status, created_by)
VALUES
  ((SELECT id FROM users WHERE username='charlie'), '2025-09-10', '2025-09-12', 'CONFIRMED', (SELECT id FROM users WHERE username='reception2')),
  ((SELECT id FROM users WHERE username='diana'),   '2025-09-01', '2025-09-02', 'CHECKED_OUT', (SELECT id FROM users WHERE username='reception1')),
  ((SELECT id FROM users WHERE username='eric'),    '2025-09-02', '2025-09-05', 'CHECKED_IN',  (SELECT id FROM users WHERE username='reception2')),
  ((SELECT id FROM users WHERE username='bob'),     '2025-09-15', '2025-09-18', 'HELD',        (SELECT id FROM users WHERE username='reception1'));

-- Gán phòng cho các đơn mới (tránh overlap với dữ liệu cũ)
INSERT INTO reservation_rooms (reservation_id, room_id, price_per_night, notes)
VALUES
  -- Charlie: 203, 204 (Deluxe)
  ((SELECT r.id FROM reservations r JOIN users u ON u.id=r.customer_id WHERE u.username='charlie' AND r.check_in='2025-09-10' AND r.check_out='2025-09-12'), (SELECT rm.id FROM rooms rm WHERE rm.room_number='203'), 1200000.00, 'Deluxe 203'),
  ((SELECT r.id FROM reservations r JOIN users u ON u.id=r.customer_id WHERE u.username='charlie' AND r.check_in='2025-09-10' AND r.check_out='2025-09-12'), (SELECT rm.id FROM rooms rm WHERE rm.room_number='204'), 1200000.00, 'Deluxe 204'),
  -- Diana: 102, 103 (Standard)
  ((SELECT r.id FROM reservations r JOIN users u ON u.id=r.customer_id WHERE u.username='diana' AND r.check_in='2025-09-01' AND r.check_out='2025-09-02'), (SELECT rm.id FROM rooms rm WHERE rm.room_number='102'), 800000.00, 'Standard 102'),
  ((SELECT r.id FROM reservations r JOIN users u ON u.id=r.customer_id WHERE u.username='diana' AND r.check_in='2025-09-01' AND r.check_out='2025-09-02'), (SELECT rm.id FROM rooms rm WHERE rm.room_number='103'), 800000.00, 'Standard 103'),
  -- Eric: 302 (Suite)
  ((SELECT r.id FROM reservations r JOIN users u ON u.id=r.customer_id WHERE u.username='eric' AND r.check_in='2025-09-02' AND r.check_out='2025-09-05'), (SELECT rm.id FROM rooms rm WHERE rm.room_number='302'), 2200000.00, 'Suite 302'),
  -- Bob (lần 2): 104, 105 (Standard)
  ((SELECT r.id FROM reservations r JOIN users u ON u.id=r.customer_id WHERE u.username='bob' AND r.check_in='2025-09-15' AND r.check_out='2025-09-18'), (SELECT rm.id FROM rooms rm WHERE rm.room_number='104'), 800000.00, 'Standard 104'),
  ((SELECT r.id FROM reservations r JOIN users u ON u.id=r.customer_id WHERE u.username='bob' AND r.check_in='2025-09-15' AND r.check_out='2025-09-18'), (SELECT rm.id FROM rooms rm WHERE rm.room_number='105'), 800000.00, 'Standard 105');

-- Service orders mới
INSERT INTO service_orders (reservation_id, service_id, qty, unit_price, amount, notes)
VALUES
  -- Charlie
  ((SELECT r.id FROM reservations r JOIN users u ON u.id=r.customer_id WHERE u.username='charlie' AND r.check_in='2025-09-10' AND r.check_out='2025-09-12'), (SELECT id FROM services WHERE name='Bữa sáng buffet'), 2, 150000.00, 300000.00, ''),
  ((SELECT r.id FROM reservations r JOIN users u ON u.id=r.customer_id WHERE u.username='charlie' AND r.check_in='2025-09-10' AND r.check_out='2025-09-12'), (SELECT id FROM services WHERE name='Giặt ủi'), 1, 100000.00, 100000.00, ''),
  -- Diana
  ((SELECT r.id FROM reservations r JOIN users u ON u.id=r.customer_id WHERE u.username='diana' AND r.check_in='2025-09-01' AND r.check_out='2025-09-02'), (SELECT id FROM services WHERE name='Đưa đón sân bay'), 1, 300000.00, 300000.00, ''),
  -- Eric
  ((SELECT r.id FROM reservations r JOIN users u ON u.id=r.customer_id WHERE u.username='eric' AND r.check_in='2025-09-02' AND r.check_out='2025-09-05'), (SELECT id FROM services WHERE name='Thuê xe'), 1, 500000.00, 500000.00, '3 ngày');

-- Payments mới
INSERT INTO payments (reservation_id, amount, method, transaction_id, status, paid_at)
VALUES
  ((SELECT r.id FROM reservations r JOIN users u ON u.id=r.customer_id WHERE u.username='charlie' AND r.check_in='2025-09-10' AND r.check_out='2025-09-12'), 2700000.00, 'CARD', 'TXN-CHARLIE-0001', 'SUCCESS', '2025-09-05 09:00:00'),
  ((SELECT r.id FROM reservations r JOIN users u ON u.id=r.customer_id WHERE u.username='diana' AND r.check_in='2025-09-01' AND r.check_out='2025-09-02'), 1900000.00, 'CASH', NULL, 'SUCCESS', '2025-09-01 20:00:00'),
  ((SELECT r.id FROM reservations r JOIN users u ON u.id=r.customer_id WHERE u.username='eric' AND r.check_in='2025-09-02' AND r.check_out='2025-09-05'), 7200000.00, 'CARD', 'TXN-ERIC-0001', 'SUCCESS', '2025-09-02 12:00:00');

-- Invoices mới (mỗi reservation 1 invoice)
INSERT INTO invoices (reservation_id, total_amount)
VALUES
  ((SELECT r.id FROM reservations r JOIN users u ON u.id=r.customer_id WHERE u.username='charlie' AND r.check_in='2025-09-10' AND r.check_out='2025-09-12'), 2700000.00),
  ((SELECT r.id FROM reservations r JOIN users u ON u.id=r.customer_id WHERE u.username='diana' AND r.check_in='2025-09-01' AND r.check_out='2025-09-02'), 1900000.00),
  ((SELECT r.id FROM reservations r JOIN users u ON u.id=r.customer_id WHERE u.username='eric' AND r.check_in='2025-09-02' AND r.check_out='2025-09-05'), 7200000.00);

-- Housekeeping bổ sung
INSERT INTO housekeeping_tasks (room_id, task, status, assignee_id, due_time, notes)
VALUES
  ((SELECT rm.id FROM rooms rm WHERE rm.room_number='102'), 'Tổng vệ sinh sau check-out', 'DONE',       (SELECT id FROM users WHERE username='hk2'), '2025-09-02 11:00:00', ''),
  ((SELECT rm.id FROM rooms rm WHERE rm.room_number='203'), 'Chuẩn bị phòng trước check-in', 'PENDING', (SELECT id FROM users WHERE username='hk2'), '2025-09-10 08:00:00', ''),
  ((SELECT rm.id FROM rooms rm WHERE rm.room_number='302'), 'Thay ga giường', 'IN_PROGRESS',           (SELECT id FROM users WHERE username='hk1'), '2025-09-03 10:00:00', '');

-- Reviews bổ sung
INSERT INTO reviews (reservation_id, rating, comment, visible)
VALUES
  ((SELECT r.id FROM reservations r JOIN users u ON u.id=r.customer_id WHERE u.username='diana' AND r.check_in='2025-09-01' AND r.check_out='2025-09-02'), 4, 'Ổn, vị trí tốt', TRUE);


