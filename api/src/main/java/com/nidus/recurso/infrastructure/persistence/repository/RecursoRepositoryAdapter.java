package com.nidus.recurso.infrastructure.persistence.repository;

import com.nidus.recurso.application.port.output.RecursoRepository;
import com.nidus.recurso.domain.Recurso;
import com.nidus.recurso.infrastructure.persistence.mapper.RecursoEntityMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class RecursoRepositoryAdapter implements RecursoRepository {

    private final JpaRecursoRepository jpaRecursoRepository;
    private final RecursoEntityMapper mapper;

    public RecursoRepositoryAdapter(JpaRecursoRepository jpaRecursoRepository, RecursoEntityMapper mapper) {
        this.jpaRecursoRepository = jpaRecursoRepository;
        this.mapper = mapper;
    }

    @Override
    public List<Recurso> findAllActivos() {
        return jpaRecursoRepository.findByActivoTrue().stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public Optional<Recurso> findById(Long id) {
        return jpaRecursoRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Recurso save(Recurso recurso) {
        var entity = mapper.toEntity(recurso);
        var saved = jpaRecursoRepository.save(entity);
        return mapper.toDomain(saved);
    }
}
