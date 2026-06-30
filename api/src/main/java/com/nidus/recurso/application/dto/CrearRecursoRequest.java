package com.nidus.recurso.application.dto;

import com.nidus.recurso.domain.TipoRecurso;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CrearRecursoRequest(
    @NotBlank String nombre,
    @NotNull TipoRecurso tipo,
    String descripcion,
    Integer capacidad
) {}
