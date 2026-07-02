package com.nidus.reserva.infrastructure.persistence.mapper;

import com.nidus.reserva.domain.Reserva;
import com.nidus.reserva.infrastructure.persistence.entity.ReservaEntity;
import org.springframework.stereotype.Component;

@Component
public class ReservaEntityMapper {

    public ReservaEntity toEntity(Reserva domain) {
        return new ReservaEntity(
            domain.getId(),
            domain.getRecursoId(),
            domain.getUsuarioId(),
            domain.getFechaInicio(),
            domain.getFechaFin(),
            domain.getEstado(),
            domain.getVersion()
        );
    }

    public Reserva toDomain(ReservaEntity entity) {
        return new Reserva(
            entity.getId(),
            entity.getRecursoId(),
            entity.getUsuarioId(),
            entity.getFechaInicio(),
            entity.getFechaFin(),
            entity.getEstado(),
            entity.getVersion()
        );
    }
}
