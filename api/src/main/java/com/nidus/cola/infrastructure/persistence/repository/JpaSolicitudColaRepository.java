package com.nidus.cola.infrastructure.persistence.repository;

import com.nidus.cola.domain.EstadoSolicitud;
import com.nidus.cola.infrastructure.persistence.entity.SolicitudColaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JpaSolicitudColaRepository extends JpaRepository<SolicitudColaEntity, Long> {

    Page<SolicitudColaEntity> findByUsuarioIdOrderByCreadoDesc(Long usuarioId, Pageable pageable);

    Page<SolicitudColaEntity> findAllByOrderByCreadoDesc(Pageable pageable);

    Optional<SolicitudColaEntity> findFirstByRecursoIdAndEstadoOrderByCreadoAsc(Long recursoId, EstadoSolicitud estado);
}
