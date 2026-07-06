# 005 · Panel de administración — Plan

## Enfoque

Módulo ligero que centraliza consultas de administración. No tiene dominio propio porque solo agrega datos de los módulos existentes (`auth`, `recurso`, `reserva`). Usa los repositorios JPA directamente desde el servicio.

## Implementación

1. **DTOs** — `DashboardResponse`, `UsuarioAdminResponse`, `ReservaAdminResponse`.
2. **Servicio** — `AdminService` que inyecta `UserRepository`, `JpaRecursoRepository` y `JpaReservaRepository` para construir las métricas y listados.
3. **Controlador** — `AdminController` con `@RequestMapping("/api/v1/admin")` y `@PreAuthorize("hasRole('ADMIN')")` a nivel de clase.
4. **Tests** — Unitario del servicio y `@SpringBootTest` con MockMvc del controlador.

## Decisiones

- **Sin puertos (ports)** — No se definen puertos de entrada/salida explícitos porque el admin no tiene lógica de dominio propia. Se inyectan repositorios directamente.
- **Filtros vía query params** — Se usa `@RequestParam` opcional con `Optional<>` para construir la query de forma dinámica.
- **Un solo servicio** — No se separa en dashboard/usuarios/reservas por simplicidad; el módulo es pequeño.

## Riesgos

- **Rendimiento en dashboard** — La consulta de métricas hace varias queries SQL. Con muchos datos podría ralentizarse. Mitigación: por ahora son pocos datos; si escala se añade paginación o una tabla de caché.
