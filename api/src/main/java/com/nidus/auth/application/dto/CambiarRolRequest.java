package com.nidus.auth.application.dto;

import com.nidus.auth.domain.Role;
import jakarta.validation.constraints.NotNull;

public record CambiarRolRequest(
    @NotNull Role rol
) {}
