package com.nidus.cola.infrastructure.persistence.repository;

import com.nidus.cola.application.port.output.SolicitudColaRepository;
import com.nidus.cola.domain.EstadoSolicitud;
import com.nidus.cola.domain.SolicitudCola;
import com.nidus.cola.infrastructure.persistence.mapper.SolicitudColaMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class SolicitudColaRepositoryAdapter implements SolicitudColaRepository {

    private final JpaSolicitudColaRepository jpaRepository;
    private final SolicitudColaMapper mapper;

    public SolicitudColaRepositoryAdapter(JpaSolicitudColaRepository jpaRepository, SolicitudColaMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public SolicitudCola guardar(SolicitudCola solicitud) {
        var entity = mapper.toEntity(solicitud);
        var saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<SolicitudCola> encontrarPorId(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Page<SolicitudCola> encontrarPorUsuarioId(Long usuarioId, Pageable pageable) {
        return jpaRepository.findByUsuarioIdOrderByCreadoDesc(usuarioId, pageable)
                .map(mapper::toDomain);
    }

    @Override
    public Page<SolicitudCola> encontrarTodas(Pageable pageable) {
        return jpaRepository.findAllByOrderByCreadoDesc(pageable)
                .map(mapper::toDomain);
    }

    @Override
    public Optional<SolicitudCola> encontrarPrimeraPendientePorRecurso(Long recursoId) {
        return jpaRepository.findFirstByRecursoIdAndEstadoOrderByCreadoAsc(recursoId, EstadoSolicitud.PENDIENTE)
                .map(mapper::toDomain);
    }
}
