# Nidus

Sistema de gestión centralizada de reservas de recursos (salas de reuniones, equipos tecnológicos, vehículos) con autenticación JWT, cola de espera, notificaciones por correo y panel de administración.

## Stack tecnológico

| Capa | Tecnología |
|------|-----------|
| Backend | Java 25, Spring Boot 4.1.0, Spring Security, Spring Data JPA |
| Frontend | Angular 19, Angular Material, Tailwind CSS v4 |
| Base de datos | PostgreSQL 16 |
| Autenticación | JWT stateless (`Authorization: Bearer <token>`) |
| Documentación API | OpenAPI 3.0 (Swagger UI) |
| Notificaciones | Spring Mail + Thymeleaf + `@Async` |
| Build | Maven (backend), npm (frontend) |
| Infraestructura | Docker multi-stage, GitHub Actions CI/CD |

## Requisitos

- Java 25+, Node.js 20+, PostgreSQL 16+

## Ejecutar

### Docker (recomendado)

```bash
docker compose up --build
```

Levanta PostgreSQL + backend en `http://localhost:8080`. El frontend se sirve desde Spring Boot (single-container).

### Desarrollo local

**Backend:**

```bash
cd api
./mvnw spring-boot:run
```

**Frontend (desarrollo con hot-reload):**

```bash
cd frontend
npm install
ng serve
```

El frontend corre en `http://localhost:4200` y apunta al backend en `http://localhost:8080/api/v1`.

### Variables de entorno

Configurar como variables del sistema (IntelliJ Run Config, Render Dashboard, etc.). No usar archivos `.env` en el repositorio.

| Variable | Descripción | Obligatoria | Default |
|----------|------------|:-----------:|---------|
| `DB_URL` | URL de conexión a PostgreSQL | Sí | — |
| `DB_USER` | Usuario de PostgreSQL | Sí | — |
| `DB_PASSWORD` | Contraseña de PostgreSQL | Sí | — |
| `JWT_SECRET` | Clave secreta para JWT (base64, 256 bits mínimo) | Sí | — |
| `JWT_EXPIRATION` | Expiración del token en ms | No | `86400000` (24 h) |
| `ADMIN_EMAIL` | Email del administrador por defecto | No | `admin@nidus.com` |
| `ADMIN_PASSWORD` | Contraseña del administrador inicial | No | vacío |
| `MAIL_HOST` | Servidor SMTP | No | `localhost` |
| `MAIL_PORT` | Puerto SMTP | No | `587` |
| `MAIL_USERNAME` | Usuario SMTP | No | `dev` |
| `MAIL_PASSWORD` | Contraseña SMTP | No* | `dev` |
| `MAIL_FROM` | Dirección remitente | No | `noreply@nidus.com` |

> *`MAIL_PASSWORD` solo es obligatoria si se usa envío real de correos.

### Semilla por defecto

Al arrancar por primera vez se crea un usuario administrador con las credenciales de `ADMIN_EMAIL` y `ADMIN_PASSWORD`. El `DataInitializer` no sobreescribe la contraseña si el usuario ya existe.

## Modelo de datos

```
┌──────────────┐       ┌──────────────────┐       ┌───────────────────┐
│    users     │       │     recursos     │       │     reservas      │
├──────────────┤       ├──────────────────┤       ├───────────────────┤
│ id (PK)      │       │ id (PK)          │◄──┐   │ id (PK)           │
│ nombre       │       │ nombre           │   │   │ recurso_id (FK)   │──┘
│ email (uniq) │  ◄────│ tipo (enum)      │   │   │ usuario_id (FK)   │──┐
│ password     │       │ descripcion      │   │   │ fecha_inicio      │  │
│ rol (enum)   │       │ capacidad        │   │   │ fecha_fin         │  │
│ activo       │       │ activo           │   │   │ estado (enum)     │  │
│ creado       │       └──────────────────┘   │   │ version (locking) │  │
└──────────────┘                              │   └───────────────────┘  │
                                              │                         │
┌──────────────────────┐                      │   ┌───────────────────┐  │
│ solicitudes_cola     │                      │   │historial_reservas │  │
├──────────────────────┤                      │   ├───────────────────┤  │
│ id (PK)              │                      │   │ id (PK)           │  │
│ recurso_id (FK)      │──────────────────────┘   │ reserva_id (FK)   │  │
│ usuario_id (FK)      │──────────────────────────│ usuario_id (FK)   │──┘
│ estado (enum)        │                          │ tipo_evento       │
│ creado               │                          │ descripcion       │
└──────────────────────┘                          │ creado            │
                                                  └───────────────────┘
```

### Enums

| Enum | Valores |
|------|---------|
| `Role` | `ADMIN`, `USER` |
| `TipoRecurso` | `SALA`, `PROYECTOR`, `VEHICULO`, `OTRO` |
| `EstadoReserva` | `CONFIRMADA`, `CANCELADA`, `MODIFICADA` |
| `EstadoSolicitud` | `PENDIENTE`, `NOTIFICADA`, `CANCELADA` |

### Invariante crítico

No pueden existir dos reservas con el mismo `recurso_id` y rangos de fecha solapados cuando ambas estén en estado `CONFIRMADA`. Se valida con JPQL y se protege con optimistic locking (`@Version`).

## API

Todas las rutas requieren autenticación excepto `register` y `login`. El JWT se envía como `Authorization: Bearer <token>`.

### Autenticación (`/api/v1/auth`)

| Método | Ruta | Descripción | Acceso |
|--------|------|-------------|--------|
| POST | `/register` | Registrar usuario | Público |
| POST | `/login` | Iniciar sesión | Público |
| GET | `/me` | Obtener perfil del usuario autenticado | USER/ADMIN |
| PUT | `/me` | Actualizar perfil (nombre, email, password) | USER/ADMIN |
| PUT | `/usuarios/{id}/rol` | Cambiar rol de un usuario | ADMIN |

### Recursos (`/api/v1/recursos`)

| Método | Ruta | Descripción | Acceso |
|--------|------|-------------|--------|
| GET | `/` | Listar recursos activos | Autenticado |
| GET | `/{id}` | Obtener recurso por ID | Autenticado |
| POST | `/` | Crear recurso | ADMIN |
| PUT | `/{id}` | Actualizar recurso | ADMIN |
| DELETE | `/{id}` | Desactivar recurso (borrado lógico) | ADMIN |

### Reservas (`/api/v1/reservas`)

| Método | Ruta | Descripción | Acceso |
|--------|------|-------------|--------|
| POST | `/` | Crear reserva | USER/ADMIN |
| GET | `/` | Listar mis reservas | USER/ADMIN |
| GET | `/todas` | Listar todas las reservas | ADMIN |
| GET | `/{id}` | Obtener reserva por ID | USER (propias), ADMIN |
| PUT | `/{id}` | Modificar reserva | USER (propias), ADMIN |
| DELETE | `/{id}` | Cancelar reserva | USER (propias), ADMIN |

### Cola de espera (`/api/v1/cola`)

| Método | Ruta | Descripción | Acceso |
|--------|------|-------------|--------|
| POST | `/` | Apuntarse a la cola de un recurso | USER/ADMIN |
| GET | `/` | Listar mis solicitudes | USER/ADMIN |
| DELETE | `/{id}` | Salir de la cola | USER/ADMIN |

Cuando se cancela una reserva, se notifica automáticamente al primer usuario en la cola de ese recurso.

### Admin (`/api/v1/admin`)

Todos los endpoints requieren rol `ADMIN`.

**Usuarios:**

| Método | Ruta | Descripción |
|--------|------|-------------|
| GET | `/usuarios` | Listar usuarios (paginado) |
| GET | `/usuarios/{id}` | Obtener usuario |
| POST | `/usuarios` | Crear usuario |
| PUT | `/usuarios/{id}` | Actualizar usuario |
| PUT | `/usuarios/{id}/rol` | Cambiar rol |
| DELETE | `/usuarios/{id}` | Eliminar usuario |

**Recursos:**

| Método | Ruta | Descripción |
|--------|------|-------------|
| GET | `/recursos` | Listar recursos (paginado) |
| GET | `/recursos/{id}` | Obtener recurso |
| POST | `/recursos` | Crear recurso |
| PUT | `/recursos/{id}` | Actualizar recurso |
| DELETE | `/recursos/{id}` | Eliminar recurso |

**Reservas (con filtros):**

| Método | Ruta | Descripción |
|--------|------|-------------|
| GET | `/reservas` | Listar reservas (paginado, con filtros) |
| GET | `/reservas/{id}` | Obtener reserva |
| PUT | `/reservas/{id}/cancelar` | Cancelar reserva |
| GET | `/reservas/{id}/historial` | Historial de eventos |

Filtros opcionales para `GET /reservas`:

| Parámetro | Tipo | Descripción |
|-----------|------|-------------|
| `estado` | String | `CONFIRMADA`, `CANCELADA`, `MODIFICADA` |
| `recursoNombre` | String | Búsqueda por nombre de recurso (parcial) |
| `usuarioNombre` | String | Búsqueda por nombre de usuario (parcial) |
| `fechaInicio` | DateTime (ISO) | Reservas que terminan después de esta fecha |
| `fechaFin` | DateTime (ISO) | Reservas que empiezan antes de esta fecha |
| `page` | Int | Número de página (default: 0) |
| `size` | Int | Tamaño de página (default: 20) |

**Cola:**

| Método | Ruta | Descripción |
|--------|------|-------------|
| GET | `/cola` | Listar solicitudes de cola (paginado) |
| DELETE | `/cola/{id}` | Eliminar solicitud de cola |

**Dashboard:**

| Método | Ruta | Descripción |
|--------|------|-------------|
| GET | `/dashboard` | Métricas del sistema |

## Documentación de la API (Swagger)

- **Swagger UI:** `http://localhost:8080/swagger-ui.html`
- **OpenAPI JSON:** `http://localhost:8080/v3/api-docs`

## Frontend

SPA construida con Angular 19, Angular Material y Tailwind CSS v4. Los componentes son standalone (sin NgModules) y usan lazy loading por ruta.

### Rutas

| Ruta | Componente | Acceso |
|------|-----------|--------|
| `/login` | LoginComponent | Público |
| `/register` | RegisterComponent | Público |
| `/recursos` | RecursoListaComponent | Autenticado |
| `/recursos/:id` | RecursoDetalleComponent | Autenticado |
| `/reservas` | ReservaListaComponent | Autenticado |
| `/reservas/nueva` | ReservaFormComponent | Autenticado |
| `/reservas/:id/editar` | ReservaFormComponent | Autenticado |
| `/perfil` | PerfilComponent | Autenticado |
| `/dashboard` | DashboardComponent | ADMIN |
| `/admin/usuarios` | AdminUsuariosComponent | ADMIN |
| `/admin/recursos` | AdminRecursosComponent | ADMIN |
| `/admin/reservas` | AdminReservasComponent | ADMIN |
| `/admin/cola` | AdminColaComponent | ADMIN |

### Guards

- **AuthGuard** — Verifica que exista un token en `localStorage`.
- **AdminGuard** — Verifica que el usuario tenga rol `ADMIN`.

### Diseño UI

- **Colores corporativos:** Navy `#1a365d` como color primario.
- **Tipografía:** Inter (Google Fonts).
- **Componentes:** Angular Material (tabs, dialogs, datepicker, snackbar) + Tailwind para layout y utilidades.

## Arquitectura

El backend sigue **arquitectura hexagonal** (puertos y adaptadores):

```
┌──────────────┐     ┌──────────────────┐     ┌────────────────┐     ┌──────────────────┐
│  Controller  │ ──> │  Puerto entrada  │ ──> │    Servicio    │ ──> │  Puerto salida   │
│  (REST)      │     │  (interface)     │     │   de dominio   │     │  (interface)     │
└──────────────┘     └──────────────────┘     └────────────────┘     └──────────────────┘
                                                                            │
                                                                    ┌───────┴───────┐
                                                                    │   Adaptador   │
                                                                    │  (JPA / Mail) │
                                                                    └───────────────┘
```

- **Dominio:** lógica de negocio pura, sin dependencias de frameworks externos
- **Puertos:** interfaces que definen contratos de entrada (`application/port/input/`) y salida (`application/port/output/`)
- **Adaptadores:** implementaciones concretas (`infrastructure/persistence/`, `infrastructure/web/`, `infrastructure/email/`)

### Eventos de dominio

El sistema publica eventos (`ReservaEvento`) que son escuchados por:

- **HistorialReservaService** — Registra cada cambio de estado en la tabla `historial_reservas`.
- **SolicitudColaServiceImpl** — Al cancelarse una reserva, notifica al siguiente en la cola.

### Estructura del proyecto

```
├── api/                            ← Backend (Maven)
│   └── src/main/java/com/nidus/
│       ├── auth/                   ← Autenticación, JWT, roles, usuarios
│       ├── recurso/                ← Gestión de recursos (CRUD)
│       ├── reserva/                ← Motor de reservas + historial
│       ├── cola/                   ← Cola de espera
│       ├── admin/                  ← Panel de administración
│       ├── notificacion/           ← Envío de correos (Spring Mail + Thymeleaf)
│       └── shared/                 ← Config transversal (excepciones, Swagger, SPA)
│
├── frontend/                       ← Frontend (Angular 19)
│   └── src/app/
│       ├── core/                   ← Guards, interceptores, servicios, models
│       ├── shared/                 ← Header, confirm-dialog, pagination
│       └── pages/                  ← Componentes por página
│
├── spec/                           ← Documentación y especificaciones
│   ├── constitution/               ← Roadmap, tech-stack
│   └── features/                   ← Specs por feature
│
├── Dockerfile                      ← Build multi-stage (Node + Maven + JRE)
├── docker-compose.yml              ← PostgreSQL + backend
└── .github/workflows/ci.yml       ← CI/CD (GitHub Actions)
```

## Pruebas

```bash
# Todos los tests (unit + integration)
./mvnw test

# Solo tests unitarios (sin DB)
./mvnw test -DexcludedGroups=integration

# Solo tests de integración (requiere Supabase)
./mvnw test -Dgroups=integration
```

### Distribución de tests

| Clase | Tipo | Tests |
|-------|------|:-----:|
| `AdminControllerTest` | Unit | 11 |
| `AdminServiceImplTest` | Unit | 22 |
| `AuthControllerTest` | Unit | 7 |
| `AuthServiceTest` | Unit | 7 |
| `JwtServiceTest` | Unit | 3 |
| `JwtAuthenticationFilterTest` | Unit | 5 |
| `RecursoControllerTest` | Unit | 11 |
| `RecursoServiceTest` | Unit | 9 |
| `ReservaControllerTest` | Unit | 12 |
| `ReservaServiceTest` | Unit | 15 |
| `SolicitudColaServiceImplTest` | Unit | 12 |
| `EmailNotificacionAdapterTest` | Unit | 7 |
| `GlobalExceptionHandlerTest` | Unit | 10 |
| `UserRepositoryTest` | Integration | 2 |
| `RecursoRepositoryTest` | Integration | 2 |
| `ReservaRepositoryTest` | Integration | 6 |
| `**Total** | | **141** |

- **Tests unitarios (90):** No necesitan base de datos. Corren con Mockito.
- **Tests de integración (51):** Requieren conexión a PostgreSQL (Supabase, esquema `nidus_test`). Estan etiquetados con `@Tag("integration")` y se excluyen en CI.

### CI/CD

GitHub Actions ejecuta en cada push/PR a `main`:

1. **Tests backend** — Solo tests unitarios (`-DexcludedGroups=integration`)
2. **Build frontend** — `npm ci && ng build`

### Despliegue

El `Dockerfile` multi-stage construye todo en un solo contenedor:

1. **Etapa 1 (Node):** Build del frontend Angular → `dist/frontend/browser/`
2. **Etapa 2 (Maven):** Build del backend + copia de archivos estáticos del frontend a `src/main/resources/static/`
3. **Etapa 3 (JRE):** Runtime ligero con solo el JAR

Spring Boot sirve el frontend estático y el `SpaForwardController` redirige las rutas del SPA a `index.html`.

## Seguridad

### Medidas implementadas

- Contraseñas hasheadas con BCrypt
- JWT firmado con HMAC-SHA256 (secreto de 256 bits)
- Tokens con expiración configurable
- Roles y permisos por endpoint (`@PreAuthorize`)
- Usuarios solo pueden modificar/cancelar sus propias reservas
- Borrado lógico de recursos (no se eliminan datos)
- Optimistic locking con `@Version` en reservas

### Buenas prácticas

- **No hardcodees secretos** — todas las credenciales van en variables de entorno
- **Usa `JWT_SECRET` diferente** en cada entorno (local, tests, producción)
- **No expongas la API** sin autenticación en producción
- **Configura HTTPS** en producción (Render lo provee automáticamente)

## Licencia

Proyecto de portafolio. Uso libre para fines educativos.
