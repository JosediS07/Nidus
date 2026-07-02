package com.nidus.reserva.application.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record CrearReservaRequest(
    @NotNull(message = "El ID del recurso es obligatorio")
    Long recursoId,

    @NotNull(message = "La fecha de inicio es obligatoria")
    @Future(message = "La fecha de inicio debe ser futura")
    LocalDateTime fechaInicio,

    @NotNull(message = "La fecha de fin es obligatoria")
    @Future(message = "La fecha de fin debe ser futura")
    LocalDateTime fechaFin
) {}
