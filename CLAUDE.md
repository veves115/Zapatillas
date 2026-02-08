# Proyecto Zapatillas - Contexto

## Descripcion
API REST de Zapatillas con Spring Boot 4.0.0, Java 21, Maven.
Basado en el proyecto de referencia de tarjetas (carlosgs-iesquevedo/DWES25-26).

## Compilar y ejecutar

```bash
./mvnw clean install         # Compilar e instalar
./mvnw spring-boot:run       # Arrancar en puerto 3000
./mvnw test                  # Ejecutar tests
```

- App: http://localhost:3000
- H2 Console: http://localhost:3000/h2-console
- Swagger UI: http://localhost:3000/swagger-ui.html

## Arquitectura

- **Package base**: `es.pabloab.zapatillas`
- **Patron**: Controller -> Service -> Repository con DTOs y Mappers
- **Security**: JWT (HS256, 24h) + Spring Security con roles ADMIN y USER
- **WebSockets**: STOMP sobre `/ws`, topic `/topic/zapatillas`
- **Templates**: Pebble 4.1.0 (`.peb.html`)
- **DB**: H2 en memoria (`jdbc:h2:mem:zapatillas`, ddl-auto: create-drop)
- **Puerto**: 3000
- **Cache**: Simple in-memory
- **Docs API**: SpringDoc OpenAPI 2.3.0

## Estructura de paquetes

```
es.pabloab.zapatillas/
  config/          # SecurityConfig, SwaggerConfig, WebSocketConfig, SecurityUtils
  rest/
    auth/          # Login y registro (JWT)
    zapatillas/    # CRUD zapatillas + WebSocket controller
    cliente/       # Gestion de clientes
    user/          # Gestion de usuarios
  utils/pagination/
  web/controllers/ # Controlador web publico (Pebble)
```

## Entidades

### Zapatilla
Campos: `id`, `marca` (50), `modelo` (100), `codigoProducto` (10, unique), `talla` (Double), `color` (50), `tipo` (20), `precio` (Double), `stock` (Integer), `uuid` (auto), `createdAt`, `updatedAt`

Tipos validos: Running, Basketball, Training, Casual, Trail

### User (implements UserDetails)
Campos: `id`, `nombre`, `apellidos`, `username` (unique), `email` (unique), `password`, `roles` (Set<Role>), `cliente` (OneToOne), `deleted`, `createdAt`, `updatedAt`

### Cliente
Campos: `id`, `nombre` (100), `email` (150, unique), `createdAt`, `updatedAt`

## Endpoints REST

### Zapatillas (`/api/v1/zapatillas`)
| Metodo | Ruta | Acceso | Descripcion |
|--------|------|--------|-------------|
| GET | `/api/v1/zapatillas` | Publico | Listar (paginado, filtros: marca, tipo) |
| GET | `/api/v1/zapatillas/{id}` | Publico | Obtener por ID |
| POST | `/api/v1/zapatillas` | ADMIN | Crear |
| PUT | `/api/v1/zapatillas/{id}` | ADMIN | Actualizar completo |
| PATCH | `/api/v1/zapatillas/{id}` | ADMIN | Actualizar parcial |
| DELETE | `/api/v1/zapatillas/{id}` | ADMIN | Eliminar |

Parametros paginacion: `page`, `size` (default 10), `sortBy` (default "id"), `direction` (asc/desc)

### Auth (`/api/v1/auth`)
| Metodo | Ruta | Acceso | Descripcion |
|--------|------|--------|-------------|
| POST | `/api/v1/auth/register` | Publico | Registro |
| POST | `/api/v1/auth/login` | Publico | Login (devuelve JWT) |

### Users (`/api/v1/users`)
| Metodo | Ruta | Acceso | Descripcion |
|--------|------|--------|-------------|
| GET | `/api/v1/users/me` | Autenticado | Perfil propio |
| GET | `/api/v1/users/{id}` | ADMIN o propio | Obtener usuario |
| PUT | `/api/v1/users/me` | Autenticado | Actualizar perfil |
| PUT | `/api/v1/users/{id}` | ADMIN o propio | Actualizar usuario |

### Clientes (`/api/v1/usuarios`)
| Metodo | Ruta | Acceso | Descripcion |
|--------|------|--------|-------------|
| GET | `/api/v1/usuarios` | Autenticado | Listar (paginado) |
| GET | `/api/v1/usuarios/{id}` | ADMIN o propio | Obtener |
| POST | `/api/v1/usuarios` | ADMIN | Crear |
| PUT | `/api/v1/usuarios/{id}` | ADMIN o propio | Actualizar |
| DELETE | `/api/v1/usuarios/{id}` | ADMIN | Eliminar |

### Web
| Metodo | Ruta | Descripcion |
|--------|------|-------------|
| GET | `/`, `/index` | Catalogo de zapatillas (HTML/Pebble) |

## WebSockets

- **Endpoint**: `/ws` (con SockJS fallback)
- **App prefix**: `/app`
- **Topics**: `/topic/zapatillas`, `/topic/broadcast`, `/queue/notificaciones`
- **Tipos de notificacion**: CREATED, UPDATED, DELETED, STOCK_LOW, PRICE_CHANGED
- Notificacion automatica de stock bajo cuando stock < 5

## Seguridad

- JWT en header `Authorization: Bearer <TOKEN>`
- Algoritmo HS256, expiracion 24h
- Passwords con BCrypt
- Endpoints publicos: GET zapatillas, auth, H2 console, Swagger, WebSocket, recursos estaticos
- `SecurityUtils` ofrece helpers: `getCurrentUser()`, `isAdmin()`, `hasRole()`

## Tests

```
src/test/java/es/pabloab/zapatillas/
  ZapatilassApplicationTests.java          # Context test
  zapatillas/controllers/                  # MockMvc controller tests
  zapatillas/dto/                          # Validacion de DTOs
  zapatillas/mappers/                      # Mapper tests
  zapatillas/services/                     # Service tests
```

Frameworks: JUnit 5, Mockito, AssertJ, Spring Test (MockMvc), Spring Security Test

## Usuarios de prueba
- `admin` / `admin123` (rol ADMIN)
- `user1` / `user123` (rol USER)
- `user2` / `user123` (rol USER)

## Proyecto de referencia
- Repo: https://github.com/carlosgs-iesquevedo/DWES25-26
- Package: `es.carlosgs.dwes2526`
- Dominio: Tarjetas de credito
- Seguir mismos patrones adaptados a zapatillas

## Ficheros utiles
- `crud-zapatillas.http` - Ejemplos REST API
- `test-security.http` - Tests de seguridad
- `ws.zapatillas.http` - Tests WebSocket
- `Websockets.md` - Documentacion WebSocket
