-- Countries
INSERT INTO countries (id, name, code, is_active)
VALUES
  (1, 'مصر', 'EG', true),
  (2, 'السعودية', 'SA', true),
  (3, 'الإمارات', 'AE', true);

-- Governorates for Egypt
INSERT INTO governorates (id, name, is_active, country_id)
VALUES
  (1, 'القاهرة', true, 1),
  (2, 'الإسكندرية', true, 1),
  (3, 'الجيزة', true, 1);

-- Governorates for Saudi Arabia
INSERT INTO governorates (id, name, is_active, country_id)
VALUES
  (4, 'الرياض', true, 2),
  (5, 'جدة', true, 2),
  (6, 'مكة', true, 2);

-- Governorates for UAE
INSERT INTO governorates (id, name, is_active, country_id)
VALUES
  (7, 'دبي', true, 3),
  (8, 'أبوظبي', true, 3),
  (9, 'الشارقة', true, 3);