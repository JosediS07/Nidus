package com.nidus.admin.application.dto;

import com.nidus.auth.domain.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CrearUsuarioAdminRequest(
    @NotBlank String nombre,
    @NotBlank @Email String email,
    @NotBlank @Size(min = 6) String password,
    @NotNull Role rol
) {}
