package com.nidus.reserva.domain.evento;

public record ReservaEvento(
    String tipo,
    Long reservaId,
    Long usuarioId,
    String descripcion
) {

    public static final String CREACION = "CREACION";
    public static final String MODIFICACION = "MODIFICACION";
    public static final String CANCELACION = "CANCELACION";
}
