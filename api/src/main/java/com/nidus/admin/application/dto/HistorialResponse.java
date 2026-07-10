package com.nidus.admin.application.dto;

import java.time.LocalDateTime;

public record HistorialResponse(
    Long id,
    Long reservaId,
    Long usuarioId,
    String tipoEvento,
    String descripcion,
    LocalDateTime creado
) {}