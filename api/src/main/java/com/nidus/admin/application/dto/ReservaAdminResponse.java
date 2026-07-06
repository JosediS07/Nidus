package com.nidus.admin.application.dto;

import java.time.LocalDateTime;

public record ReservaAdminResponse(
    Long id,
    Long recursoId,
    Long usuarioId,
    LocalDateTime fechaInicio,
    LocalDateTime fechaFin,
    String estado
) {}
