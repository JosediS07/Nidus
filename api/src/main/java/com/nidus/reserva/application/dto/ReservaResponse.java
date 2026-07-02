package com.nidus.reserva.application.dto;

import com.nidus.reserva.domain.EstadoReserva;
import java.time.LocalDateTime;

public record ReservaResponse(
    Long id,
    Long recursoId,
    Long usuarioId,
    LocalDateTime fechaInicio,
    LocalDateTime fechaFin,
    EstadoReserva estado
) {}
