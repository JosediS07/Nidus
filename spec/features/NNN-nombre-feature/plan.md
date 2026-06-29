# NNN · <Nombre de la feature> — Plan

## Enfoque

<Estrategia general. Ej: "La lógica de control de conflictos vive en `BookingService` y consulta el repositorio con una query JPQL que detecta solapamientos antes de insertar.">

## Implementación

1. <Paso — módulo de dominio y archivo. Ej: "Crear `Booking` entity en `com.nidus.booking.model`">
2. <Paso — archivo/módulo.>
3. <Paso — archivo/módulo.>

## Decisiones

- **<Decisión>** — <por qué; qué se descartó>.

## Riesgos

- **<Riesgo>** — <mitigación. Ej: "Condición de carrera en reservas simultáneas → usar `@Version` (optimistic lock) en Booking.">
