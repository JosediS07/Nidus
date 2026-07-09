package com.nidus.reserva.domain.evento;

import java.time.LocalDateTime;

public record ReservaEvento(
    String tipo,
    Long reservaId,
    Long usuarioId,
    Long recursoId,
    LocalDateTime fechaInicio,
    LocalDateTime fechaFin,
    String descripcion
) {

    public static final String CREACION = "CREACION";
    public static final String MODIFICACION = "MODIFICACION";
    public static final String CANCELACION = "CANCELACION";
}
