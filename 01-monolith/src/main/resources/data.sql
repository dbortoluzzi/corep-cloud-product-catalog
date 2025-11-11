-- Sample data initialization using SQL
-- This file is automatically executed by Spring Boot if:
-- - spring.sql.init.mode is set to 'always' or 'embedded' (default for H2)
-- - spring.jpa.hibernate.ddl-auto is set to 'create' or 'create-drop'
--
-- Note: This approach is simpler but doesn't use business logic
-- For data that requires validation or service logic, use DataInitializer (Java)

-- Insert products
INSERT INTO products (name, description, price, category, created_at, updated_at) VALUES
('Laptop Dell XPS 15', 'High-performance laptop with 4K display', 1299.99, 'Electronics', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('iPhone 15 Pro', 'Latest iPhone with A17 Pro chip', 999.00, 'Electronics', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Spring in Action', 'Comprehensive guide to Spring Framework', 49.99, 'Books', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Wireless Mouse', 'Ergonomic wireless mouse', 29.99, 'Accessories', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Mechanical Keyboard', 'RGB mechanical keyboard with Cherry MX switches', 149.99, 'Accessories', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Monitor 27" 4K', 'Ultra HD 4K monitor with HDR', 399.99, 'Electronics', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Note: Inventory records should be created via InventoryService to ensure validation
-- If you want to use SQL, you would need to:
-- 1. Get the product IDs (requires subquery or manual IDs)
-- 2. Insert into inventory table
-- But this bypasses business logic validation!

-- Example (commented out - not recommended):
-- INSERT INTO inventory (product_id, stock_quantity, reserved_quantity, last_updated)
-- SELECT id, 25, 0, CURRENT_TIMESTAMP FROM products WHERE name = 'Laptop Dell XPS 15';

