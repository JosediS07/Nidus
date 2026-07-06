# Roadmap

## Hecho ✅

1. **001 · Autenticación y registro** — registro de usuarios, login con JWT y roles Admin/Usuario.
   → `features/001-auth/`
2. **002 · Gestión de recursos** — CRUD de recursos con visibilidad según permisos.
   → `features/002-recursos/`
   - Además: seed admin configurable (`application.yaml`), endpoint `PUT /api/v1/auth/usuarios/{id}/rol` para promover usuarios.
3. **003 · Motor de reservas** — creación/modificación/cancelación de reservas con detección de solapamientos, JPQL, `@Version` para optimistic locking, índices compuestos, arquitectura hexagonal.
   → `features/003-motor-reservas/`
4. **004 · Notificaciones por correo** — envío automático de notificaciones (confirmación, modificación, cancelación) con Spring Mail + Thymeleaf + @Async.
   → `features/004-notificaciones/`
5. **005 · Panel de administración** — métricas (dashboard), listado de usuarios y gestión de reservas con filtros.
   → `features/005-admin-panel/`

## Backlog / ideas 💡

- Historial de reservas — registro de auditoría con todos los cambios sobre una reserva.
- Reservas recurrentes — permitir reservas diarias/semanales/mensuales.
- Cola de espera — notificar cuando un recurso solicitado se libere.
- Imagen de recurso — subir foto del recurso (sala, proyector, etc.).

> Cada feature nueva se crea como `features/NNN-nombre-feature/` con `spec.md`, `plan.md` y `tasks.md` antes de tocar código.
