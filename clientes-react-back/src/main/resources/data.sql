-- Borra datos previos para evitar duplicados en cada reinicio
DELETE FROM clientes;

-- Inserta registros de prueba
INSERT INTO clientes (nombre, identificacion, email, fecha_registro)
VALUES ('Juan Perez', '12345678', 'juan.perez@example.com', CURRENT_TIMESTAMP);

INSERT INTO clientes (nombre, identificacion, email, fecha_registro)
VALUES ('Maria Lopez', '87654321', 'maria.lopez@example.com', CURRENT_TIMESTAMP);

INSERT INTO clientes (nombre, identificacion, email, fecha_registro)
VALUES ('Prueba Resiliencia', '99999999', 'test@resiliencia.com', CURRENT_TIMESTAMP);