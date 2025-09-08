-- Compact seed data only (no table creation) for Smart Hotel
-- Goal: 10 rooms total, multi-room reservations trải đều 12 tháng 2025 để occupancy cao, đồ thị đẹp
-- Safe to re-run: we clear data then insert fixed set. Requires existing schema (from schema.sql)

USE smarthoteldb;

INSERT INTO users (email, username, password, full_name, phone, role) VALUES
 ('admin@example.com','admin','$2a$10$5X9k5N1sTc1/CjVH5XJoje3QMYijH3ETpgkox00R0MdPaJPPrf7wO','Admin','0900000000','ADMIN'),
 ('reception@example.com','reception','$2a$10$5X9k5N1sTc1/CjVH5XJoje3QMYijH3ETpgkox00R0MdPaJPPrf7wO','Reception','0900000001','RECEPTION'),
 ('alice@example.com','alice','$2a$10$5X9k5N1sTc1/CjVH5XJoje3QMYijH3ETpgkox00R0MdPaJPPrf7wO','Alice','0900001001','CUSTOMER'),
 ('bob@example.com','bob','$2a$10$5X9k5N1sTc1/CjVH5XJoje3QMYijH3ETpgkox00R0MdPaJPPrf7wO','Bob','0900001002','CUSTOMER'),
 ('charlie@example.com','charlie','$2a$10$5X9k5N1sTc1/CjVH5XJoje3QMYijH3ETpgkox00R0MdPaJPPrf7wO','Charlie','0900001003','CUSTOMER'),
 ('diana@example.com','diana','$2a$10$5X9k5N1sTc1/CjVH5XJoje3QMYijH3ETpgkox00R0MdPaJPPrf7wO','Diana','0900001004','CUSTOMER'),
 ('eric@example.com','eric','$2a$10$5X9k5N1sTc1/CjVH5XJoje3QMYijH3ETpgkox00R0MdPaJPPrf7wO','Eric','0900001005','CUSTOMER');

INSERT INTO customer_profiles (user_id, dob, address) VALUES
 ((SELECT id FROM users WHERE username='alice'),'1997-05-20','HCM'),
 ((SELECT id FROM users WHERE username='bob'),'1995-11-02','HN'),
 ((SELECT id FROM users WHERE username='charlie'),'1994-03-10','DN'),
 ((SELECT id FROM users WHERE username='diana'),'1998-07-22','CT'),
 ((SELECT id FROM users WHERE username='eric'),'1992-12-05','Hue');

-- Danh mục loại phòng + 10 phòng
INSERT INTO room_types (name, price, capacity, description, active) VALUES
 ('Standard', 800000, 2, 'Phòng tiêu chuẩn', TRUE),
 ('Deluxe', 1200000, 3, 'Phòng deluxe', TRUE),
 ('Suite', 2200000, 4, 'Phòng suite', TRUE);

INSERT INTO rooms (room_number, room_type_id, floor, status, note, image_url) VALUES
 ('101',(SELECT id FROM room_types WHERE name='Standard'),1,'AVAILABLE','', 'https://images.unsplash.com/photo-1554995207-c18c203602cb?q=80&w=1200&auto=format'),
 ('102',(SELECT id FROM room_types WHERE name='Standard'),1,'AVAILABLE','', 'https://images.unsplash.com/photo-1505693416388-ac5ce068fe85?q=80&w=1200&auto=format'),
 ('103',(SELECT id FROM room_types WHERE name='Standard'),1,'AVAILABLE','', 'https://images.unsplash.com/photo-1505691723518-36a5ac3b2d95?q=80&w=1200&auto=format'),
 ('104',(SELECT id FROM room_types WHERE name='Standard'),1,'AVAILABLE','', 'https://images.unsplash.com/photo-1540518614846-7eded433c457?q=80&w=1200&auto=format'),
 ('105',(SELECT id FROM room_types WHERE name='Standard'),1,'AVAILABLE','', 'https://images.unsplash.com/photo-1493809842364-78817add7ffb?q=80&w=1200&auto=format'),
 ('201',(SELECT id FROM room_types WHERE name='Deluxe'),2,'AVAILABLE','', 'https://images.unsplash.com/photo-1560066984-138dadb4c035?q=80&w=1200&auto=format'),
 ('202',(SELECT id FROM room_types WHERE name='Deluxe'),2,'AVAILABLE','', 'https://images.unsplash.com/photo-1560448204-e02f11c3d0e2?q=80&w=1200&auto=format'),
 ('203',(SELECT id FROM room_types WHERE name='Deluxe'),2,'AVAILABLE','', 'https://images.unsplash.com/photo-1551776235-dde6d4829808?q=80&w=1200&auto=format'),
 ('301',(SELECT id FROM room_types WHERE name='Suite'),3,'AVAILABLE','', 'https://images.unsplash.com/photo-1501117716987-c8e98ddb0f41?q=80&w=1200&auto=format'),
 ('302',(SELECT id FROM room_types WHERE name='Suite'),3,'AVAILABLE','', 'https://images.unsplash.com/photo-1505691723518-36a5ac3b2d95?q=80&w=1200&auto=format');

-- Dịch vụ (tham khảo)
INSERT INTO services (name, price, description, active) VALUES
 ('Breakfast',150000,'Buffet sáng',TRUE),
 ('Airport Transfer',300000,'Đưa đón sân bay',TRUE),
 ('Spa 60m',400000,'Thư giãn 60 phút',TRUE);

-- =============== Đơn đặt nhiều phòng theo 12 tháng năm 2025 ===============
-- Helper macro: dùng SELECT id theo username + room_number để giữ ID ổn định

-- Tháng 1: 3 đêm x 5 phòng = 15
INSERT INTO reservations (customer_id, check_in, check_out, status, created_by)
VALUES ((SELECT id FROM users WHERE username='alice'),'2025-01-18','2025-01-21','CHECKED_OUT',(SELECT id FROM users WHERE username='reception'));
INSERT INTO reservation_rooms (reservation_id, room_id, price_per_night) VALUES
 ((SELECT id FROM reservations WHERE customer_id=(SELECT id FROM users WHERE username='alice') AND check_in='2025-01-18'), (SELECT id FROM rooms WHERE room_number='101'), 800000),
 ((SELECT id FROM reservations WHERE customer_id=(SELECT id FROM users WHERE username='alice') AND check_in='2025-01-18'), (SELECT id FROM rooms WHERE room_number='102'), 800000),
 ((SELECT id FROM reservations WHERE customer_id=(SELECT id FROM users WHERE username='alice') AND check_in='2025-01-18'), (SELECT id FROM rooms WHERE room_number='201'), 1200000),
 ((SELECT id FROM reservations WHERE customer_id=(SELECT id FROM users WHERE username='alice') AND check_in='2025-01-18'), (SELECT id FROM rooms WHERE room_number='202'), 1200000),
 ((SELECT id FROM reservations WHERE customer_id=(SELECT id FROM users WHERE username='alice') AND check_in='2025-01-18'), (SELECT id FROM rooms WHERE room_number='301'), 2200000);
INSERT INTO invoices (reservation_id,total_amount,issued_at)
VALUES ((SELECT id FROM reservations WHERE customer_id=(SELECT id FROM users WHERE username='alice') AND check_in='2025-01-18'), 9000000,'2025-01-21 09:30:00');
INSERT INTO payments (reservation_id, amount, method, transaction_id, status, paid_at)
VALUES ((SELECT id FROM reservations WHERE customer_id=(SELECT id FROM users WHERE username='alice') AND check_in='2025-01-18'), 9000000, 'CARD', 'TXA-2025-01', 'SUCCESS', '2025-01-21 09:35:00');

-- Tháng 2: 4 đêm x 6 phòng = 24
INSERT INTO reservations VALUES (NULL,(SELECT id FROM users WHERE username='bob'),'2025-02-10','2025-02-14','CHECKED_OUT',(SELECT id FROM users WHERE username='reception'),NOW(),NULL);
INSERT INTO reservation_rooms (reservation_id, room_id, price_per_night) VALUES
 ((SELECT id FROM reservations WHERE customer_id=(SELECT id FROM users WHERE username='bob') AND check_in='2025-02-10'), (SELECT id FROM rooms WHERE room_number='101'), 800000),
 ((SELECT id FROM reservations WHERE customer_id=(SELECT id FROM users WHERE username='bob') AND check_in='2025-02-10'), (SELECT id FROM rooms WHERE room_number='103'), 800000),
 ((SELECT id FROM reservations WHERE customer_id=(SELECT id FROM users WHERE username='bob') AND check_in='2025-02-10'), (SELECT id FROM rooms WHERE room_number='104'), 800000),
 ((SELECT id FROM reservations WHERE customer_id=(SELECT id FROM users WHERE username='bob') AND check_in='2025-02-10'), (SELECT id FROM rooms WHERE room_number='203'), 1200000),
 ((SELECT id FROM reservations WHERE customer_id=(SELECT id FROM users WHERE username='bob') AND check_in='2025-02-10'), (SELECT id FROM rooms WHERE room_number='301'), 2200000),
 ((SELECT id FROM reservations WHERE customer_id=(SELECT id FROM users WHERE username='bob') AND check_in='2025-02-10'), (SELECT id FROM rooms WHERE room_number='302'), 2200000);
INSERT INTO invoices VALUES (NULL,(SELECT id FROM reservations WHERE customer_id=(SELECT id FROM users WHERE username='bob') AND check_in='2025-02-10'), 15000000,'2025-02-14 10:00:00');
INSERT INTO payments (reservation_id, amount, method, transaction_id, status, paid_at)
VALUES ((SELECT id FROM reservations WHERE customer_id=(SELECT id FROM users WHERE username='bob') AND check_in='2025-02-10'), 15000000, 'CARD', 'TXB-2025-02', 'SUCCESS', '2025-02-14 10:05:00');

-- Tháng 3: 3 đêm x 4 phòng = 12
INSERT INTO reservations VALUES (NULL,(SELECT id FROM users WHERE username='charlie'),'2025-03-05','2025-03-08','CHECKED_OUT',(SELECT id FROM users WHERE username='reception'),NOW(),NULL);
INSERT INTO reservation_rooms (reservation_id, room_id, price_per_night) VALUES
 ((SELECT id FROM reservations WHERE customer_id=(SELECT id FROM users WHERE username='charlie') AND check_in='2025-03-05'), (SELECT id FROM rooms WHERE room_number='102'), 800000),
 ((SELECT id FROM reservations WHERE customer_id=(SELECT id FROM users WHERE username='charlie') AND check_in='2025-03-05'), (SELECT id FROM rooms WHERE room_number='105'), 800000),
 ((SELECT id FROM reservations WHERE customer_id=(SELECT id FROM users WHERE username='charlie') AND check_in='2025-03-05'), (SELECT id FROM rooms WHERE room_number='201'), 1200000),
 ((SELECT id FROM reservations WHERE customer_id=(SELECT id FROM users WHERE username='charlie') AND check_in='2025-03-05'), (SELECT id FROM rooms WHERE room_number='202'), 1200000);
INSERT INTO invoices VALUES (NULL,(SELECT id FROM reservations WHERE customer_id=(SELECT id FROM users WHERE username='charlie') AND check_in='2025-03-05'), 5600000,'2025-03-08 10:00:00');
INSERT INTO payments (reservation_id, amount, method, transaction_id, status, paid_at)
VALUES ((SELECT id FROM reservations WHERE customer_id=(SELECT id FROM users WHERE username='charlie') AND check_in='2025-03-05'), 5600000, 'CASH', 'TXC-2025-03', 'SUCCESS', '2025-03-08 10:10:00');

-- Tháng 4: 2 đêm x 5 phòng = 10
INSERT INTO reservations VALUES (NULL,(SELECT id FROM users WHERE username='diana'),'2025-04-15','2025-04-17','CHECKED_OUT',(SELECT id FROM users WHERE username='reception'),NOW(),NULL);
INSERT INTO reservation_rooms (reservation_id, room_id, price_per_night) VALUES
 ((SELECT id FROM reservations WHERE customer_id=(SELECT id FROM users WHERE username='diana') AND check_in='2025-04-15'), (SELECT id FROM rooms WHERE room_number='101'), 800000),
 ((SELECT id FROM reservations WHERE customer_id=(SELECT id FROM users WHERE username='diana') AND check_in='2025-04-15'), (SELECT id FROM rooms WHERE room_number='103'), 800000),
 ((SELECT id FROM reservations WHERE customer_id=(SELECT id FROM users WHERE username='diana') AND check_in='2025-04-15'), (SELECT id FROM rooms WHERE room_number='104'), 800000),
 ((SELECT id FROM reservations WHERE customer_id=(SELECT id FROM users WHERE username='diana') AND check_in='2025-04-15'), (SELECT id FROM rooms WHERE room_number='203'), 1200000),
 ((SELECT id FROM reservations WHERE customer_id=(SELECT id FROM users WHERE username='diana') AND check_in='2025-04-15'), (SELECT id FROM rooms WHERE room_number='302'), 2200000);
INSERT INTO invoices VALUES (NULL,(SELECT id FROM reservations WHERE customer_id=(SELECT id FROM users WHERE username='diana') AND check_in='2025-04-15'), 7400000,'2025-04-17 10:00:00');
INSERT INTO payments (reservation_id, amount, method, transaction_id, status, paid_at)
VALUES ((SELECT id FROM reservations WHERE customer_id=(SELECT id FROM users WHERE username='diana') AND check_in='2025-04-15'), 7400000, 'CARD', 'TXD-2025-04', 'SUCCESS', '2025-04-17 10:05:00');

-- Tháng 5: 4 đêm x 6 phòng = 24
INSERT INTO reservations VALUES (NULL,(SELECT id FROM users WHERE username='eric'),'2025-05-20','2025-05-24','CHECKED_OUT',(SELECT id FROM users WHERE username='reception'),NOW(),NULL);
INSERT INTO reservation_rooms (reservation_id, room_id, price_per_night) VALUES
 ((SELECT id FROM reservations WHERE customer_id=(SELECT id FROM users WHERE username='eric') AND check_in='2025-05-20'), (SELECT id FROM rooms WHERE room_number='101'), 800000),
 ((SELECT id FROM reservations WHERE customer_id=(SELECT id FROM users WHERE username='eric') AND check_in='2025-05-20'), (SELECT id FROM rooms WHERE room_number='102'), 800000),
 ((SELECT id FROM reservations WHERE customer_id=(SELECT id FROM users WHERE username='eric') AND check_in='2025-05-20'), (SELECT id FROM rooms WHERE room_number='104'), 800000),
 ((SELECT id FROM reservations WHERE customer_id=(SELECT id FROM users WHERE username='eric') AND check_in='2025-05-20'), (SELECT id FROM rooms WHERE room_number='201'), 1200000),
 ((SELECT id FROM reservations WHERE customer_id=(SELECT id FROM users WHERE username='eric') AND check_in='2025-05-20'), (SELECT id FROM rooms WHERE room_number='203'), 1200000),
 ((SELECT id FROM reservations WHERE customer_id=(SELECT id FROM users WHERE username='eric') AND check_in='2025-05-20'), (SELECT id FROM rooms WHERE room_number='301'), 2200000);
INSERT INTO invoices VALUES (NULL,(SELECT id FROM reservations WHERE customer_id=(SELECT id FROM users WHERE username='eric') AND check_in='2025-05-20'), 15200000,'2025-05-24 10:00:00');
INSERT INTO payments (reservation_id, amount, method, transaction_id, status, paid_at)
VALUES ((SELECT id FROM reservations WHERE customer_id=(SELECT id FROM users WHERE username='eric') AND check_in='2025-05-20'), 15200000, 'WALLET', 'TXE-2025-05', 'SUCCESS', '2025-05-24 10:02:00');

-- Tháng 6: 3 đêm x 5 phòng = 15
INSERT INTO reservations VALUES (NULL,(SELECT id FROM users WHERE username='alice'),'2025-06-10','2025-06-13','CHECKED_OUT',(SELECT id FROM users WHERE username='reception'),NOW(),NULL);
INSERT INTO reservation_rooms (reservation_id, room_id, price_per_night) VALUES
 ((SELECT id FROM reservations WHERE customer_id=(SELECT id FROM users WHERE username='alice') AND check_in='2025-06-10'), (SELECT id FROM rooms WHERE room_number='102'), 800000),
 ((SELECT id FROM reservations WHERE customer_id=(SELECT id FROM users WHERE username='alice') AND check_in='2025-06-10'), (SELECT id FROM rooms WHERE room_number='105'), 800000),
 ((SELECT id FROM reservations WHERE customer_id=(SELECT id FROM users WHERE username='alice') AND check_in='2025-06-10'), (SELECT id FROM rooms WHERE room_number='202'), 1200000),
 ((SELECT id FROM reservations WHERE customer_id=(SELECT id FROM users WHERE username='alice') AND check_in='2025-06-10'), (SELECT id FROM rooms WHERE room_number='203'), 1200000),
 ((SELECT id FROM reservations WHERE customer_id=(SELECT id FROM users WHERE username='alice') AND check_in='2025-06-10'), (SELECT id FROM rooms WHERE room_number='302'), 2200000);
INSERT INTO invoices VALUES (NULL,(SELECT id FROM reservations WHERE customer_id=(SELECT id FROM users WHERE username='alice') AND check_in='2025-06-10'), 8600000,'2025-06-13 10:00:00');
INSERT INTO payments (reservation_id, amount, method, transaction_id, status, paid_at)
VALUES ((SELECT id FROM reservations WHERE customer_id=(SELECT id FROM users WHERE username='alice') AND check_in='2025-06-10'), 8600000, 'CARD', 'TXF-2025-06', 'SUCCESS', '2025-06-13 10:03:00');

-- Tháng 7: 5 đêm x 6 phòng = 30
INSERT INTO reservations VALUES (NULL,(SELECT id FROM users WHERE username='bob'),'2025-07-05','2025-07-10','CHECKED_OUT',(SELECT id FROM users WHERE username='reception'),NOW(),NULL);
INSERT INTO reservation_rooms (reservation_id, room_id, price_per_night) VALUES
 ((SELECT id FROM reservations WHERE customer_id=(SELECT id FROM users WHERE username='bob') AND check_in='2025-07-05'), (SELECT id FROM rooms WHERE room_number='101'), 800000),
 ((SELECT id FROM reservations WHERE customer_id=(SELECT id FROM users WHERE username='bob') AND check_in='2025-07-05'), (SELECT id FROM rooms WHERE room_number='103'), 800000),
 ((SELECT id FROM reservations WHERE customer_id=(SELECT id FROM users WHERE username='bob') AND check_in='2025-07-05'), (SELECT id FROM rooms WHERE room_number='104'), 800000),
 ((SELECT id FROM reservations WHERE customer_id=(SELECT id FROM users WHERE username='bob') AND check_in='2025-07-05'), (SELECT id FROM rooms WHERE room_number='201'), 1200000),
 ((SELECT id FROM reservations WHERE customer_id=(SELECT id FROM users WHERE username='bob') AND check_in='2025-07-05'), (SELECT id FROM rooms WHERE room_number='203'), 1200000),
 ((SELECT id FROM reservations WHERE customer_id=(SELECT id FROM users WHERE username='bob') AND check_in='2025-07-05'), (SELECT id FROM rooms WHERE room_number='302'), 2200000);
INSERT INTO invoices VALUES (NULL,(SELECT id FROM reservations WHERE customer_id=(SELECT id FROM users WHERE username='bob') AND check_in='2025-07-05'), 19000000,'2025-07-10 10:00:00');
INSERT INTO payments (reservation_id, amount, method, transaction_id, status, paid_at)
VALUES ((SELECT id FROM reservations WHERE customer_id=(SELECT id FROM users WHERE username='bob') AND check_in='2025-07-05'), 19000000, 'CARD', 'TXG-2025-07', 'SUCCESS', '2025-07-10 10:05:00');

-- Tháng 8: 3 đêm x 5 phòng = 15
INSERT INTO reservations VALUES (NULL,(SELECT id FROM users WHERE username='charlie'),'2025-08-20','2025-08-23','CHECKED_OUT',(SELECT id FROM users WHERE username='reception'),NOW(),NULL);
INSERT INTO reservation_rooms (reservation_id, room_id, price_per_night) VALUES
 ((SELECT id FROM reservations WHERE customer_id=(SELECT id FROM users WHERE username='charlie') AND check_in='2025-08-20'), (SELECT id FROM rooms WHERE room_number='101'), 800000),
 ((SELECT id FROM reservations WHERE customer_id=(SELECT id FROM users WHERE username='charlie') AND check_in='2025-08-20'), (SELECT id FROM rooms WHERE room_number='102'), 800000),
 ((SELECT id FROM reservations WHERE customer_id=(SELECT id FROM users WHERE username='charlie') AND check_in='2025-08-20'), (SELECT id FROM rooms WHERE room_number='201'), 1200000),
 ((SELECT id FROM reservations WHERE customer_id=(SELECT id FROM users WHERE username='charlie') AND check_in='2025-08-20'), (SELECT id FROM rooms WHERE room_number='203'), 1200000),
 ((SELECT id FROM reservations WHERE customer_id=(SELECT id FROM users WHERE username='charlie') AND check_in='2025-08-20'), (SELECT id FROM rooms WHERE room_number='301'), 2200000);
INSERT INTO invoices VALUES (NULL,(SELECT id FROM reservations WHERE customer_id=(SELECT id FROM users WHERE username='charlie') AND check_in='2025-08-20'), 9000000,'2025-08-23 10:00:00');
INSERT INTO payments (reservation_id, amount, method, transaction_id, status, paid_at)
VALUES ((SELECT id FROM reservations WHERE customer_id=(SELECT id FROM users WHERE username='charlie') AND check_in='2025-08-20'), 9000000, 'CARD', 'TXH-2025-08', 'SUCCESS', '2025-08-23 10:05:00');

-- Tháng 9: 4 đêm x 6 phòng = 24
INSERT INTO reservations VALUES (NULL,(SELECT id FROM users WHERE username='diana'),'2025-09-10','2025-09-14','CHECKED_OUT',(SELECT id FROM users WHERE username='reception'),NOW(),NULL);
INSERT INTO reservation_rooms (reservation_id, room_id, price_per_night) VALUES
 ((SELECT id FROM reservations WHERE customer_id=(SELECT id FROM users WHERE username='diana') AND check_in='2025-09-10'), (SELECT id FROM rooms WHERE room_number='103'), 800000),
 ((SELECT id FROM reservations WHERE customer_id=(SELECT id FROM users WHERE username='diana') AND check_in='2025-09-10'), (SELECT id FROM rooms WHERE room_number='104'), 800000),
 ((SELECT id FROM reservations WHERE customer_id=(SELECT id FROM users WHERE username='diana') AND check_in='2025-09-10'), (SELECT id FROM rooms WHERE room_number='105'), 800000),
 ((SELECT id FROM reservations WHERE customer_id=(SELECT id FROM users WHERE username='diana') AND check_in='2025-09-10'), (SELECT id FROM rooms WHERE room_number='202'), 1200000),
 ((SELECT id FROM reservations WHERE customer_id=(SELECT id FROM users WHERE username='diana') AND check_in='2025-09-10'), (SELECT id FROM rooms WHERE room_number='203'), 1200000),
 ((SELECT id FROM reservations WHERE customer_id=(SELECT id FROM users WHERE username='diana') AND check_in='2025-09-10'), (SELECT id FROM rooms WHERE room_number='301'), 2200000);
INSERT INTO invoices VALUES (NULL,(SELECT id FROM reservations WHERE customer_id=(SELECT id FROM users WHERE username='diana') AND check_in='2025-09-10'), 16000000,'2025-09-14 10:00:00');
INSERT INTO payments (reservation_id, amount, method, transaction_id, status, paid_at)
VALUES ((SELECT id FROM reservations WHERE customer_id=(SELECT id FROM users WHERE username='diana') AND check_in='2025-09-10'), 16000000, 'WALLET', 'TXI-2025-09', 'SUCCESS', '2025-09-14 10:02:00');

-- Tháng 10: 3 đêm x 4 phòng = 12
INSERT INTO reservations VALUES (NULL,(SELECT id FROM users WHERE username='eric'),'2025-10-03','2025-10-06','CHECKED_OUT',(SELECT id FROM users WHERE username='reception'),NOW(),NULL);
INSERT INTO reservation_rooms (reservation_id, room_id, price_per_night) VALUES
 ((SELECT id FROM reservations WHERE customer_id=(SELECT id FROM users WHERE username='eric') AND check_in='2025-10-03'), (SELECT id FROM rooms WHERE room_number='101'), 800000),
 ((SELECT id FROM reservations WHERE customer_id=(SELECT id FROM users WHERE username='eric') AND check_in='2025-10-03'), (SELECT id FROM rooms WHERE room_number='102'), 800000),
 ((SELECT id FROM reservations WHERE customer_id=(SELECT id FROM users WHERE username='eric') AND check_in='2025-10-03'), (SELECT id FROM rooms WHERE room_number='201'), 1200000),
 ((SELECT id FROM reservations WHERE customer_id=(SELECT id FROM users WHERE username='eric') AND check_in='2025-10-03'), (SELECT id FROM rooms WHERE room_number='202'), 1200000);
INSERT INTO invoices VALUES (NULL,(SELECT id FROM reservations WHERE customer_id=(SELECT id FROM users WHERE username='eric') AND check_in='2025-10-03'), 5600000,'2025-10-06 10:00:00');
INSERT INTO payments (reservation_id, amount, method, transaction_id, status, paid_at)
VALUES ((SELECT id FROM reservations WHERE customer_id=(SELECT id FROM users WHERE username='eric') AND check_in='2025-10-03'), 5600000, 'CASH', 'TXJ-2025-10', 'SUCCESS', '2025-10-06 10:04:00');

-- Tháng 11: 2 đêm x 5 phòng = 10
INSERT INTO reservations VALUES (NULL,(SELECT id FROM users WHERE username='alice'),'2025-11-20','2025-11-22','CHECKED_OUT',(SELECT id FROM users WHERE username='reception'),NOW(),NULL);
INSERT INTO reservation_rooms (reservation_id, room_id, price_per_night) VALUES
 ((SELECT id FROM reservations WHERE customer_id=(SELECT id FROM users WHERE username='alice') AND check_in='2025-11-20'), (SELECT id FROM rooms WHERE room_number='103'), 800000),
 ((SELECT id FROM reservations WHERE customer_id=(SELECT id FROM users WHERE username='alice') AND check_in='2025-11-20'), (SELECT id FROM rooms WHERE room_number='104'), 800000),
 ((SELECT id FROM reservations WHERE customer_id=(SELECT id FROM users WHERE username='alice') AND check_in='2025-11-20'), (SELECT id FROM rooms WHERE room_number='105'), 800000),
 ((SELECT id FROM reservations WHERE customer_id=(SELECT id FROM users WHERE username='alice') AND check_in='2025-11-20'), (SELECT id FROM rooms WHERE room_number='201'), 1200000),
 ((SELECT id FROM reservations WHERE customer_id=(SELECT id FROM users WHERE username='alice') AND check_in='2025-11-20'), (SELECT id FROM rooms WHERE room_number='203'), 1200000);
INSERT INTO invoices VALUES (NULL,(SELECT id FROM reservations WHERE customer_id=(SELECT id FROM users WHERE username='alice') AND check_in='2025-11-20'), 7400000,'2025-11-22 10:00:00');
INSERT INTO payments (reservation_id, amount, method, transaction_id, status, paid_at)
VALUES ((SELECT id FROM reservations WHERE customer_id=(SELECT id FROM users WHERE username='alice') AND check_in='2025-11-20'), 7400000, 'CARD', 'TXK-2025-11', 'SUCCESS', '2025-11-22 10:05:00');

-- Tháng 12: 5 đêm x 6 phòng = 30
INSERT INTO reservations VALUES (NULL,(SELECT id FROM users WHERE username='bob'),'2025-12-24','2025-12-29','CHECKED_OUT',(SELECT id FROM users WHERE username='reception'),NOW(),NULL);
INSERT INTO reservation_rooms (reservation_id, room_id, price_per_night) VALUES
 ((SELECT id FROM reservations WHERE customer_id=(SELECT id FROM users WHERE username='bob') AND check_in='2025-12-24'), (SELECT id FROM rooms WHERE room_number='101'), 800000),
 ((SELECT id FROM reservations WHERE customer_id=(SELECT id FROM users WHERE username='bob') AND check_in='2025-12-24'), (SELECT id FROM rooms WHERE room_number='102'), 800000),
 ((SELECT id FROM reservations WHERE customer_id=(SELECT id FROM users WHERE username='bob') AND check_in='2025-12-24'), (SELECT id FROM rooms WHERE room_number='104'), 800000),
 ((SELECT id FROM reservations WHERE customer_id=(SELECT id FROM users WHERE username='bob') AND check_in='2025-12-24'), (SELECT id FROM rooms WHERE room_number='202'), 1200000),
 ((SELECT id FROM reservations WHERE customer_id=(SELECT id FROM users WHERE username='bob') AND check_in='2025-12-24'), (SELECT id FROM rooms WHERE room_number='203'), 1200000),
 ((SELECT id FROM reservations WHERE customer_id=(SELECT id FROM users WHERE username='bob') AND check_in='2025-12-24'), (SELECT id FROM rooms WHERE room_number='302'), 2200000);
INSERT INTO invoices VALUES (NULL,(SELECT id FROM reservations WHERE customer_id=(SELECT id FROM users WHERE username='bob') AND check_in='2025-12-24'), 19000000,'2025-12-29 10:00:00');
INSERT INTO payments (reservation_id, amount, method, transaction_id, status, paid_at)
VALUES ((SELECT id FROM reservations WHERE customer_id=(SELECT id FROM users WHERE username='bob') AND check_in='2025-12-24'), 19000000, 'CARD', 'TXL-2025-12', 'SUCCESS', '2025-12-29 10:05:00');

-- Một vài review
INSERT INTO reviews (reservation_id, rating, comment, visible, created_at) VALUES
 ((SELECT id FROM reservations WHERE check_in='2025-01-18'),5,'Phòng sạch sẽ, nhân viên thân thiện',TRUE,'2025-01-21 13:00:00'),
 ((SELECT id FROM reservations WHERE check_in='2025-02-10'),4,'Vị trí tốt, giá hợp lý',TRUE,'2025-02-14 12:00:00'),
 ((SELECT id FROM reservations WHERE check_in='2025-03-05'),4,'Yên tĩnh, phù hợp công tác',TRUE,'2025-03-08 12:00:00'),
 ((SELECT id FROM reservations WHERE check_in='2025-05-20'),5,'Dịch vụ tuyệt vời',TRUE,'2025-05-24 12:00:00'),
 ((SELECT id FROM reservations WHERE check_in='2025-07-05'),4,'Bữa sáng ngon',TRUE,'2025-07-10 12:00:00'),
 ((SELECT id FROM reservations WHERE check_in='2025-08-20'),5,'Rất hài lòng',TRUE,'2025-08-23 12:00:00'),
 ((SELECT id FROM reservations WHERE check_in='2025-12-24'),5,'Trải nghiệm cuối năm tuyệt vời',TRUE,'2025-12-29 12:00:00');


