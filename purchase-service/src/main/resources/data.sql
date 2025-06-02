INSERT INTO suppliers (id, name, code, contact_person, phone, email, address, is_active, tenant_id, created_at, updated_at, created_by, updated_by)
VALUES
(1, 'شركة الأمل','SUP001', 'أحمد علي', '01000000001', 'supplier1@example.com', 'شارع النصر', true, 'tanzeem', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'admin', 'admin'),
(2, 'شركة الفجر','SUP002', 'سعيد حسن', '01000000002', 'supplier2@example.com', 'شارع التحرير', true, 'tanzeem', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'admin', 'admin'),
(3, 'شركة النور','SUP003', 'علي إبراهيم', '01000000003', 'supplier3@example.com', 'شارع الأزهر', true, 'tanzeem', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'admin', 'admin'),
(4, 'شركة الرضا','SUP004', 'محمد جاد', '01000000004', 'supplier4@example.com', 'شارع الهرم', true, 'tanzeem', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'admin', 'admin'),
(5, 'شركة الرحمة','SUP005', 'كريم ناصر', '01000000005', 'supplier5@example.com', 'شارع فيصل', true, 'tanzeem', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'admin', 'admin'),
(6, 'شركة التوحيد','SUP006', 'خالد مصطفى', '01000000006', 'supplier6@example.com', 'شارع عباس العقاد', true, 'tanzeem', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'admin', 'admin'),
(7, 'شركة الرخاء','SUP007', 'ياسر فهمي', '01000000007', 'supplier7@example.com', 'شارع النيل', true, 'tanzeem', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'admin', 'admin'),
(8, 'شركة السلام','SUP008', 'محمود عزت', '01000000008', 'supplier8@example.com', 'شارع الجامعة', true, 'tanzeem', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'admin', 'admin'),
(9, 'شركة البركة','SUP009', 'سامي فؤاد', '01000000009', 'supplier9@example.com', 'شارع القصر', true, 'tanzeem', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'admin', 'admin'),
(10, 'شركة الأمانة','SUP010', 'رامي يحيى', '01000000010', 'supplier10@example.com', 'شارع الجمهورية', true, 'tanzeem', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'admin', 'admin'),
(11, 'شركة الإخلاص','SUP0011', 'وليد عبد الرحمن', '01000000011', 'supplier11@example.com', 'شارع البحر الأعظم', true, 'tanzeem', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'admin', 'admin'),
(12, 'شركة الصفا','SUP0012', 'عادل حمزة', '01000000012', 'supplier12@example.com', 'شارع الجيش', true, 'tanzeem', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'admin', 'admin'),
(13, 'شركة الطيب','SUP0013', 'فتحي عمر', '01000000013', 'supplier13@example.com', 'شارع رمسيس', true, 'tanzeem', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'admin', 'admin'),
(14, 'شركة اليسر','SUP0014', 'شريف سامي', '01000000014', 'supplier14@example.com', 'شارع قصر النيل', true, 'tanzeem', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'admin', 'admin'),
(15, 'شركة الأمان','SUP0015', 'طارق سليم', '01000000015', 'supplier15@example.com', 'شارع السودان', true, 'tanzeem', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'admin', 'admin'),
(16, 'شركة البرنس','SUP0016', 'أيمن لطفي', '01000000016', 'supplier16@example.com', 'شارع النصر', true, 'tanzeem', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'admin', 'admin'),
(17, 'شركة المدينة','SUP0017', 'حسن نجم', '01000000017', 'supplier17@example.com', 'شارع التحرير', true, 'tanzeem', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'admin', 'admin'),
(18, 'شركة الجود','SUP0018', 'منير صبحي', '01000000018', 'supplier18@example.com', 'شارع الجيش', true, 'tanzeem', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'admin', 'admin'),
(19, 'شركة العرب','SUP0019', 'باسم سمير', '01000000019', 'supplier19@example.com', 'شارع العروبة', true, 'tanzeem', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'admin', 'admin'),
(20, 'شركة التميز','SUP0020', 'نادر مراد', '01000000020', 'supplier20@example.com', 'شارع المقطم', true, 'tanzeem', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'admin', 'admin');
INSERT INTO purchases (id, purchase_date, supplier_id, invoice_number, total_amount, notes, tenant_id, status, created_at, updated_at, created_by, updated_by)
VALUES
(1, '2024-01-01', 1, 'INV-001', 1000.00, 'شراء فاتورة أولى', 'tanzeem', 'COMPLETED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'admin', 'admin'),
(2, '2024-01-02', 2, 'INV-002', 850.00, 'شراء فاتورة ثانية', 'tanzeem', 'PENDING', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'admin', 'admin'),
(3, '2024-01-03', 3, 'INV-003', 500.00, 'شراء فاتورة ثالثة', 'tanzeem', 'CANCELLED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'admin', 'admin'),
(4, '2024-01-04', 4, 'INV-004', 600.00, 'شراء خامس', 'tanzeem', 'COMPLETED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'admin', 'admin'),
(5, '2024-01-05', 5, 'INV-005', 920.00, 'شراء سادس', 'tanzeem', 'PENDING', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'admin', 'admin'),
(6, '2024-01-06', 6, 'INV-006', 100.00, 'شراء سابع', 'tanzeem', 'COMPLETED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'admin', 'admin'),
(7, '2024-01-07', 7, 'INV-007', 1150.00, 'شراء ثامن', 'tanzeem', 'CANCELLED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'admin', 'admin'),
(8, '2024-01-08', 8, 'INV-008', 730.00, 'شراء تاسع', 'tanzeem', 'COMPLETED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'admin', 'admin'),
(9, '2024-01-09', 9, 'INV-009', 660.00, 'شراء عاشر', 'tanzeem', 'PENDING', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'admin', 'admin'),
(10, '2024-01-10', 10, 'INV-010', 200.00, 'شراء إضافي', 'tanzeem', 'COMPLETED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'admin', 'admin'),
(11, '2024-01-11', 11, 'INV-011', 1240.00, 'شراء عادي', 'tanzeem', 'COMPLETED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'admin', 'admin'),
(12, '2024-01-12', 12, 'INV-012', 990.00, 'طلب سريع', 'tanzeem', 'COMPLETED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'admin', 'admin'),
(13, '2024-01-13', 13, 'INV-013', 115.00, 'طلب تجريبي', 'tanzeem', 'PENDING', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'admin', 'admin'),
(14, '2024-01-14', 14, 'INV-014', 1800.00, 'شراء خاص', 'tanzeem', 'COMPLETED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'admin', 'admin'),
(15, '2024-01-15', 15, 'INV-015', 500.00, 'شراء داخلي', 'tanzeem', 'CANCELLED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'admin', 'admin'),
(16, '2024-01-16', 16, 'INV-016', 750.00, 'شراء خارجي', 'tanzeem', 'COMPLETED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'admin', 'admin'),
(17, '2024-01-17', 17, 'INV-017', 420.00, 'تجربة', 'tanzeem', 'PENDING', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'admin', 'admin'),
(18, '2024-01-18', 18, 'INV-018', 1320.00, 'فاتورة مرتجعة', 'tanzeem', 'COMPLETED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'admin', 'admin'),
(19, '2024-01-19', 19, 'INV-019', 560.00, 'طلب تخفيض', 'tanzeem', 'COMPLETED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'admin', 'admin'),
(20, '2024-01-20', 20, 'INV-020', 690.00, 'شراء جزئي', 'tanzeem', 'PENDING', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'admin', 'admin');
INSERT INTO purchase_items (id, product_id, product_name, quantity, unit_price, total_price, purchase_id, created_at, updated_at, created_by, updated_by)
VALUES
(1, 101, 'منتج أ', 2, 200.00, 400.00, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'admin', 'admin'),
(2, 102, 'منتج ب', 3, 150.00, 450.00, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'admin', 'admin'),
(3, 103, 'منتج ج', 1, 100.00, 100.00, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'admin', 'admin'),
(4, 104, 'منتج د', 5, 180.00, 900.00, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'admin', 'admin'),
(5, 105, 'منتج هـ', 2, 250.00, 500.00, 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'admin', 'admin'),
(6, 106, 'منتج و', 4, 300.00, 1200.00, 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'admin', 'admin');

INSERT INTO business_types (name, code, tenant_id, is_active, is_deleted)
VALUES
  ('تجارة جملة', 'WHOLESALE', 'tanzeem', true, false),
  ('تجارة تجزئة', 'RETAIL', 'tanzeem', true, false),
  ('مقاولات', 'CONSTRUCTION', 'tanzeem', true, false),
  ('خدمات', 'SERVICES', 'tanzeem', true, false),
  ('تصنيع', 'MANUFACTURING', 'tanzeem', true, false),
  ('استيراد وتصدير', 'IMPORT_EXPORT', 'tanzeem', true, false),
  ('أخرى', 'OTHER', 'tanzeem', true, false);