-- Countries
INSERT INTO countries (id, name, code, is_active)
VALUES
  (1, 'مصر', 'EG', true),
  (2, 'السعودية', 'SA', true),
  (3, 'الإمارات', 'AE', true);

-- Governorates for Egypt
INSERT INTO governorates (id, name, code, is_active, country_id)
VALUES
  (1, 'القاهرة', 'CAIRO', true, 1),
  (2, 'الإسكندرية', 'ALEX', true, 1),
  (3, 'الجيزة', 'GIZA', true, 1);

-- Governorates for Saudi Arabia
INSERT INTO governorates (id, name, code, is_active, country_id)
VALUES
  (4, 'الرياض', 'RYAD', true, 2),
  (5, 'جدة', 'GADA', true, 2),
  (6, 'مكة', 'MAKA', true, 2);

-- Governorates for UAE
INSERT INTO governorates (id, name, code, is_active, country_id)
VALUES
  (7, 'دبي', 'DUBAI', true, 3),
  (8, 'أبوظبي', 'ABUDHABI', true, 3),
  (9, 'الشارقة', 'SHARJA', true, 3);