package com.nidus.auth.dto;

public record AuthResponse(
    String token,
    String nombre,
    String email,
    String rol
) {}
