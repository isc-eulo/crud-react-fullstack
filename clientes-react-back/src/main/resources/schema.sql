CREATE TABLE IF NOT EXISTS clientes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(255),
    identificacion VARCHAR(50),
    email VARCHAR(255),
    fecha_registro TIMESTAMP
);