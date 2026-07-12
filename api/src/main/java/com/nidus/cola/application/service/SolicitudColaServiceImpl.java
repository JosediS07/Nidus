package com.nidus.cola.application.service;

import com.nidus.auth.application.port.output.UserRepository;
import com.nidus.cola.application.dto.SolicitudColaResponse;
import com.nidus.cola.application.port.input.SolicitudColaService;
import com.nidus.cola.application.port.output.SolicitudColaRepository;
import com.nidus.cola.domain.EstadoSolicitud;
import com.nidus.cola.infrastructure.persistence.entity.SolicitudColaEntity;
import com.nidus.notificacion.application.port.NotificacionPort;
import com.nidus.recurso.application.port.input.RecursoService;
import com.nidus.reserva.domain.evento.ReservaEvento;
import com.nidus.shared.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Service
public class SolicitudColaServiceImpl implements SolicitudColaService {

    private static final Logger log = LoggerFactory.getLogger(SolicitudColaServiceImpl.class);

    private final SolicitudColaRepository solicitudColaRepository;
    private final NotificacionPort notificacionPort;
    private final UserRepository userRepository;
    private final RecursoService recursoService;

    public SolicitudColaServiceImpl(SolicitudColaRepository solicitudColaRepository,
                                    NotificacionPort notificacionPort,
                                    UserRepository userRepository,
                                    RecursoService recursoService) {
        this.solicitudColaRepository = solicitudColaRepository;
        this.notificacionPort = notificacionPort;
        this.userRepository = userRepository;
        this.recursoService = recursoService;
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

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void manejarCancelacion(ReservaEvento evento) {
        if (!ReservaEvento.CANCELACION.equals(evento.tipo())) {
            return;
        }

        try {
            var solicitudOpt = solicitudColaRepository.encontrarPrimeraPendientePorRecurso(evento.recursoId());
            if (solicitudOpt.isEmpty()) {
                return;
            }

            var solicitud = solicitudOpt.get();
            var usuario = userRepository.findById(solicitud.getUsuarioId())
                    .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
            var recurso = recursoService.obtener(evento.recursoId());

            notificacionPort.enviarNotificacionCola(
                    usuario.getEmail(), usuario.getNombre(), recurso.nombre(), solicitud.getId());

            solicitud.setEstado(EstadoSolicitud.NOTIFICADA);
            solicitudColaRepository.guardar(solicitud);

            log.info("Notificada solicitud {} de cola para recurso {} a usuario {}",
                    solicitud.getId(), evento.recursoId(), usuario.getEmail());
        } catch (Exception e) {
            log.error("Error al procesar cola de espera tras cancelación: {}", e.getMessage());
        }
    }

    private SolicitudColaResponse toResponse(SolicitudColaEntity e) {
        return new SolicitudColaResponse(e.getId(), e.getRecursoId(), e.getUsuarioId(), e.getEstado().name(), e.getCreado());
    }
}
