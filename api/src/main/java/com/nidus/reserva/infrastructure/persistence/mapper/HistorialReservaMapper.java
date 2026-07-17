package com.nidus.reserva.infrastructure.persistence.mapper;

import com.nidus.reserva.domain.HistorialReserva;
import com.nidus.reserva.infrastructure.persistence.entity.HistorialReservaEntity;
import org.springframework.stereotype.Component;

@Component
public class HistorialReservaMapper {

    public HistorialReserva toDomain(HistorialReservaEntity entity) {
        if (entity == null) return null;
        return new HistorialReserva(
                entity.getId(), entity.getReservaId(), entity.getUsuarioId(),
                entity.getTipoEvento(), entity.getDescripcion(), entity.getCreado());
    }

    public HistorialReservaEntity toEntity(HistorialReserva domain) {
        if (domain == null) return null;
        return new HistorialReservaEntity(
                domain.reservaId(), domain.usuarioId(),
                domain.tipoEvento(), domain.descripcion());
    }
}
