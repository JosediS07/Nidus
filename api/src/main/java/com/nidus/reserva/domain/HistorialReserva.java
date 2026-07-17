package com.nidus.reserva.domain;

import java.time.LocalDateTime;

public record HistorialReserva(
    Long id,
    Long reservaId,
    Long usuarioId,
    String tipoEvento,
    String descripcion,
    LocalDateTime creado
) {}