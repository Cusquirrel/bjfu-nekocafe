INSERT INTO users(username, phone, password_hash, role_code) VALUES
('demo_member','13800000001','demo123','CUSTOMER'),
('staff_01','13800000002','demo123','STAFF'),
('catkeeper_01','13800000003','demo123','CAT_KEEPER'),
('ops_admin','13800000004','demo123','OPS_MANAGER')
ON CONFLICT DO NOTHING;
INSERT INTO members(user_id, level_code, points) SELECT id, 'GOLD', 1280 FROM users WHERE username='demo_member' ON CONFLICT DO NOTHING;
INSERT INTO stores(name, city, address, opening_time, closing_time) VALUES
('森林猫咖·学院路店','北京','北京市海淀区学院路 88 号','10:00','21:00'),
('NekoCafé·樱花店','北京','北京市朝阳区樱花路 12 号','09:30','22:00')
ON CONFLICT DO NOTHING;
INSERT INTO dining_tables(store_id, code, capacity, area_type)
SELECT s.id, x.code, x.capacity, x.area_type FROM stores s CROSS JOIN (VALUES
('A01',2,'QUIET'),('A02',4,'NORMAL'),('B01',4,'CAT_INTERACTION'),('C01',6,'CAT_INTERACTION')) AS x(code,capacity,area_type)
ON CONFLICT DO NOTHING;
INSERT INTO cats(store_id, name, breed, age_months, personality, interaction_status, health_status, weight_kg)
SELECT id, '年糕', '布偶', 18, '亲人、安静，适合初次到店顾客', 'AVAILABLE', 'NORMAL', 4.80 FROM stores WHERE name LIKE '%学院路%'
ON CONFLICT DO NOTHING;
INSERT INTO cats(store_id, name, breed, age_months, personality, interaction_status, health_status, weight_kg)
SELECT id, '豆沙', '英短', 30, '活泼，喜欢逗猫棒', 'RESTING', 'NORMAL', 5.20 FROM stores WHERE name LIKE '%学院路%'
ON CONFLICT DO NOTHING;
INSERT INTO cats(store_id, name, breed, age_months, personality, interaction_status, health_status, weight_kg)
SELECT id, '栗子', '缅因', 40, '体型较大，互动需管家确认', 'AVAILABLE', 'WATCH', 7.10 FROM stores WHERE name LIKE '%樱花%'
ON CONFLICT DO NOTHING;
