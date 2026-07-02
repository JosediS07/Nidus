# 003 · Motor de reservas

**Estado:** en curso

## Qué hace

Los usuarios autenticados pueden crear, modificar y cancelar reservas sobre recursos disponibles. El sistema impide solapamientos de horario en un mismo recurso. Los administradores pueden ver y gestionar cualquier reserva.

## Por qué

Las reservas son el núcleo de la plataforma. Sin un motor de reservas con control de conflictos no hay valor real para el usuario.

## Criterios de aceptación

- [ ] `POST /api/v1/reservas` crea una reserva si el recurso está disponible en el rango horario. Responde `201 Created`.
- [ ] Si el rango ya está ocupado (solapamiento con otra reserva CONFIRMADA), responde `409 Conflict`.
- [ ] `GET /api/v1/reservas` devuelve las reservas del usuario autenticado.
- [ ] `GET /api/v1/reservas/todas` devuelve todas las reservas (solo ADMIN).
- [ ] `GET /api/v1/reservas/{id}` devuelve una reserva por ID (propia o ADMIN).
- [ ] `PUT /api/v1/reservas/{id}` modifica una reserva existente validando solapamientos.
- [ ] `DELETE /api/v1/reservas/{id}` cancela una reserva (cambia estado a CANCELADA).
- [ ] Cancelar una reserva ya cancelada responde `409 Conflict`.
- [ ] Las fechas deben ser futuras (`fechaInicio` y `fechaFin` posteriores a ahora).
- [ ] `fechaFin` debe ser posterior a `fechaInicio`.

## Fuera de alcance

- Notificaciones por correo (feature 004).
- Reservas recurrentes.
- Historial de cambios / auditoría.
- Paginación en listados.
