package com.nidus.cola.application.service;

import com.nidus.cola.application.dto.SolicitudColaResponse;
import com.nidus.cola.application.port.input.SolicitudColaService;
import com.nidus.cola.application.port.output.SolicitudColaRepository;
import com.nidus.cola.domain.EstadoSolicitud;
import com.nidus.cola.infrastructure.persistence.entity.SolicitudColaEntity;
import com.nidus.shared.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SolicitudColaServiceImpl implements SolicitudColaService {

    private final SolicitudColaRepository solicitudColaRepository;

    public SolicitudColaServiceImpl(SolicitudColaRepository solicitudColaRepository) {
        this.solicitudColaRepository = solicitudColaRepository;
    }

    @Override
    @Transactional
    public SolicitudColaResponse apuntarse(Long recursoId, Long usuarioId) {
        var entity = new SolicitudColaEntity(recursoId, usuarioId);
        var guardada = solicitudColaRepository.guardar(entity);
        return toResponse(guardada);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SolicitudColaResponse> listarPorUsuario(Long usuarioId, Pageable pageable) {
        return solicitudColaRepository.encontrarPorUsuarioId(usuarioId, pageable)
                .map(this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SolicitudColaResponse> listarTodas(Pageable pageable) {
        return solicitudColaRepository.encontrarTodas(pageable)
                .map(this::toResponse);
    }

    @Override
    @Transactional
    public void salir(Long id, Long usuarioId) {
        var entity = solicitudColaRepository.encontrarPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Solicitud con id " + id + " no encontrada"));

        if (!entity.getUsuarioId().equals(usuarioId)) {
            throw new com.nidus.shared.exception.InvalidStateException("No tienes permiso para cancelar esta solicitud");
        }

        entity.setEstado(EstadoSolicitud.CANCELADA);
        solicitudColaRepository.guardar(entity);
    }

    private SolicitudColaResponse toResponse(SolicitudColaEntity e) {
        return new SolicitudColaResponse(e.getId(), e.getRecursoId(), e.getEstado().name(), e.getCreado());
    }
}
