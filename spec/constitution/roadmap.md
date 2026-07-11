# Roadmap

## Hecho ✅

1. **001 · Autenticación y registro** — registro de usuarios, login con JWT y roles Admin/Usuario.
   → `features/001-auth/`
   - Además: seed admin configurable (`application.yaml`), endpoint `PUT /api/v1/auth/usuarios/{id}/rol` para promover usuarios.
2. **002 · Gestión de recursos** — CRUD de recursos con visibilidad según permisos.
   → `features/002-recursos/`
3. **003 · Motor de reservas** — creación/modificación/cancelación de reservas con detección de solapamientos, JPQL, `@Version` para optimistic locking, índices compuestos, arquitectura hexagonal.
   → `features/003-motor-reservas/`
4. **004 · Notificaciones por correo** — envío automático de notificaciones (confirmación, modificación, cancelación) con Spring Mail + Thymeleaf + @Async.
   → `features/004-notificaciones/`
5. **005 · Panel de administración** — métricas (dashboard), listado de usuarios y gestión de reservas con filtros.
   → `features/005-admin-panel/`
6. **006 · Historial de reservas** — auditoría de eventos (CREACION, MODIFICACION, CANCELACION) con consulta vía API.
   → `features/006-historial-reservas/`
7. **007 · Paginación** — endpoints paginados con `@PageableDefault(size = 20)`, `Specification` para filtros en SQL.
   → `features/007-paginacion/`
8. **008 · Cola de espera** — registro en cola cuando un recurso está ocupado, notificación automática al cancelarse una reserva.
   → `features/008-cola-espera/`

## En desarrollo 🚧

9. **009 · Frontend Angular** — SPA con Angular Material + Tailwind.
   → Próximamente en repositorio aparte

## Backlog / ideas 💡

- Reservas recurrentes — permitir reservas diarias/semanales/mensuales.
- Imagen de recurso — subir foto del recurso (sala, proyector, etc.).
- Refresh tokens — mejorar seguridad del JWT.
- Exportación de datos (CSV, PDF) desde admin.

> Cada feature nueva se crea como `features/NNN-nombre-feature/` con `spec.md`, `plan.md` y `tasks.md` antes de tocar código.
