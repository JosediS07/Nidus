# 002 · Gestión de recursos — Plan

## Enfoque

CRUD completo siguiendo la misma arquitectura hexagonal del módulo `auth`. El dominio se modela con un enum `TipoRecurso` y un record `Recurso`. El borrado es lógico (campo `activo`). Las operaciones de escritura se protegen con el rol `ADMIN`.

## Implementación

1. **Dominio** — `TipoRecurso.java` (enum: SALA, PROYECTOR, VEHICULO, OTRO) y `Recurso.java` (record inmutable con id, nombre, tipo, descripcion, capacidad, activo).
2. **DTOs** — `CrearRecursoRequest`, `ActualizarRecursoRequest`, `RecursoResponse` (record + Jakarta Validation).
3. **Puertos** — `RecursoService` (input), `RecursoRepository` (output).
4. **Servicio** — `RecursoServiceImpl` con validaciones: nombre obligatorio, tipo obligatorio, recurso no encontrado (404), recurso ya inactivo (409).
5. **Controlador** — `RecursoController` con `@RequestMapping("/api/v1/recursos")`.
6. **Persistencia** — `RecursoEntity`, `JpaRecursoRepository`, `RecursoRepositoryAdapter`, `RecursoEntityMapper`.
7. **Seguridad** — Añadir reglas en `SecurityConfig` para `/api/v1/recursos/**`.
8. **Tests** — Unitario del servicio, `@DataJpaTest` del repositorio, `@SpringBootTest` con `MockMvc` del controlador.

## Decisiones

- **Borrado lógico** — Se usa `activo = false` en lugar de DELETE físico para mantener integridad referencial con reservas futuras.
- **GET incluye inactivos por ID** — Para permitir al admin ver recursos desactivados; el listado general solo devuelve activos.
- **Misma estructura que auth** — Se replica el patrón de paquetes hexagonal para mantener consistencia.

## Riesgos

- **Inconsistencia con auth** — Si `SecurityConfig` no se actualiza correctamente, los endpoints quedarán bloqueados o abiertos. Mitigación: actualizar la config y verificar con tests de controller.
