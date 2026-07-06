package com.nidus.admin.application.dto;

import java.time.LocalDateTime;

public record UsuarioAdminResponse(
    Long id,
    String nombre,
    String email,
    String rol,
    boolean activo,
    LocalDateTime creado
) {}
