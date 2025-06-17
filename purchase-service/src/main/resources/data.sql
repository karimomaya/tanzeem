INSERT INTO business_types (name, code, tenant_id, is_active, is_deleted)
VALUES
  ('تجارة جملة', 'WHOLESALE', 'tanzeem', true, false),
  ('تجارة تجزئة', 'RETAIL', 'tanzeem', true, false),
  ('مقاولات', 'CONSTRUCTION', 'tanzeem', true, false),
  ('خدمات', 'SERVICES', 'tanzeem', true, false),
  ('تصنيع', 'MANUFACTURING', 'tanzeem', true, false),
  ('استيراد وتصدير', 'IMPORT_EXPORT', 'tanzeem', true, false),
  ('أخرى', 'OTHER', 'tanzeem', true, false);

INSERT INTO payment_terms (name, code, tenant_id, is_active, is_deleted)
VALUES   ( 'نقداً', 'CASH', 'tanzeem', true, false),
( 'أجل 30 يوم', 'NET_30', 'tanzeem', true, false),
( 'أجل 60 يوم', 'NET_60', 'tanzeem', true, false);

INSERT INTO suppliers ( name, code, contact_person, phone, email, address, is_active, tenant_id, country_code, governorate_code, business_type, payment_term, rating, created_at, updated_at, created_by, updated_by)
VALUES
( 'شركة الأمل','SUP001', 'أحمد علي', '01000000001', 'supplier1@example.com', 'شارع النصر', true, 'tanzeem', 'EG','ALEX','WHOLESALE', 'NET_30', 10,  CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'admin', 'admin'),
( 'شركة الفجر','SUP002', 'سعيد حسن', '01000000002', 'supplier2@example.com', 'شارع التحرير', true, 'tanzeem', 'EG','CAIRO','WHOLESALE', 'NET_30', 10,  CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'admin', 'admin'),
( 'شركة النور','SUP003', 'علي إبراهيم', '01000000003', 'supplier3@example.com', 'شارع الأزهر', true, 'tanzeem', 'EG','CAIRO','WHOLESALE', 'NET_30', 10,  CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'admin', 'admin'),
( 'شركة الرضا','SUP004', 'محمد جاد', '01000000004', 'supplier4@example.com', 'شارع الهرم', true, 'tanzeem', 'EG','GIZA','WHOLESALE', 'NET_30', 10,  CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'admin', 'admin'),
( 'شركة الرحمة','SUP005', 'كريم ناصر', '01000000005', 'supplier5@example.com', 'شارع فيصل', true, 'tanzeem', 'EG','GIZA','WHOLESALE', 'NET_30', 10,  CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'admin', 'admin'),
( 'شركة التوحيد','SUP006', 'خالد مصطفى', '01000000006', 'supplier6@example.com', 'شارع عباس العقاد', true, 'tanzeem', 'EG','ALEX','WHOLESALE', 'NET_30', 10,  CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'admin', 'admin'),
( 'شركة الرخاء','SUP007', 'ياسر فهمي', '01000000007', 'supplier7@example.com', 'شارع النيل', true, 'tanzeem', 'EG','CAIRO','WHOLESALE', 'NET_30', 10,  CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'admin', 'admin'),
( 'شركة السلام','SUP008', 'محمود عزت', '01000000008', 'supplier8@example.com', 'شارع الجامعة', true, 'tanzeem', 'EG','CAIRO','WHOLESALE', 'NET_30', 10,  CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'admin', 'admin'),
( 'شركة البركة','SUP009', 'سامي فؤاد', '01000000009', 'supplier9@example.com', 'شارع القصر', true, 'tanzeem', 'SA','RYAD','WHOLESALE', 'NET_30', 10,  CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'admin', 'admin'),
( 'شركة الأمانة','SUP010', 'رامي يحيى', '01000000010', 'supplier10@example.com', 'شارع الجمهورية', true, 'tanzeem', 'SA','RYAD','WHOLESALE', 'NET_30', 10,  CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'admin', 'admin'),
( 'شركة الإخلاص','SUP0011', 'وليد عبد الرحمن', '01000000011', 'supplier11@example.com', 'شارع البحر الأعظم', true, 'tanzeem', 'EG','ALEX','WHOLESALE', 'NET_30', 10,  CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'admin', 'admin'),
( 'شركة الصفا','SUP0012', 'عادل حمزة', '01000000012', 'supplier12@example.com', 'شارع الجيش', true, 'tanzeem', 'SA','MAKA','WHOLESALE', 'NET_30', 10,  CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'admin', 'admin'),
( 'شركة الطيب','SUP0013', 'فتحي عمر', '01000000013', 'supplier13@example.com', 'شارع رمسيس', true, 'tanzeem', 'SA','MAKA','WHOLESALE', 'NET_30', 10,  CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'admin', 'admin'),
( 'شركة اليسر','SUP0014', 'شريف سامي', '01000000014', 'supplier14@example.com', 'شارع قصر النيل', true, 'tanzeem', 'AE','DUBAI','WHOLESALE', 'NET_30', 10,  CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'admin', 'admin'),
( 'شركة الأمان','SUP0015', 'طارق سليم', '01000000015', 'supplier15@example.com', 'شارع السودان', true, 'tanzeem', 'AE','DUBAI','WHOLESALE', 'NET_30', 10,  CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'admin', 'admin'),
( 'شركة البرنس','SUP0016', 'أيمن لطفي', '01000000016', 'supplier16@example.com', 'شارع النصر', true, 'tanzeem', 'AE','ABUDHABI','WHOLESALE', 'NET_30', 10,  CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'admin', 'admin'),
( 'شركة المدينة','SUP0017', 'حسن نجم', '01000000017', 'supplier17@example.com', 'شارع التحرير', true, 'tanzeem', 'EG','ALEX','WHOLESALE', 'NET_30', 10,  CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'admin', 'admin'),
( 'شركة الجود','SUP0018', 'منير صبحي', '01000000018', 'supplier18@example.com', 'شارع الجيش', true, 'tanzeem', 'EG','ALEX','WHOLESALE', 'NET_30', 10,  CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'admin', 'admin'),
( 'شركة العرب','SUP0019', 'باسم سمير', '01000000019', 'supplier19@example.com', 'شارع العروبة', true, 'tanzeem', 'EG','ALEX','WHOLESALE', 'NET_30', 10,  CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'admin', 'admin'),
( 'شركة التميز','SUP0020', 'نادر مراد', '01000000020', 'supplier20@example.com', 'شارع المقطم', true, 'tanzeem', 'EG','ALEX','WHOLESALE', 'NET_30', 10,  CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'admin', 'admin');
INSERT INTO purchases ( purchase_date, supplier_id, invoice_number, total_amount, notes, tenant_id, status, created_at, updated_at, created_by, updated_by)
VALUES
( '2024-01-01', 1, 'INV-001', 1000.00, 'شراء فاتورة أولى', 'tanzeem', 'DELIVERED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'admin', 'admin'),
( '2024-01-02', 2, 'INV-002', 850.00, 'شراء فاتورة ثانية', 'tanzeem', 'PENDING', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'admin', 'admin'),
( '2024-01-03', 3, 'INV-003', 500.00, 'شراء فاتورة ثالثة', 'tanzeem', 'CANCELLED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'admin', 'admin'),
( '2024-01-04', 4, 'INV-004', 600.00, 'شراء خامس', 'tanzeem', 'DELIVERED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'admin', 'admin'),
( '2024-01-05', 5, 'INV-005', 920.00, 'شراء سادس', 'tanzeem', 'PENDING', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'admin', 'admin'),
( '2024-01-06', 6, 'INV-006', 100.00, 'شراء سابع', 'tanzeem', 'DELIVERED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'admin', 'admin'),
( '2024-01-07', 7, 'INV-007', 1150.00, 'شراء ثامن', 'tanzeem', 'CANCELLED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'admin', 'admin'),
( '2024-01-08', 8, 'INV-008', 730.00, 'شراء تاسع', 'tanzeem', 'DELIVERED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'admin', 'admin'),
( '2024-01-09', 9, 'INV-009', 660.00, 'شراء عاشر', 'tanzeem', 'PENDING', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'admin', 'admin'),
( '2024-01-10', 10, 'INV-010', 200.00, 'شراء إضافي', 'tanzeem', 'DELIVERED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'admin', 'admin'),
( '2024-01-11', 11, 'INV-011', 1240.00, 'شراء عادي', 'tanzeem', 'DELIVERED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'admin', 'admin'),
( '2024-01-12', 12, 'INV-012', 990.00, 'طلب سريع', 'tanzeem', 'DELIVERED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'admin', 'admin'),
( '2024-01-13', 13, 'INV-013', 115.00, 'طلب تجريبي', 'tanzeem', 'PENDING', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'admin', 'admin'),
( '2024-01-14', 14, 'INV-014', 1800.00, 'شراء خاص', 'tanzeem', 'DELIVERED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'admin', 'admin'),
( '2024-01-15', 15, 'INV-015', 500.00, 'شراء داخلي', 'tanzeem', 'CANCELLED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'admin', 'admin'),
( '2024-01-16', 16, 'INV-016', 750.00, 'شراء خارجي', 'tanzeem', 'DELIVERED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'admin', 'admin'),
( '2024-01-17', 17, 'INV-017', 420.00, 'تجربة', 'tanzeem', 'PENDING', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'admin', 'admin'),
( '2024-01-18', 18, 'INV-018', 1320.00, 'فاتورة مرتجعة', 'tanzeem', 'DELIVERED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'admin', 'admin'),
( '2024-01-19', 19, 'INV-019', 560.00, 'طلب تخفيض', 'tanzeem', 'DELIVERED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'admin', 'admin'),
( '2024-01-20', 20, 'INV-020', 690.00, 'شراء جزئي', 'tanzeem', 'PENDING', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'admin', 'admin');
INSERT INTO purchase_items ( product_id, product_name_at_purchase, quantity, unit_price, total_price, purchase_id, created_at, updated_at, created_by, updated_by)
VALUES
( 1, 'منتج أ', 2, 200.00, 400.00, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'admin', 'admin'),
( 2, 'منتج ب', 3, 150.00, 450.00, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'admin', 'admin'),
( 3, 'منتج ج', 1, 100.00, 100.00, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'admin', 'admin'),
( 4, 'منتج د', 5, 180.00, 900.00, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'admin', 'admin'),
( 5, 'منتج هـ', 2, 250.00, 500.00, 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'admin', 'admin'),
( 6, 'منتج و', 4, 300.00, 1200.00, 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'admin', 'admin');

