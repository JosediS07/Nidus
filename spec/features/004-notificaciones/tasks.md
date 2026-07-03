# 004 · Notificaciones por correo — Tareas

- [x] Crear spec, plan y tasks (`spec/features/004-notificaciones/`).
- [x] Agregar dependencias `spring-boot-starter-mail` y `spring-boot-starter-thymeleaf` en `pom.xml`.
- [x] Agregar configuración `app.mail` en `application.yaml`.
- [x] Implementar `EmailConfig.java` (bean JavaMailSender + @EnableAsync).
- [x] Implementar `NotificacionPort.java` (interface con 3 métodos).
- [x] Crear plantillas Thymeleaf: `confirmacion.html`, `cancelacion.html`, `modificacion.html`.
- [x] Implementar `EmailNotificacionAdapter.java` (async, Thymeleaf, JavaMailSender).
- [x] Inyectar `NotificacionPort` en `ReservaServiceImpl` y disparar tras cada operación.
- [x] Escribir tests del adaptador (`EmailNotificacionAdapterTest`).
- [x] Actualizar `ReservaServiceImplTest` para verificar que llama al puerto.
- [x] Validar contra los criterios de aceptación de `spec.md`.
- [x] Mover la feature a "Hecho" en `../../constitution/roadmap.md`.
