insert into laundry_item_tb(name, category, base_price, description, created_at) values
                                                                                     ('셔츠', 'TOP', 3000, '일상적으로 착용하는 일반 셔츠 세탁입니다.', now()),
                                                                                     ('바지', 'BOTTOM', 4000, '면바지, 슬랙스 등 일반 바지 세탁입니다.', now()),
                                                                                     ('코트', 'OUTER', 8000, '겨울용 코트 및 두꺼운 아우터 세탁입니다.', now()),
                                                                                     ('이불', 'BEDDING', 12000, '이불, 담요 등 침구류 세탁 서비스입니다.', now()),
                                                                                     ('모자', 'ACCESSORY', 2000, '캡모자, 비니 등 모자류 세탁입니다.', now()),
                                                                                     ('드라이클리닝', 'SPECIAL', 20000, '드라이클리닝이 필요한 의류 전용 세탁입니다.', now()),
                                                                                     ('기타', 'ETC', 5000, '분류되지 않은 기타 세탁물 항목입니다.', now());

insert into laundry_option_tb(name, extra_price, description, is_active, created_at) values
         ('표백', 2000, '표백 처리를 통해 얼룩과 변색을 개선합니다.', true, now()),
         ('향 추가', 1000, '세탁 후 은은한 향을 추가해 드립니다.', true, now()),
         ('프리미엄 세제', 6000, '프리미엄 세제를 사용해 섬유 손상을 줄입니다.', true, now()),
         ('얼룩 집중 제거', 4000, '목, 소매 등 심한 얼룩을 집중적으로 제거합니다.', false, now()),
         ('저자극 세탁', 3000, '피부 자극을 줄인 저자극 세탁 옵션입니다.', false, now());

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

INSERT INTO customer_tb (name, birth, phone, grade, memo, created_at) VALUES
    ('김민수', '1990-03-15', '010-1234-5678', 'NEW', '첫 방문 고객', '2025-01-15'),
    ('이서연', '1985-07-22', '010-2345-6789', 'REGULAR', '월 2회 이용', '2025-03-30'),
    ('박지훈', '1978-11-05', '010-3456-7890', 'VIP', '장기 고객, 프리미엄 서비스 선호', '2025-07-06'),
    ('최은지', '1995-01-30', '010-4567-8901', 'REGULAR', '알레르기 주의', '2025-10-15'),
    ('정우성', '1988-09-12', '010-5678-9012', 'NEW', null, '2025-12-03');

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
