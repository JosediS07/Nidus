package com.nidus.cola.infrastructure.persistence.repository;

import com.nidus.cola.application.port.output.SolicitudColaRepository;
import com.nidus.cola.domain.EstadoSolicitud;
import com.nidus.cola.infrastructure.persistence.entity.SolicitudColaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class SolicitudColaRepositoryAdapter implements SolicitudColaRepository {

    private final JpaSolicitudColaRepository jpaRepository;

    public SolicitudColaRepositoryAdapter(JpaSolicitudColaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public SolicitudColaEntity guardar(SolicitudColaEntity entity) {
        return jpaRepository.save(entity);
    }

    @Override
    public Optional<SolicitudColaEntity> encontrarPorId(Long id) {
        return jpaRepository.findById(id);
    }

    @Override
    public Page<SolicitudColaEntity> encontrarPorUsuarioId(Long usuarioId, Pageable pageable) {
        return jpaRepository.findByUsuarioIdOrderByCreadoDesc(usuarioId, pageable);
    }

    @Override
    public Page<SolicitudColaEntity> encontrarTodas(Pageable pageable) {
        return jpaRepository.findAllByOrderByCreadoDesc(pageable);
    }

    @Override
    public Optional<SolicitudColaEntity> encontrarPrimeraPendientePorRecurso(Long recursoId) {
        return jpaRepository.findFirstByRecursoIdAndEstadoOrderByCreadoAsc(recursoId, EstadoSolicitud.PENDIENTE);
    }
}
