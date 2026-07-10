package com.nidus.auth.application.dto;

public record UserResponse(
    Long id,
    String nombre,
    String email,
    String rol,
    boolean activo
) {}