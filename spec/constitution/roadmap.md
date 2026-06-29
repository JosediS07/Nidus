# Roadmap

## Hecho ✅

_(Aún no hay features completadas)_

## Siguiente 🔜

1. **001 · Autenticación y registro** — registro de usuarios, login con JWT y roles Admin/Usuario.
   → `features/001-auth/`
2. **002 · Gestión de recursos** — CRUD de recursos con visibilidad según permisos.
   → `features/002-recursos/`
3. **003 · Motor de reservas** — calendario, creación/modificación/cancelación de reservas con control de conflictos.
4. **004 · Notificaciones por correo** — envío automático de confirmaciones.
5. **005 · Panel de administración** — métricas, gestión de usuarios y reservas.

## Backlog / ideas 💡

- Historial de reservas — registro de auditoría con todos los cambios sobre una reserva.
- Reservas recurrentes — permitir reservas diarias/semanales/mensuales.
- Cola de espera — notificar cuando un recurso solicitado se libere.
- Imagen de recurso — subir foto del recurso (sala, proyector, etc.).

> Cada feature nueva se crea como `features/NNN-nombre-feature/` con `spec.md`, `plan.md` y `tasks.md` antes de tocar código.
