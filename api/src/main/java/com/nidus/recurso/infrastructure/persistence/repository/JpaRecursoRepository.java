package com.nidus.recurso.infrastructure.persistence.repository;

import com.nidus.recurso.infrastructure.persistence.entity.RecursoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JpaRecursoRepository extends JpaRepository<RecursoEntity, Long> {
    List<RecursoEntity> findByActivoTrue();
}
