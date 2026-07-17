package com.nidus.cola.infrastructure.persistence.mapper;

import com.nidus.cola.domain.SolicitudCola;
import com.nidus.cola.infrastructure.persistence.entity.SolicitudColaEntity;
import org.springframework.stereotype.Component;

@Component
public class SolicitudColaMapper {

    public SolicitudCola toDomain(SolicitudColaEntity entity) {
        if (entity == null) return null;
        return new SolicitudCola(
            entity.getId(), entity.getRecursoId(), entity.getUsuarioId(),
            entity.getEstado(), entity.getCreado());
    }

    public SolicitudColaEntity toEntity(SolicitudCola domain) {
        if (domain == null) return null;
        var entity = new SolicitudColaEntity();
        entity.setId(domain.id());
        entity.setRecursoId(domain.recursoId());
        entity.setUsuarioId(domain.usuarioId());
        entity.setEstado(domain.estado());
        entity.setCreado(domain.creado());
        return entity;
    }
}
