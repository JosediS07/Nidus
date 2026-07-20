package com.nidus.reserva.application.dto;

import java.time.LocalDateTime;

public record ReservaResponse(
    Long id,
    Long recursoId,
    Long usuarioId,
    LocalDateTime fechaInicio,
    LocalDateTime fechaFin,
    String estado,
    String usuarioNombre,
    String recursoNombre
) {}
