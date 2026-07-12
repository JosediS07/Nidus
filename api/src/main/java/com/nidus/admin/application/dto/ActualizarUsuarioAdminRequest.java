package com.nidus.admin.application.dto;

public record ActualizarUsuarioAdminRequest(
    String nombre,
    String email,
    Boolean activo
) {}
