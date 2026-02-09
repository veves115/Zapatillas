-- Script de inicialización de base de datos
-- Incluye tablas, datos de prueba y usuarios para testing

-- ============================================
-- INSERTAR DATOS DE PRUEBA
-- ============================================

-- Zapatillas de ejemplo
INSERT INTO ZAPATILLAS(marca, modelo, codigo_producto, talla, color, tipo, precio, stock,created_at,updated_at, uuid)
VALUES ('Nike', 'Air Max 90', 'NI1234KE', 42.0, 'Rojo', 'Running', 89.99, 28,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP, RANDOM_UUID());

INSERT INTO ZAPATILLAS(marca, modelo, codigo_producto, talla, color, tipo, precio, stock,created_at,updated_at, uuid)
VALUES ('Fila', 'High Top', 'FI1234LA', 40.0, 'Blanco', 'Running', 79.99, 52,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP, RANDOM_UUID());

INSERT INTO ZAPATILLAS(marca, modelo, codigo_producto, talla, color, tipo, precio, stock,created_at,updated_at ,uuid)
VALUES ('Adidas', 'UltraBoost', 'AD5678AS', 43.0, 'Negro', 'Running', 149.99, 15,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP, RANDOM_UUID());

INSERT INTO ZAPATILLAS(marca, modelo, codigo_producto, talla, color, tipo, precio, stock,created_at,updated_at ,uuid)
VALUES ('Puma', 'RS-X', 'PU9012MA', 41.5, 'Azul', 'Casual', 99.99, 3,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP, RANDOM_UUID());

INSERT INTO ZAPATILLAS(marca, modelo, codigo_producto, talla, color, tipo, precio, stock,created_at,updated_at ,uuid)
VALUES ('New Balance', '574', 'NB3456CE', 42.5, 'Gris', 'Casual', 89.99, 20,CURRENT_TIMESTAMP,CURRENT_TIMESTAMP, RANDOM_UUID());

-- ============================================
-- CREAR USUARIOS DE PRUEBA
-- ============================================

-- USUARIO ADMINISTRADOR
-- Username: admin
-- Password: admin123
-- Contraseña hasheada con BCrypt (10 rondas)
INSERT INTO usuarios (nombre, apellidos, username, email, password, deleted, cliente_id, created_at, updated_at)
VALUES ('Admin', 'Sistema', 'admin', 'admin@zapatillas.com',
        '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
        false, null, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Asignar rol ADMIN al usuario admin
INSERT INTO user_roles (user_id, roles)
SELECT id, 'ADMIN'
FROM usuarios
WHERE username = 'admin';

-- USUARIO NORMAL 1
-- Username: user1
-- Password: user123
INSERT INTO usuarios (nombre, apellidos, username, email, password, deleted, cliente_id, created_at, updated_at)
VALUES ('Juan', 'Pérez', 'user1', 'juan@example.com',
        '$2a$10$5UR5me5.E5x7zCKJLxwmL.cLdQl5L3A2EjqHI0lRvVJv7p8u8u8u8',
        false, null, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Asignar rol USER al usuario user1
INSERT INTO user_roles (user_id, roles)
SELECT id, 'USER'
FROM usuarios
WHERE username = 'user1';

-- USUARIO NORMAL 2
-- Username: user2
-- Password: user123
INSERT INTO usuarios (nombre, apellidos, username, email, password, deleted, cliente_id, created_at, updated_at)
VALUES ('María', 'García', 'user2', 'maria@example.com',
        '$2a$10$5UR5me5.E5x7zCKJLxwmL.cLdQl5L3A2EjqHI0lRvVJv7p8u8u8u8',
        false, null, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Asignar rol USER al usuario user2
INSERT INTO user_roles (user_id, roles)
SELECT id, 'USER'
FROM usuarios
WHERE username = 'user2';

-- ============================================
-- INFORMACIÓN DE USUARIOS DE PRUEBA
-- ============================================

-- Para probar la aplicación, usa estos usuarios:
--
-- ADMINISTRADOR:
--   Username: admin
--   Password: admin123
--   Puede: crear, modificar y eliminar zapatillas y clientes
--
-- USUARIOS NORMALES:
--   Username: user1 / Password: user123
--   Username: user2 / Password: user123
--   Pueden: ver zapatillas, ver y modificar su propio perfil
--
-- ENDPOINTS DE PRUEBA:
--   POST /api/v1/auth/login - Login (devuelve token JWT)
--   POST /api/v1/auth/register - Registro de nuevo usuario
--   GET /api/v1/zapatillas - Ver zapatillas (público)
--   POST /api/v1/zapatillas - Crear zapatilla (solo ADMIN)
--   GET /api/v1/users/me - Ver mi perfil
--   PUT /api/v1/users/me - Actualizar mi perfil