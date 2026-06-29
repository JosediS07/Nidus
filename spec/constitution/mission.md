# Misión

## Qué construimos

**Nidus** es una plataforma SaaS de reserva y gestión centralizada de recursos. Resuelve el problema de los solapamientos de horarios y la falta de visibilidad al coordinar salas de reuniones, equipos tecnológicos y citas en una organización.

1. **Motor de reservas** — calendario interactivo que permite reservar, modificar y cancelar recursos sin conflictos de horario.
2. **Gestión de recursos** — CRUD completo de recursos (salas, proyectores, vehículos, etc.) con visibilidad según permisos.
3. **Panel de administración** — métricas de uso, gestión global de usuarios y reservas.

## Para quién

- **Usuarios finales** — empleados o miembros de una organización que necesitan reservar recursos de forma rápida y sin fricciones.
- **Administradores** — encargados de gestionar recursos, usuarios y resolver conflictos.
- **Autor del proyecto** — portafolio profesional que demuestra dominio de Spring Boot + React con buenas prácticas.

## Principios

- **Código limpio y seguro** — priorizar legibilidad, buenas prácticas y seguridad en cada endpoint.
- **Sin solapamientos** — la lógica de control de conflictos es crítica y debe ser infalible en la capa de servicio.
- **API-first** — el frontend es un cliente más de la API; toda la lógica de negocio vive en el backend.
- **Eficiencia en persistencia** — consultas JPA optimizadas, evitar N+1, usar transacciones correctamente.

## Qué NO es

- Nidus **no es** un ERP ni un sistema integral de gestión empresarial.
- Nidus **no es** una aplicación de calendario personal (como Google Calendar).
- Nidus **no es** una herramienta de videoconferencia ni incluye comunicación en tiempo real.
