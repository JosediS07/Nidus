# 004 · Notificaciones por correo

**Estado:** pendiente

## Qué hace

Cuando un usuario crea, modifica o cancela una reserva, el sistema envía un email de confirmación al correo del usuario. El envío es asíncrono para no afectar el tiempo de respuesta de la API.

## Por qué

El usuario necesita confirmación fehaciente de que su reserva fue procesada. Sin notificación por correo la plataforma carece de trazabilidad para el usuario final.

## Criterios de aceptación

- [ ] Al crear una reserva (`POST /api/v1/reservas`), se envía un email de confirmación al usuario autenticado.
- [ ] Al modificar una reserva (`PUT /api/v1/reservas/{id}`), se envía un email de modificación al usuario autenticado.
- [ ] Al cancelar una reserva (`DELETE /api/v1/reservas/{id}`), se envía un email de cancelación al usuario autenticado.
- [ ] El envío es asíncrono (`@Async`) — la respuesta HTTP no espera a que el correo se envíe.
- [ ] Los emails contienen: tipo de operación, datos de la reserva (recurso, fechas) y estado actual.
- [ ] La configuración SMTP se lee de `application.yaml` con variables de entorno.
- [ ] Si el servidor SMTP no está disponible, la operación de reserva no falla (el error se loguea silenciosamente).

## Fuera de alcance

- Historial de notificaciones o tabla de base de datos.
- Notificaciones push, SMS u otros canales.
- Plantillas editables por el usuario.
- Reintentos programados ante fallo de envío.
