# 002 · Gestión de recursos — Tareas

- [ ] Crear spec, plan y tasks (`spec/features/002-recursos/`).
- [ ] Implementar `TipoRecurso.java` (enum).
- [ ] Implementar `Recurso.java` (record de dominio).
- [ ] Implementar `CrearRecursoRequest`, `ActualizarRecursoRequest`, `RecursoResponse`.
- [ ] Implementar `RecursoService` (interface, puerto de entrada).
- [ ] Implementar `RecursoRepository` (interface, puerto de salida).
- [ ] Implementar `RecursoServiceImpl` con validaciones.
- [ ] Implementar `RecursoController` con GET, POST, PUT, DELETE.
- [ ] Implementar `RecursoEntity` (JPA).
- [ ] Implementar `JpaRecursoRepository`.
- [ ] Implementar `RecursoEntityMapper`.
- [ ] Implementar `RecursoRepositoryAdapter`.
- [ ] Actualizar `SecurityConfig` con reglas de `/api/v1/recursos/**`.
- [ ] Escribir tests unitarios (`RecursoServiceTest`).
- [ ] Escribir tests de integración (`RecursoRepositoryTest`).
- [ ] Escribir tests de controller (`RecursoControllerTest`).
- [ ] Validar contra los criterios de aceptación de `spec.md`.
- [ ] Mover la feature a "Hecho" en `../../constitution/roadmap.md`.
