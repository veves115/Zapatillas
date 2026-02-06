# Proyecto Zapatillas - Contexto

## Descripción
API REST de Zapatillas con Spring Boot 4.0.0, Java 21.
Basado en el proyecto de referencia de tarjetas (carlosgs-iesquevedo/DWES25-26).

## Arquitectura
- Package base: es.pabloab.zapatillas
- Entidad principal: Zapatilla (marca, modelo, codigoProducto, talla, color, tipo, precio, stock)
- Security: JWT + Spring Security con roles ADMIN y USER
- WebSockets: STOMP sobre /ws, topic /topic/zapatillas
- Templates: Pebble (.peb.html)
- DB: H2 en memoria (desarrollo)
- Puerto: 3000

## Proyecto de referencia
- Repo: https://github.com/carlosgs-iesquevedo/DWES25-26
- Package: es.carlosgs.dwes2526
- Dominio: Tarjetas de crédito
- Seguir mismos patrones adaptados a zapatillas

## Reglas de seguridad
- GET zapatillas: público (sin token)
- POST/PUT/PATCH/DELETE zapatillas: solo ADMIN
- Auth: /api/v1/auth/login y /api/v1/auth/register

## Usuarios de prueba
- admin / admin123 (rol ADMIN)
- user1 / user123 (rol USER)
- user2 / user123 (rol USER)