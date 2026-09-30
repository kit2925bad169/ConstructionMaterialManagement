CREATE DATABASE IF NOT EXISTS construction_material_db;
USE construction_material_db;

CREATE TABLE IF NOT EXISTS materials (
    material_id INT PRIMARY KEY AUTO_INCREMENT,
    material_name VARCHAR(100) NOT NULL,
    category VARCHAR(60) NOT NULL,
    unit VARCHAR(20) NOT NULL,
    quantity DECIMAL(10,2) NOT NULL DEFAULT 0,
    minimum_stock DECIMAL(10,2) NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS suppliers (
    supplier_id INT PRIMARY KEY AUTO_INCREMENT,
    supplier_name VARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    email VARCHAR(100),
    address VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS purchases (
    purchase_id INT PRIMARY KEY AUTO_INCREMENT,
    material_id INT NOT NULL,
    supplier_id INT NOT NULL,
    quantity DECIMAL(10,2) NOT NULL,
    unit_price DECIMAL(10,2) NOT NULL,
    total_amount DECIMAL(12,2) NOT NULL,
    purchase_date DATE NOT NULL,
    FOREIGN KEY (material_id) REFERENCES materials(material_id),
    FOREIGN KEY (supplier_id) REFERENCES suppliers(supplier_id)
);

CREATE TABLE IF NOT EXISTS material_usage (
    usage_id INT PRIMARY KEY AUTO_INCREMENT,
    material_id INT NOT NULL,
    quantity_used DECIMAL(10,2) NOT NULL,
    project_name VARCHAR(120) NOT NULL,
    usage_date DATE NOT NULL,
    FOREIGN KEY (material_id) REFERENCES materials(material_id)
);

INSERT INTO materials (material_name, category, unit, quantity, minimum_stock)
SELECT 'Cement', 'Building Material', 'bags', 0, 20
WHERE NOT EXISTS (SELECT 1 FROM materials WHERE material_name='Cement');

INSERT INTO materials (material_name, category, unit, quantity, minimum_stock)
SELECT 'Steel Rod', 'Metal', 'kg', 0, 100
WHERE NOT EXISTS (SELECT 1 FROM materials WHERE material_name='Steel Rod');

INSERT INTO materials (material_name, category, unit, quantity, minimum_stock)
SELECT 'Bricks', 'Building Material', 'pieces', 0, 500
WHERE NOT EXISTS (SELECT 1 FROM materials WHERE material_name='Bricks');
