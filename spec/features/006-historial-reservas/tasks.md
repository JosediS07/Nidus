# 006 · Historial de reservas — Tareas

- [x] Crear feature docs (spec, plan, tasks).
- [x] Crear `ReservaEvento` (record) en `reserva.domain.evento`.
- [x] Crear `HistorialReservaEntity` (JPA entity) con tabla `historial_reservas`.
- [x] Crear `JpaHistorialReservaRepository`.
- [x] Crear `HistorialReservaService` con método para guardar y consultar.
- [x] Modificar `ReservaServiceImpl` para publicar eventos tras cada operación.
- [x] Añadir endpoint `GET /api/v1/admin/reservas/{id}/historial`.
- [x] Añadir test del endpoint en `AdminControllerTest`.
- [x] Ejecutar `mvnw test` - 92 tests pasan (0 fallos, 0 errores).
- [x] Actualizar Postman collection y roadmap.
