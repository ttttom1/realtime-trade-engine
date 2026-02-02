-- user01: 1억 원 있음, 주식 없음
INSERT INTO members (id, name, balance, samsung_stock_quantity)
VALUES ('user01', '개미왕', 100000000.00, 0);

-- user02: 돈 없음, 주식 100주 있음
INSERT INTO members (id, name, balance, samsung_stock_quantity)
VALUES ('user02', '고래', 0.00, 100);


-- 정산 결과 확인 쿼리
SELECT id, name, balance, samsung_stock_quantity
FROM members
WHERE id IN ('user01', 'user02');

-- 체결 내역 확인 쿼리
SELECT * FROM trades ORDER BY created_at DESC;