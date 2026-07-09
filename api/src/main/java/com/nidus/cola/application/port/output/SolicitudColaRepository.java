package com.nidus.cola.application.port.output;

import com.nidus.cola.domain.EstadoSolicitud;
import com.nidus.cola.infrastructure.persistence.entity.SolicitudColaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface SolicitudColaRepository {

    SolicitudColaEntity guardar(SolicitudColaEntity entity);

    Optional<SolicitudColaEntity> encontrarPorId(Long id);

    Page<SolicitudColaEntity> encontrarPorUsuarioId(Long usuarioId, Pageable pageable);

    Page<SolicitudColaEntity> encontrarTodas(Pageable pageable);

    Optional<SolicitudColaEntity> encontrarPrimeraPendientePorRecurso(Long recursoId);
}
