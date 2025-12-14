insert into laundry_item_tb(name, category, base_price) values
                                                           ('셔츠', 'TOP', 3000),
                                                           ('바지', 'BOTTOM', 4000),
                                                           ('코트', 'OUTER', 8000),
                                                           ('이불', 'BEDDING', 12000),
                                                           ('모자', 'ACCESSORY', 2000),
                                                           ('드라이클리닝', 'SPECIAL', 20000),
                                                           ('기타', 'ETC', 5000);

insert into laundry_option_tb(name, extra_price) values
                                                     ('표백', 2000),
                                                     ('향 추가', 1000),
                                                     ('프리미엄 세제', 6000);

insert into supply_item_tb(name, unit, stock_quantity, safety_stock) values
                                                           ('세탁세제', 'ml', 5, 10),
                                                           ('섬유유연제', 'ml', 30, 10),
                                                           ('표백제', 'ml', 5, 3),
                                                           ('비닐포장지', 'ea', 100, 100),
                                                           ('종이포장봉투', 'ea', 500, 100),
                                                           ('옷걸이', 'ea', 300, 100),
                                                           ('세탁망', 'ea', 150, 100);

INSERT INTO employee_tb (name, username, password, email, user_role) VALUES
    ('김관리', 'admin01', '$2a$10$adminhashedpassword', 'admin01@clean4u.com', 'ADMIN'),
    ('이직원', 'employee01', '$2a$10$employeehashedpassword1', 'employee01@clean4u.com', 'EMPLOYEE'),
    ('박직원', 'employee02', '$2a$10$employeehashedpassword2', 'employee02@clean4u.com', 'EMPLOYEE');

INSERT INTO customer_tb (name, birth, phone, grade, memo) VALUES
    ('김민수', '1990-03-15', '010-1234-5678', 'NEW', '첫 방문 고객'),
    ('이서연', '1985-07-22', '010-2345-6789', 'REGULAR', '월 2회 이용'),
    ('박지훈', '1978-11-05', '010-3456-7890', 'VIP', '장기 고객, 프리미엄 서비스 선호'),
    ('최은지', '1995-01-30', '010-4567-8901', 'REGULAR', '알레르기 주의'),
    ('정우성', '1988-09-12', '010-5678-9012', 'NEW', NULL);

INSERT INTO order_tb (customer_id, status, total_price, order_date, memo, editor_id) VALUES
    (1, 'RECEIVED',   15000, '2025-12-01', '일반 세탁', 1),
    (2, 'WASHING',    32000, '2025-12-02', '이불 포함', 2),
    (3, 'DRYING',     45000, '2025-12-03', NULL, 1),
    (1, 'COMPLETED',  18000, '2025-12-04', '재주문', 3),
    (4, 'RECEIVED',   22000, '2025-12-05', '얼룩 제거 요청', 2),
    (5, 'WASHING',    60000, '2025-12-06', '대량 세탁', 1),
    (2, 'DRYING',     38000, '2025-12-07', NULL, 3),
    (3, 'COMPLETED',  27000, '2025-12-08', '빠른 처리 감사', 2),
    (4, 'RECEIVED',   19000, '2025-12-09', NULL, 1),
    (5, 'COMPLETED',  52000, '2025-12-10', '정기 고객', 3);
