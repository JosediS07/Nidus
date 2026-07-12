package com.nidus.cola.application.dto;

import java.time.LocalDateTime;

public record SolicitudColaResponse(
    Long id,
    Long recursoId,
    Long usuarioId,
    String estado,
    LocalDateTime creado
) {}
