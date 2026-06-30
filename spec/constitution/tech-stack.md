# Tech stack y convenciones

## Tecnologías

- **Lenguaje backend:** Java 17+
- **Framework backend:** Spring Boot 3.x, Spring Security, Spring Data JPA
- **Base de datos:** Relacional (H2 para desarrollo, PostgreSQL para producción)
- **Autenticación:** Stateless con JWT (almacenado en el cliente, enviado como `Authorization: Bearer <token>`)
- **Notificaciones:** JavaMail (envío automático de correos al confirmar, cancelar o modificar una reserva)
- **Frontend:** React (SPA) con consumo de API REST
- **Tests:** JUnit 5 + Mockito (backend); Vitest + React Testing Library (frontend)
- **Build:** Maven (backend), Vite (frontend)

## Arquitectura del código (backend)

Paquete raíz: `com.nidus`. Organización por **módulos de dominio**:

```
com.nidus
├── auth          — autenticación y roles (Admin, Usuario)
├── booking       — lógica de reservas y control de conflictos
├── recurso       — gestión de recursos (CRUD)
├── notification  — envío de correos (JavaMail)
└── shared        — utilidades transversales (excepciones, dtos, config)
```

## Archivos / módulos clave

- `com.nidus.auth.*` — registro, login, JWT filter, seguridad.
- `com.nidus.booking.*` — servicio de reservas con validación de conflictos.
- `com.nidus.recurso.*` — CRUD de recursos con visibilidad por rol.
- `com.nidus.notification.*` — plantillas de correo y envío asíncrono.
- `com.nidus.shared.*` — excepciones globales, DTOs base, configuración común.

## Comandos

- `./mvnw spring-boot:run` — arranca el backend en local.
- `./mvnw test` — ejecuta los tests del backend.
- `./mvnw verify` — build completo con tests y chequeos.
- `npm run dev` — arranca el frontend en local.
- `npm run test` — ejecuta los tests del frontend.

## Modelo de datos / dominio

- **User** — id, nombre, email (único), password hash, rol (ADMIN / USER), creado, activo.
- **Resource** — id, nombre, tipo (SALA, PROYECTOR, VEHICULO, OTRO), descripción, capacidad, activo.
- **Booking** — id, recurso (FK), usuario (FK), fechaInicio, fechaFin, estado (CONFIRMADA, CANCELADA, MODIFICADA), creado, modificado.
- **Invariante crítico:** No pueden existir dos `Booking` con el mismo `resource_id` y rangos de fecha solapados cuando ambas estén en estado `CONFIRMADA`.

## Convenciones

- **Idioma:** Código fuente, comentarios y documentación en español (el proyecto es para portafolio en español).
- **Nombres:** camelCase para variables y métodos; PascalCase para clases; UPPER_SNAKE para constantes.
- **API REST:** URLs en plural (`/api/resources`, `/api/bookings`), versionado por prefijo (`/api/v1/`).
- **Respuestas:** DTOs específicos por caso de uso (no exponer entidades JPA directamente).
- **Manejo de errores:** `@ControllerAdvice` global con `ResponseEntity` estructurado (`{ error, message, status, timestamp }`).
- **Validación:** Jakarta Validation (`@Valid`, `@NotBlank`, etc.) en los DTOs de entrada.
 - **Tests:** Unitarios con Mockito para servicios; repositorios con `@DataJpaTest`; controllers con `@WebMvcTest`.
- **Commits:** Conventional Commits: `tipo(alcance): descripción`.
  Tipos: `feat`, `docs`, `test`, `refactor`, `chore`.
  Alcance: nombre del módulo (`auth`, `recursos`, `booking`, etc.).
- **Ramas:** `feature/NNN-nombre` desde `main`. Merge a `main` con `--no-ff` para mantener historial de ramas.
- **Confirmación:** Cada commit/acción se presenta al autor y requiere confirmación explícita antes de ejecutarse.

## Límites duros

- No exponer contraseñas ni datos sensibles en respuestas de la API.
- No subir archivos `.env`, `*.pem`, ni secretos al repositorio.
- No añadir dependencias nuevas sin actualizar este documento.
- No usar consultas nativas SQL si se puede expresar con JPQL o Criteria API.
- No eliminar ni modificar datos de otros usuarios desde endpoints de usuario estándar (solo admin).
