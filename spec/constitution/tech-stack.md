# Stack tecnológico y convenciones

## Tecnologías

- **Lenguaje backend:** Java 25+
- **Framework backend:** Spring Boot 4.1.0, Spring Security, Spring Data JPA
- **Base de datos:** PostgreSQL 16
- **Autenticación:** JWT sin estado (stateless) — se envía como `Authorization: Bearer <token>`
- **Notificaciones:** Spring Mail + Thymeleaf + `@Async` (envío automático al confirmar, cancelar o modificar una reserva)
- **Frontend:** Angular 19, Angular Material, Tailwind CSS v4
- **Pruebas backend:** JUnit 5 + Mockito (unitarias); `@DataJpaTest` + `@SpringBootTest` (integración)
- **Pruebas frontend:** Karma + Jasmine
- **Build backend:** Maven (`./mvnw`)
- **Build frontend:** Angular CLI (`ng build`)
- **Infraestructura:** Docker multi-stage, GitHub Actions CI/CD

## Arquitectura del código (backend)

Paquete raíz: `com.nidus`. Organización por **módulos de dominio**:

```
com.nidus
├── auth          — autenticación, JWT, roles, usuarios
├── recurso       — gestión de recursos (CRUD)
├── reserva       — motor de reservas + historial de auditoría
├── cola          — cola de espera con notificación automática
├── admin         — panel de administración (dashboard, CRUD)
├── notificacion  — envío de correos (Spring Mail + Thymeleaf)
└── shared        — configuración transversal (excepciones, Swagger, SPA)
```

## Archivos / módulos clave

- `com.nidus.auth.*` — registro, login, filtro JWT, seguridad, `DataInitializer`.
- `com.nidus.recurso.*` — CRUD de recursos con visibilidad por rol.
- `com.nidus.reserva.*` — servicio de reservas con validación de solapamientos e historial.
- `com.nidus.cola.*` — cola de espera con notificación automática al cancelar reservas.
- `com.nidus.admin.*` — dashboard, gestión de usuarios/recursos/reservas con filtros.
- `com.nidus.notificacion.*` — plantillas de correo (Thymeleaf) y envío asíncrono.
- `com.nidus.shared.*` — excepciones globales, DTOs base, `SpaForwardController`, Swagger.

## Comandos

- `./mvnw spring-boot:run` — arranca el backend en local.
- `./mvnw test` — ejecuta todos los tests (unit + integration).
- `./mvnw test -DexcludedGroups=integration` — solo tests unitarios.
- `ng serve` — arranca el frontend en local (puerto 4200).
- `ng build --configuration production` — build de producción del frontend.
- `docker compose up --build` — levanta todo (PostgreSQL + backend + frontend servido por Spring Boot).

## Modelo de datos / dominio

- **Usuario** — id, nombre, email (único), contraseña hasheada, rol (`ADMIN`/`USER`), activo, creado.
- **Recurso** — id, nombre, tipo (`SALA`/`PROYECTOR`/`VEHICULO`/`OTRO`), descripción, capacidad, activo.
- **Reserva** — id, recurso (FK), usuario (FK), fechaInicio, fechaFin, estado (`CONFIRMADA`/`CANCELADA`/`MODIFICADA`), version (optimistic locking).
- **SolicitudCola** — id, recurso (FK), usuario (FK), estado (`PENDIENTE`/`NOTIFICADA`/`CANCELADA`), creado.
- **HistorialReserva** — id, reserva (FK), usuario (FK), tipoEvento, descripción, creado.
- **Invariante crítico:** No pueden existir dos `Reserva` con el mismo `recurso_id` y rangos de fecha solapados cuando ambas estén en estado `CONFIRMADA`.

## Convenciones

- **Idioma:** Código fuente, comentarios y documentación en español.
- **Nombres:** camelCase para variables y métodos; PascalCase para clases; UPPER_SNAKE para constantes.
- **API REST:** URLs en plural (`/api/v1/recursos`, `/api/v1/reservas`), versionado por prefijo (`/api/v1/`).
- **Respuestas:** DTOs inmutables (`record` Java) por caso de uso (no exponer entidades JPA).
- **Manejo de errores:** `@ControllerAdvice` global con `ResponseEntity` estructurado (`{ error, message, status, timestamp }`).
- **Validación:** Jakarta Validation (`@Valid`, `@NotBlank`, etc.) en DTOs de entrada.
- **Pruebas:** Unitarias con Mockito para servicios; `@DataJpaTest` para repositorios; `@SpringBootTest` + `@AutoConfigureMockMvc` para controladores. Tests de integración etiquetados con `@Tag("integration")`.
- **Commits:** Convencionales: `tipo(alcance): descripción`.
  Tipos: `feat`, `fix`, `refactor`, `test`, `docs`, `chore`.
  Alcance: `auth`, `recurso`, `reserva`, `cola`, `admin`, `notificacion`, `shared`, `ci`.
- **Clean Code:** Nombres en español, métodos ≤ 20 líneas, early return, sin comentarios.

## Límites duros

- No exponer contraseñas ni datos sensibles en respuestas de la API.
- No subir archivos `.env`, `*.pem`, ni secretos al repositorio.
- No añadir dependencias nuevas sin actualizar este documento.
- No usar consultas nativas SQL si se puede expresar con JPQL o Criteria API.
- No eliminar ni modificar datos de otros usuarios desde endpoints de usuario estándar (solo admin).
