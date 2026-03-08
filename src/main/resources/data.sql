INSERT INTO reminder_lists (name, color, icon, created_at, updated_at) VALUES
    ('개인', '#007AFF', 'person', NOW(), NOW()),
    ('업무', '#FF3B30', 'briefcase', NOW(), NOW()),
    ('쇼핑', '#34C759', 'cart', NOW(), NOW());

INSERT INTO reminders (title, note, is_done, priority, list_id, created_at, updated_at) VALUES
    ('장보기', '우유, 계란, 빵', false, 'NONE', 3, NOW(), NOW()),
    ('운동하기', '30분 러닝', false, 'NONE', 1, NOW(), NOW()),
    ('독서', '클린 코드 3장', false, 'LOW', 1, NOW(), NOW()),
    ('이메일 확인', NULL, true, 'NONE', 2, NOW(), NOW()),
    ('회의 준비', '자료 정리 및 발표 연습', false, 'HIGH', 2, NOW(), NOW());
