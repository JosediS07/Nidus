package com.nidus.recurso.application.dto;

import com.nidus.recurso.domain.TipoRecurso;

public record ActualizarRecursoRequest(
    String nombre,
    TipoRecurso tipo,
    String descripcion,
    Integer capacidad
) {}
