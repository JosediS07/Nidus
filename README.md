# Nidus

API REST de gestión centralizada de reservas de recursos (salas de reuniones, equipos tecnológicos, citas) con arquitectura hexagonal, autenticación JWT y notificaciones por correo electrónico.

## Stack tecnológico

| Capa | Tecnología |
|------|-----------|
| Backend | Java 25, Spring Boot 4.1.0, Spring Security, Spring Data JPA |
| Base de datos | PostgreSQL |
| Autenticación | JWT stateless (`Authorization: Bearer <token>`) |
| Documentación API | OpenAPI 3.0 (Swagger UI) |
| Notificaciones | Spring Mail + Thymeleaf + @Async |
| Build | Maven |
| Contenedores | Docker + docker-compose |

## Requisitos

- **Local:** Java 25+, Maven (`./mvnw`), PostgreSQL 16+
- **Docker:** Docker Engine + docker-compose

## Ejecutar

### Con Docker (recomendado)

```bash
# Crear archivo .env con las variables obligatorias
cat > .env << EOF
POSTGRES_PASSWORD=cambiar123
DB_PASSWORD=cambiar123
JWT_SECRET=$(openssl rand -base64 32)
ADMIN_PASSWORD=admin123
MAIL_PASSWORD=
EOF

# Arrancar
docker compose up
```

La API disponible en `http://localhost:8080`.

### Local (sin Docker)

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=local
```

Requiere PostgreSQL en `localhost:5432` con base de datos `nidus`.

### Variables de entorno

> ⚠️ **Seguridad:** Nunca uses los valores por defecto en producción. Cambia todas las contraseñas y el `JWT_SECRET`.

| Variable | Descripción | Obligatoria | Valor por defecto |
|----------|------------|:-----------:|-------------------|
| `DB_URL` | URL de conexión a PostgreSQL | Sí | — |
| `DB_USER` | Usuario de PostgreSQL | Sí | — |
| `DB_PASSWORD` | Contraseña de PostgreSQL | Sí | — |
| `JWT_SECRET` | Clave secreta para firmar JWT (base64, 256 bits mínimo) | Sí | — |
| `JWT_EXPIRATION` | Tiempo de expiración del token en ms | No | `86400000` (24 h) |
| `ADMIN_EMAIL` | Email del administrador por defecto | No | `admin@nidus.com` |
| `ADMIN_PASSWORD` | Contraseña del administrador por defecto | Sí | — |
| `MAIL_HOST` | Servidor SMTP | No | `localhost` |
| `MAIL_PORT` | Puerto SMTP | No | `587` |
| `MAIL_USERNAME` | Usuario SMTP | No | `dev` |
| `MAIL_PASSWORD` | Contraseña SMTP | Sí (si se usan emails) | — |
| `MAIL_FROM` | Dirección remitente de los correos | No | `noreply@nidus.com` |

### Semilla por defecto

Al arrancar por primera vez se crea un usuario administrador con las credenciales definidas en `ADMIN_EMAIL` y `ADMIN_PASSWORD`.

> ⚠️ **Cambia `ADMIN_PASSWORD`** en cualquier entorno que no sea desarrollo local.

## Documentación de la API (Swagger)

Una vez arrancado el proyecto:

- **Swagger UI:** [`http://localhost:8080/swagger-ui.html`](http://localhost:8080/swagger-ui.html)
- **OpenAPI JSON:** [`http://localhost:8080/v3/api-docs`](http://localhost:8080/v3/api-docs)

Para probar endpoints protegidos:

1. Haz login con `POST /api/v1/auth/login`
2. Copia el `token` de la respuesta
3. En Swagger UI, haz clic en **Authorize** y pega el token

## API

### Autenticación (`/api/v1/auth`)

| Método | Ruta | Descripción | Acceso |
|--------|------|-------------|--------|
| POST | `/api/v1/auth/register` | Registrar un nuevo usuario | Público |
| POST | `/api/v1/auth/login` | Iniciar sesión y obtener JWT | Público |
| PUT | `/api/v1/auth/usuarios/{id}/rol` | Cambiar el rol de un usuario | ADMIN |

### Recursos (`/api/v1/recursos`)

| Método | Ruta | Descripción | Acceso |
|--------|------|-------------|--------|
| GET | `/api/v1/recursos` | Listar todos los recursos activos | Autenticado |
| GET | `/api/v1/recursos/{id}` | Obtener un recurso por ID | Autenticado |
| POST | `/api/v1/recursos` | Crear un nuevo recurso | ADMIN |
| PUT | `/api/v1/recursos/{id}` | Actualizar un recurso existente | ADMIN |
| DELETE | `/api/v1/recursos/{id}` | Desactivar un recurso (borrado lógico) | ADMIN |

### Reservas (`/api/v1/reservas`)

| Método | Ruta | Descripción | Acceso |
|--------|------|-------------|--------|
| POST | `/api/v1/reservas` | Crear una reserva | USER, ADMIN |
| GET | `/api/v1/reservas` | Listar reservas del usuario autenticado | USER, ADMIN |
| GET | `/api/v1/reservas/todas` | Listar todas las reservas | ADMIN |
| GET | `/api/v1/reservas/{id}` | Obtener una reserva por ID | USER (propias), ADMIN |
| PUT | `/api/v1/reservas/{id}` | Modificar una reserva existente | USER (propias), ADMIN |
| DELETE | `/api/v1/reservas/{id}` | Cancelar una reserva | USER (propias), ADMIN |

## Pruebas

```bash
./mvnw test
```

### Tests unitarios (41 tests)

| Clase | Tests | Descripción |
|-------|:-----:|-------------|
| `AuthServiceTest` | 7 | Registro, login, cambio de rol |
| `JwtServiceTest` | 3 | Generación y validación de tokens |
| `RecursoServiceTest` | 9 | CRUD de recursos con permisos |
| `ReservaServiceTest` | 15 | Creación, modificación, cancelación y solapamientos |
| `EmailNotificacionAdapterTest` | 7 | Envío de correos y captura de errores |

### Tests de integración

Requieren base de datos PostgreSQL y las variables de entorno configuradas. No se ejecutan por defecto con `./mvnw test`.

## Estructura del proyecto

```
api/                              ← módulo Maven del backend
├── src/main/java/com/nidus/
│   ├── auth/                     ← autenticación y roles (JWT, SecurityConfig)
│   ├── recurso/                  ← gestión de recursos (CRUD)
│   ├── reserva/                  ← motor de reservas con detección de solapamientos
│   ├── notificacion/             ← envío de correos (Spring Mail + Thymeleaf + @Async)
│   └── shared/                   ← configuración transversal (excepciones, Swagger)
├── src/main/resources/
│   ├── templates/email/          ← plantillas HTML para correos
│   └── application.yaml         ← configuración principal
└── src/test/java/com/nidus/
    ├── auth/
    ├── recurso/
    ├── reserva/
    └── notificacion/

spec/                             ← documentación y reglas del proyecto
├── constitution/                 ← roadmap, tech-stack
└── features/                     ← especificaciones por feature

Dockerfile                        ← build multi-etapa para producción
docker-compose.yml                ← orquestación local (app + PostgreSQL)
```

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
- **Puertos:** interfaces que definen contratos de entrada (servicios) y salida (repositorios, notificaciones)
- **Adaptadores:** implementaciones concretas (JPA, REST, JavaMail)

Cada módulo (`auth`, `recurso`, `reserva`, `notificacion`) es independiente y se comunica a través de sus puertos.

## Seguridad

### Medidas implementadas

- Contraseñas hasheadas con BCrypt
- JWT firmado con HMAC-SHA256 (secreto de 256 bits)
- Tokens con expiración configurable
- Roles y permisos por endpoint (`@PreAuthorize`)
- Usuarios solo pueden modificar/cancelar sus propias reservas
- Borrado lógico de recursos (no se eliminan datos)

### Buenas prácticas

- **No hardcodees secretos** — todas las credenciales van en variables de entorno
- **Usa JWT_SECRET diferente** en cada entorno (local, tests, docker, producción)
- **No expongas la API** sin autenticación en producción
- **Configura HTTPS** en producción (Railway/Render lo proveen automáticamente)

## Licencia

Proyecto de portafolio. Uso libre para fines educativos.
