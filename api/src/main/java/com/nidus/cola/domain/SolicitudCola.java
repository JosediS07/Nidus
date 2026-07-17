package com.nidus.cola.domain;

import java.time.LocalDateTime;

public record SolicitudCola(
    Long id,
    Long recursoId,
    Long usuarioId,
    EstadoSolicitud estado,
    LocalDateTime creado
) {
    public SolicitudCola conEstado(EstadoSolicitud nuevoEstado) {
        return new SolicitudCola(id, recursoId, usuarioId, nuevoEstado, creado);
    }
}
