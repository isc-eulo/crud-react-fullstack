-- DML para insertar datos de prueba
-- -----------------------------------

-- INSERCIÓN DE CATEGORÍAS
INSERT INTO categoria (nombre) VALUES ('Electrónica'); -- ID 1
INSERT INTO categoria (nombre) VALUES ('Limpieza');    -- ID 2
INSERT INTO categoria (nombre) VALUES ('Oficina');     -- ID 3

-- INSERCIÓN DE PRODUCTOS
-- Producto 1: Categoría 'Electrónica' (ID 1)
INSERT INTO producto (nombre, categoria_id, precio_unitario, stock)
VALUES ('Smartphone X10', 1, 750.00, 50);

-- Producto 2: Categoría 'Limpieza' (ID 2)
INSERT INTO producto (nombre, categoria_id, precio_unitario, stock)
VALUES ('Detergente Líquido', 2, 15.50, 200);

-- Producto 3: Categoría 'Electrónica' (ID 1)
INSERT INTO producto (nombre, categoria_id, precio_unitario, stock)
VALUES ('Auriculares Bluetooth', 1, 89.99, 120);

-- Producto 4: Categoría 'Oficina' (ID 3)
INSERT INTO producto (nombre, categoria_id, precio_unitario, stock)
VALUES ('Paquete de Hojas A4', 3, 5.00, 500);