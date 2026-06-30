package com.nidus.recurso.application.dto;

import com.nidus.recurso.domain.TipoRecurso;

public record RecursoResponse(
    Long id,
    String nombre,
    TipoRecurso tipo,
    String descripcion,
    Integer capacidad,
    boolean activo
) {}
