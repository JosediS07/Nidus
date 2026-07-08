package com.nidus.recurso.infrastructure.persistence.repository;

import com.nidus.recurso.infrastructure.persistence.entity.RecursoEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaRecursoRepository extends JpaRepository<RecursoEntity, Long> {
    Page<RecursoEntity> findByActivoTrue(Pageable pageable);
}
