package com.nidus.recurso.application.port.output;

import com.nidus.recurso.domain.Recurso;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface RecursoRepository {
    Page<Recurso> findAllActivos(Pageable pageable);
    Optional<Recurso> findById(Long id);
    Recurso save(Recurso recurso);
}
