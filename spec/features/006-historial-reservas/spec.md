# 006 · Historial de reservas

**Estado:** en curso

## Qué hace

Cada vez que se crea, modifica o cancela una reserva, queda registrado un evento de auditoría. Los administradores pueden consultar el historial de cambios de cualquier reserva.

## Por qué

Sin historial no hay trazabilidad. Si un usuario modifica una reserva o la cancela, no hay forma de saber qué pasó ni quién lo hizo. Esto es crítico para soporte y auditoría.

## Criterios de aceptación

- [ ] Al crear una reserva se guarda un evento tipo `CREACION`.
- [ ] Al modificar una reserva se guarda un evento tipo `MODIFICACION`.
- [ ] Al cancelar una reserva se guarda un evento tipo `CANCELACION`.
- [ ] `GET /api/v1/admin/reservas/{id}/historial` devuelve los eventos ordenados por fecha descendente.
- [ ] Si la reserva no existe, responde `404 Not Found`.
- [ ] El endpoint solo es accesible para ADMIN (403 si no lo es).
- [ ] La publicación de eventos no afecta el flujo normal: si falla el guardado del historial, la operación original no se revierte.

## Fuera de alcance

- Frontend.
- Historial de cambios en usuarios o recursos.
- Exportación del historial.
