# Nidus

Plataforma SaaS de reserva y gestión centralizada de recursos (salas de reuniones, equipos tecnológicos, citas).

## Stack

| Capa | Tecnología |
|------|-----------|
| Backend | Java 24, Spring Boot 4.1.0, Spring Security, Spring Data JPA |
| Base de datos | MySQL (desarrollo), PostgreSQL (producción) |
| Autenticación | JWT stateless |
| Frontend | React (SPA) — _próximamente_ |
| Build | Maven |

## Requisitos

- Java 24+
- Maven (incluye `mvnw`)

## Ejecutar

```bash
./mvnw spring-boot:run
```

La API arranca en `http://localhost:8080`.

## Tests

```bash
./mvnw test
```

## API

### Autenticación (`/api/v1/auth`)

| Método | Ruta | Descripción | Auth |
|--------|------|-------------|------|
| POST | `/api/v1/auth/register` | Registrar usuario | No |
| POST | `/api/v1/auth/login` | Iniciar sesión | No |

### Recursos (`/api/v1/recursos`)

| Método | Ruta | Descripción | Auth |
|--------|------|-------------|------|
| GET | `/api/v1/recursos` | Listar recursos | Sí |
| GET | `/api/v1/recursos/{id}` | Obtener recurso | Sí |
| POST | `/api/v1/recursos` | Crear recurso | ADMIN |
| PUT | `/api/v1/recursos/{id}` | Actualizar recurso | ADMIN |
| DELETE | `/api/v1/recursos/{id}` | Desactivar recurso | ADMIN |

### Seed por defecto

| Email | Contraseña | Rol |
|-------|-----------|-----|
| admin@nidus.com | admin123 | ADMIN |

## Estructura

```
api/                         ← código fuente del backend
├── src/main/java/com/nidus/
│   ├── auth/                ← autenticación y roles
│   ├── recurso/             ← gestión de recursos
│   └── shared/              ← configuración y excepciones globales
└── src/test/java/com/nidus/
    ├── auth/
    └── recurso/

spec/
├── constitution/            ← reglas del proyecto
└── features/                ← especificaciones por feature
```
