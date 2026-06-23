-- Default user
-- password: admin123
INSERT INTO users (username, password, role)
VALUES
('admin', '$2a$10$Tgf2dpoh.amZ4uTWesUj2OGWQa2L8sDfyHJ8DupLri3DnvHhlUedO', 'ADMIN');

-- Master barang
INSERT INTO products (sku, product_name, purchase_price, selling_price, description)
VALUES
('BRG-001', 'Indomie Goreng', 2500, 3500, 'Mie instan goreng'),
('BRG-002', 'Aqua 600ml', 3000, 4500, 'Air mineral botol 600ml'),
('BRG-003', 'Teh Botol Sosro', 3500, 5000, 'Minuman teh botol'),
('BRG-004', 'Kopi Kapal Api', 1200, 2000, 'Kopi sachet'),
('BRG-005', 'Biskuit Roma', 6000, 8500, 'Biskuit kemasan'),
('BRG-006', 'Susu Ultra 250ml', 5000, 7500, 'Susu UHT kemasan kecil'),
('BRG-007', 'Chitato 68g', 8000, 12000, 'Snack kentang kemasan'),
('BRG-008', 'Roti Tawar', 11000, 15000, 'Roti tawar kemasan'),
('BRG-009', 'Sabun Lifebuoy', 3500, 6000, 'Sabun mandi batang'),
('BRG-010', 'Shampoo Sunsilk', 9000, 13500, 'Shampoo botol kecil');

-- Stok awal
INSERT INTO stocks (product_id, quantity)
SELECT id, 100
FROM products
WHERE sku IN (
    'BRG-001',
    'BRG-002',
    'BRG-003',
    'BRG-004',
    'BRG-005',
    'BRG-006',
    'BRG-007',
    'BRG-008',
    'BRG-009',
    'BRG-010'
);