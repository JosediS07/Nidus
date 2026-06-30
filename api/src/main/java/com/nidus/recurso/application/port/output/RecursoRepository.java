package com.nidus.recurso.application.port.output;

import com.nidus.recurso.domain.Recurso;

import java.util.List;
import java.util.Optional;

public interface RecursoRepository {
    List<Recurso> findAllActivos();
    Optional<Recurso> findById(Long id);
    Recurso save(Recurso recurso);
}
