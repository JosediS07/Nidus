# 003 · Motor de reservas — Tareas

- [x] Crear spec, plan y tasks (`spec/features/003-motor-reservas/`).
- [x] Implementar `EstadoReserva.java` (enum).
- [x] Implementar `Reserva.java` (modelo de dominio con `@Version`).
- [x] Implementar `CrearReservaRequest`, `ModificarReservaRequest`, `ReservaResponse`.
- [x] Implementar `ReservaService` (interface, puerto de entrada).
- [x] Implementar `ReservaRepository` (interface, puerto de salida).
- [x] Implementar `ReservaServiceImpl` con detección de solapamientos.
- [x] Implementar `ReservaEntity` (JPA con `@Version` e índices).
- [x] Implementar `JpaReservaRepository` con JPQL de solapamiento.
- [x] Implementar `ReservaEntityMapper`.
- [x] Implementar `ReservaRepositoryAdapter`.
- [x] Implementar `ReservaController` con GET, POST, PUT, DELETE.
- [x] Escribir tests unitarios (`ReservaServiceTest`).
- [x] Escribir tests de integración (`ReservaRepositoryTest`).
- [x] Escribir tests de controller (`ReservaControllerTest`).
- [x] Validar contra los criterios de aceptación de `spec.md`.
- [x] Mover la feature a "Hecho" en `../../constitution/roadmap.md`.
