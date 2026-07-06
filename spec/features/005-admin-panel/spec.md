# 005 · Panel de administración

**Estado:** en curso

## Qué hace

Los administradores disponen de endpoints exclusivos para consultar métricas del sistema, listar todos los usuarios y acceder a cualquier reserva con filtros avanzados. No hay interfaz gráfica; la funcionalidad se consume vía API.

## Por qué

Sin un panel de administración los administradores no tienen visibilidad del estado del sistema (cuántos usuarios, reservas activas, recursos más usados) ni pueden gestionar usuarios o reservas de forma centralizada.

## Criterios de aceptación

- [ ] `GET /api/v1/admin/dashboard` devuelve métricas: total de usuarios, recursos, reservas, reservas por estado y el recurso más reservado.
- [ ] `GET /api/v1/admin/usuarios` devuelve todos los usuarios del sistema con id, nombre, email, rol y fecha de creación.
- [ ] `GET /api/v1/admin/usuarios/{id}` devuelve un usuario por ID o `404`.
- [ ] `GET /api/v1/admin/reservas` devuelve todas las reservas del sistema (solo ADMIN).
- [ ] `GET /api/v1/admin/reservas` acepta filtros opcionales por query param: `estado`, `recursoId`, `usuarioId`, `fechaInicio`, `fechaFin`.
- [ ] `GET /api/v1/admin/reservas/{id}` devuelve cualquier reserva por ID.
- [ ] Todos los endpoints requieren rol `ADMIN`; si el usuario no es admin responde `403 Forbidden`.

## Fuera de alcance

- Frontend / interfaz gráfica.
- Paginación (se difiere a futura iteración).
- Exportación de datos (CSV, PDF).
