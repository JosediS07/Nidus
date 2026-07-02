# 003 · Motor de reservas — Plan

## Enfoque

CRUD de reservas con detección de conflictos mediante JPQL. Misma arquitectura hexagonal que `auth` y `recurso`. Se añade `@Version` para optimistic locking e índice compuesto en BD para rendimiento en consultas de solapamiento.

## Implementación

1. **Dominio** — `EstadoReserva.java` (enum: CONFIRMADA, CANCELADA, MODIFICADA) y `Reserva.java` (POJO con id, recursoId, usuarioId, fechaInicio, fechaFin, estado, version).
2. **DTOs** — `CrearReservaRequest`, `ModificarReservaRequest`, `ReservaResponse` (records + Jakarta Validation).
3. **Puertos** — `ReservaService` (input), `ReservaRepository` (output).
4. **Servicio** — `ReservaServiceImpl` con:
   - Validación de fechas futuras y `fechaFin > fechaInicio`.
   - Detección de solapamientos vía repositorio.
   - Solo el dueño o ADMIN puede modificar/cancelar.
5. **Controlador** — `ReservaController` con `@RequestMapping("/api/v1/reservas")`.
6. **Persistencia** — `ReservaEntity` con `@Version`, `@Table(indexes = ...)` e índice compuesto `(recurso_id, fecha_inicio, fecha_fin)`; `JpaReservaRepository` con JPQL de solapamiento; `ReservaRepositoryAdapter`; `ReservaEntityMapper`.
7. **Pruebas** — Unitario del servicio, `@DataJpaTest` del repositorio, `@SpringBootTest` con `MockMvc` del controlador.

## Decisiones

- **Optimistic locking (`@Version`)** — Se usa en vez de bloqueos pesimistas para evitar race conditions sin afectar rendimiento.
- **Índice compuesto** — La query de solapamiento más común filtra por `recurso_id` + rango de fechas. El índice acelera esta consulta.
- **`getReferenceById()`** — Al crear reservas, el adapter usa `getReferenceById()` en lugar de `findById()` para no hacer un SELECT innecesario.

## Riesgos

- **Condiciones de carrera** — Dos peticiones simultáneas en el mismo hueco. Mitigación: `@Version` + reintento en servicio.
- **Rendimiento en listados** — Sin paginación, listados grandes pueden ser lentos. Mitigación: se difiere a futura iteración.
