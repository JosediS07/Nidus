# 006 · Historial de reservas — Plan

## Enfoque

Usar el sistema de eventos de Spring (`ApplicationEventPublisher`). El servicio de reservas publica un evento después de cada operación exitosa. Un listener separado persiste el evento en la tabla `historial_reservas`. Esto mantiene el `ReservaServiceImpl` limpio y el historial es un componente desacoplado.

## Implementación

1. **Evento** — `ReservaEvento` (record) con: tipo, reservaId, usuarioId, descripción.
2. **Entidad** — `HistorialReservaEntity` con: id, reservaId, usuarioId, tipoEvento, descripcion, creado.
3. **Repositorio** — `JpaHistorialReservaRepository` con método `findByReservaIdOrderByCreadoDesc`.
4. **Servicio** — `HistorialReservaService` que publica el evento y tiene métodos para guardar y consultar.
5. **Modificar** `ReservaServiceImpl` para inyectar `ApplicationEventPublisher` y llamar a `publishEvent` tras crear, modificar y cancelar.
6. **Endpoint** — Añadir `GET /api/v1/admin/reservas/{id}/historial` en `AdminController` + `AdminService`.
7. **Tests** — Unitario del historial, test del endpoint admin.

## Decisiones

- **Eventos síncronos** — Se usa `@TransactionalEventListener(phase = AFTER_COMMIT)` para que el historial se persista solo si la transacción principal se completa con éxito. Si el historial falla, la operación original no se revierte.
- **Misma tabla de BD** — El historial está en su propia tabla (`historial_reservas`), independiente de `reservas`. No se toca el esquema existente.

## Riesgos

- **Rendimiento** — Cada operación de reserva hace ahora 2 inserts (reserva + historial). Impacto despreciable para el volumen actual.
