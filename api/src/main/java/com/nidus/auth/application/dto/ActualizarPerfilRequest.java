package com.nidus.auth.application.dto;

public record ActualizarPerfilRequest(
    String nombre,
    String email,
    String password,
    String currentPassword
) {}