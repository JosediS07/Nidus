package com.nidus.cola.application.dto;

import java.time.LocalDateTime;

public record SolicitudColaResponse(
    Long id,
    Long recursoId,
    String estado,
    LocalDateTime creado
) {}
