package com.nidus.cola.application.port.output;

import com.nidus.cola.domain.SolicitudCola;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface SolicitudColaRepository {

    SolicitudCola guardar(SolicitudCola solicitud);

    Optional<SolicitudCola> encontrarPorId(Long id);

    Page<SolicitudCola> encontrarPorUsuarioId(Long usuarioId, Pageable pageable);

    Page<SolicitudCola> encontrarTodas(Pageable pageable);

    Optional<SolicitudCola> encontrarPrimeraPendientePorRecurso(Long recursoId);
}
