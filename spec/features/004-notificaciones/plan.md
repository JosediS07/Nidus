# 004 · Notificaciones por correo — Plan

## Enfoque

Añadir un puerto de salida `NotificacionPort` que la implementación `EmailNotificacionAdapter` resuelve con JavaMailSender + Thymeleaf. El adaptador se inyecta en `ReservaServiceImpl` para disparar el envío tras cada operación exitosa. El envío es asíncrono con `@Async`.

## Implementación

1. **Spec** — Crear `spec.md`, `plan.md` y `tasks.md`.
2. **Dependencias y configuración** — Agregar `spring-boot-starter-mail`, `spring-boot-starter-thymeleaf` al `pom.xml`. Agregar bloque `app.mail` en `application.yaml`.
3. **Puerto de salida** — `NotificacionPort.java` con métodos `enviarConfirmacion`, `enviarModificacion`, `enviarCancelacion`.
4. **Adaptador** — `EmailNotificacionAdapter.java` con `@Async`, JavaMailSender y Thymeleaf `SpringTemplateEngine`.
5. **Plantillas** — `confirmacion.html`, `cancelacion.html`, `modificacion.html` en `templates/email/`.
6. **Config** — `EmailConfig.java` con `@Bean` de JavaMailSender y `@EnableAsync`.
7. **Integración** — Inyectar `NotificacionPort` en `ReservaServiceImpl` y llamar tras cada operación.
8. **Pruebas** — Test unitario del adaptador con `MockJavaMailSender`; spy en `ReservaServiceImplTest` para verificar llamadas.

## Decisiones

- **Async (`@Async`)** — Se usa para no bloquear la respuesta HTTP. Los errores de envío se loguean pero no se propagan.
- **Thymeleaf** — Se usa para generar HTML de los correos. Spring Boot ya lo integra con `SpringTemplateEngine`.
- **Try-catch en el adaptador** — Cualquier excepción de envío se captura y loguea para no afectar la operación principal.

## Riesgos

- **SMTP no disponible** — El email no se entrega pero la reserva se crea igual. Mitigación: log de error y el usuario puede ver su reserva en la plataforma.
- **Rendimiento del template engine** — Thymeleaf es rápido, pero si el volumen es alto puede haber contención. Mitigación: el envío ya es async, y Thymeleaf tiene cache de templates por defecto.
