package com.nidus.auth.application.dto;

public record AuthResponse(
    String token,
    String nombre,
    String email,
    String rol
) {}
