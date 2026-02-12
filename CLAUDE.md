# Proyecto Zapatillas - Contexto

## Descripcion
API REST de Zapatillas con Spring Boot 4.0.0, Java 21, Maven.
Basado en el proyecto de referencia de tarjetas(https://github.com/veves115/DWES25-26.git).

## Repositorio
- GitHub: https://github.com/veves115/Zapatillas.git
- Branch: master
- 36+ commits

## Compilar y ejecutar

```bash
./mvnw clean install         # Compilar e instalar
./mvnw spring-boot:run       # Arrancar en puerto 3000
./mvnw test                  # Ejecutar tests
```

- App: http://localhost:3000
- H2 Console: http://localhost:3000/h2-console
- Swagger UI: http://localhost:3000/swagger-ui.html
- GraphiQL: http://localhost:3000/graphiql

## Arquitectura

- **Package base**: `es.pabloab.zapatillas`
- **Patron**: Controller -> Service -> Repository con DTOs y Mappers
- **Security**: JWT (HS256, 24h) + Spring Security con roles ADMIN y USER
- **WebSockets**: STOMP sobre `/ws`, topic `/topic/zapatillas`
- **GraphQL**: Queries + Mutations sobre `/graphql`, playground en `/graphiql`
- **Templates**: Pebble 4.1.0 (`.peb.html`)
- **DB**: H2 en memoria (`jdbc:h2:mem:zapatillas`, ddl-auto: create-drop, defer-datasource-initialization: true)
- **Puerto**: 3000
- **Cache**: Simple in-memory
- **Docs API**: SpringDoc OpenAPI 2.3.0

## Estructura de paquetes

```
es.pabloab.zapatillas/
  config/              # SecurityConfig, SwaggerConfig, WebSocketConfig, SecurityUtils
  graphql/controllers/ # ZapatillaGraphQLController (queries + mutations)
  rest/
    auth/              # Login y registro (JWT)
    zapatillas/        # CRUD zapatillas + WebSocket controller
    cliente/           # Gestion de clientes
    user/              # Gestion de usuarios
  utils/pagination/
  web/controllers/     # ZonaPublicaController (Pebble templates)
```

## Entidades

### Zapatilla
Campos: `id`, `marca` (50), `modelo` (100), `codigoProducto` (10, unique), `talla` (Double), `color` (50), `tipo` (20), `precio` (Double), `stock` (Integer), `uuid` (auto), `createdAt`, `updatedAt`

Tipos validos: Running, Basketball, Training, Casual, Trail

### User (implements UserDetails)
Campos: `id`, `nombre`, `apellidos`, `username` (unique), `email` (unique), `password`, `roles` (Set<Role>), `cliente` (OneToOne), `deleted`, `createdAt`, `updatedAt`

### Cliente
Campos: `id`, `nombre`, `apellidos`, `direccion`, `email`, `telefono`, `usuario` (OneToOne), `createdAt`, `updatedAt`

## Endpoints principales

### REST API
| Metodo | Ruta | Descripcion |
|--------|------|-------------|
| GET | `/api/v1/zapatillas` | Listar (paginado, filtros marca/tipo) |
| GET | `/api/v1/zapatillas/{id}` | Detalle por ID |
| POST | `/api/v1/zapatillas` | Crear (ADMIN) |
| PUT | `/api/v1/zapatillas/{id}` | Actualizar (ADMIN) |
| PATCH | `/api/v1/zapatillas/{id}` | Actualizar parcial (ADMIN) |
| DELETE | `/api/v1/zapatillas/{id}` | Eliminar (ADMIN) |
| POST | `/api/v1/auth/login` | Login (devuelve JWT) |
| POST | `/api/v1/auth/register` | Registro |

### GraphQL
| Tipo | Operacion | Descripcion |
|------|-----------|-------------|
| Query | `zapatillas` | Listar todas |
| Query | `zapatillaById(id)` | Buscar por ID |
| Query | `zapatillasByMarca(marca)` | Filtrar por marca |
| Query | `zapatillasByTipo(tipo)` | Filtrar por tipo |
| Mutation | `createZapatilla(input)` | Crear zapatilla |
| Mutation | `updateZapatilla(id, input)` | Actualizar zapatilla |
| Mutation | `deleteZapatilla(id)` | Eliminar zapatilla |

### Web (Pebble)
| Metodo | Ruta | Descripcion |
|--------|------|-------------|
| GET | `/`, `/index` | Catalogo de zapatillas (HTML/Pebble, paginado) |

## GraphQL - Ficheros

```
src/main/resources/graphql/schema.graphqls    # Schema: tipos, queries, mutations, inputs
src/main/java/.../graphql/controllers/
  ZapatillaGraphQLController.java             # @QueryMapping + @MutationMapping
```

- Schema define: type Zapatilla, type Query, type Mutation, input CreateZapatillaInput, input UpdateZapatillaInput
- Controller usa records internos para mapear inputs GraphQL
- Reutiliza ZapatillaMapper y ZapatillasRepository existentes
- Security: `/graphql` y `/graphiql/**` con permitAll() en SecurityConfig

## Pebble Templates - Ficheros

```
src/main/resources/templates/
  index.peb.html                              # Catalogo con cards, paginacion, badges stock
  fragments/
    layout.peb.html                           # Layout base (head, navbar, footer, content block)
    head.peb.html                             # Meta, Bootstrap CSS, Bootstrap Icons, estilos custom
    navbar.peb.html                           # Navbar con links a Inicio, API, H2, Swagger
    footer.peb.html                           # Footer con nombre app y copyright
    inputField.peb.html                       # Macro reutilizable para campos de formulario
```

- Usa WebJars: Bootstrap 5.3.8, Bootstrap Icons 1.13.1
- Controller: `ZonaPublicaController` en `web/controllers/`
- Paginacion completa con navegacion por paginas
- Badges de stock: verde (>10), amarillo (1-10), rojo (0)

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
- Endpoints publicos: GET zapatillas, auth, H2 console, Swagger, WebSocket, GraphQL, recursos estaticos
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
- `http-client/graphql.http` - Ejemplos queries GraphQL
- `Websockets.md` - Documentacion WebSocket

## Notas tecnicas importantes
- `data.sql` usa snake_case para columnas (Hibernate naming strategy): `codigo_producto`, `created_at`, `updated_at`
- `data.sql` necesita `CURRENT_TIMESTAMP` explicito para timestamps y `RANDOM_UUID()` para uuid
- `spring.jpa.defer-datasource-initialization=true` es obligatorio para que data.sql se ejecute despues de Hibernate crear las tablas
- Repository tiene metodos con y sin Pageable: sin Pageable para GraphQL, con Pageable para REST
- Pebble version en pom.xml: 4.1.0 (compatible con Spring Boot 4 / Spring 7)
- Pebble config: `pebble.suffix=.peb.html`, `pebble.cache=false`