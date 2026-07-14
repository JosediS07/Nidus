package com.nidus.reserva.infrastructure.persistence.mapper;

import com.nidus.reserva.domain.Reserva;
import com.nidus.reserva.infrastructure.persistence.entity.ReservaEntity;
import org.springframework.stereotype.Component;

@Component
public class ReservaEntityMapper {

    public ReservaEntity toEntity(Reserva dominio) {
        return new ReservaEntity(
            dominio.getId(),
            dominio.getRecursoId(),
            dominio.getUsuarioId(),
            dominio.getFechaInicio(),
            dominio.getFechaFin(),
            dominio.getEstado(),
            dominio.getVersion()
        );
    }

    public Reserva toDomain(ReservaEntity entidad) {
        return new Reserva(
            entidad.getId(),
            entidad.getRecursoId(),
            entidad.getUsuarioId(),
            entidad.getFechaInicio(),
            entidad.getFechaFin(),
            entidad.getEstado(),
            entidad.getVersion()
        );
    }
}
