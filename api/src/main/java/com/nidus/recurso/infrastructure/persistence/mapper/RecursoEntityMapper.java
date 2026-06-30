package com.nidus.recurso.infrastructure.persistence.mapper;

import com.nidus.recurso.domain.Recurso;
import com.nidus.recurso.infrastructure.persistence.entity.RecursoEntity;
import org.springframework.stereotype.Component;

@Component
public class RecursoEntityMapper {

    public RecursoEntity toEntity(Recurso domain) {
        if (domain == null) return null;

        var entity = new RecursoEntity(
                domain.getNombre(),
                domain.getTipo(),
                domain.getDescripcion(),
                domain.getCapacidad()
        );
        entity.setId(domain.getId());
        entity.setActivo(domain.isActivo());
        return entity;
    }

    public Recurso toDomain(RecursoEntity entity) {
        if (entity == null) return null;

        var domain = new Recurso(
                entity.getNombre(),
                entity.getTipo(),
                entity.getDescripcion(),
                entity.getCapacidad()
        );
        domain.setId(entity.getId());
        domain.setActivo(entity.isActivo());
        return domain;
    }
}
