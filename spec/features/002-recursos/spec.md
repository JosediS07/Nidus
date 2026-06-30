# 002 · Gestión de recursos

**Estado:** en curso

## Qué hace

El administrador puede crear, consultar, actualizar y desactivar recursos (salas, proyectores, vehículos, etc.). Cualquier usuario autenticado puede listar y ver el detalle de los recursos activos.

## Por qué

Los recursos son el objeto central de la plataforma. Sin ellos no pueden existir reservas. Es necesario distinguir entre usuarios que solo consultan y administradores que gestionan el catálogo.

## Criterios de aceptación

- [ ] `GET /api/v1/recursos` devuelve la lista de recursos activos (sin paginación inicialmente).
- [ ] `GET /api/v1/recursos/{id}` devuelve un recurso por ID (incluye inactivos, para que el admin pueda verlos).
- [ ] `POST /api/v1/recursos` crea un recurso nuevo. Solo accesible para ADMIN. Valida que `nombre` y `tipo` sean obligatorios.
- [ ] `PUT /api/v1/recursos/{id}` actualiza los campos de un recurso existente. Solo ADMIN. Si el recurso no existe responde `404`.
- [ ] `DELETE /api/v1/recursos/{id}` hace un borrado lógico (`activo = false`). Solo ADMIN. Si ya está inactivo responde `409`.
- [ ] Cualquier endpoint fuera de `GET` rechaza peticiones de usuarios con rol `USER` con `403`.
- [ ] Las respuestas de error siguen el formato `{ error, message, status, timestamp }`.

## Fuera de alcance

- Paginación, filtros y búsqueda.
- Imagen del recurso.
- Categorías o jerarquía de recursos.
- Historial de cambios del recurso.
