# 004 · Notificaciones por correo — Tareas

- [x] Crear spec, plan y tasks (`spec/features/004-notificaciones/`).
- [ ] Agregar dependencias `spring-boot-starter-mail` y `spring-boot-starter-thymeleaf` en `pom.xml`.
- [ ] Agregar configuración `app.mail` en `application.yaml`.
- [ ] Implementar `EmailConfig.java` (bean JavaMailSender + @EnableAsync).
- [ ] Implementar `NotificacionPort.java` (interface con 3 métodos).
- [ ] Crear plantillas Thymeleaf: `confirmacion.html`, `cancelacion.html`, `modificacion.html`.
- [ ] Implementar `EmailNotificacionAdapter.java` (async, Thymeleaf, JavaMailSender).
- [ ] Inyectar `NotificacionPort` en `ReservaServiceImpl` y disparar tras cada operación.
- [ ] Escribir tests del adaptador (`EmailNotificacionAdapterTest`).
- [ ] Actualizar `ReservaServiceImplTest` para verificar que llama al puerto.
- [ ] Validar contra los criterios de aceptación de `spec.md`.
- [ ] Mover la feature a "Hecho" en `../../constitution/roadmap.md`.
