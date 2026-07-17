package com.nidus.cola.application.service;

import com.nidus.auth.application.port.output.UserRepository;
import com.nidus.cola.application.dto.SolicitudColaResponse;
import com.nidus.cola.application.port.input.SolicitudColaService;
import com.nidus.cola.application.port.output.SolicitudColaRepository;
import com.nidus.cola.domain.EstadoSolicitud;
import com.nidus.cola.domain.SolicitudCola;
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

import java.time.LocalDateTime;

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
        var solicitud = new SolicitudCola(null, recursoId, usuarioId, EstadoSolicitud.PENDIENTE, LocalDateTime.now());
        var guardada = solicitudColaRepository.guardar(solicitud);
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
        var solicitud = solicitudColaRepository.encontrarPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Solicitud con id " + id + " no encontrada"));

        if (!solicitud.usuarioId().equals(usuarioId)) {
            throw new com.nidus.shared.exception.InvalidStateException("No tienes permiso para cancelar esta solicitud");
        }

        solicitudColaRepository.guardar(solicitud.conEstado(EstadoSolicitud.CANCELADA));
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        var solicitud = solicitudColaRepository.encontrarPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Solicitud con id " + id + " no encontrada"));
        solicitudColaRepository.guardar(solicitud.conEstado(EstadoSolicitud.CANCELADA));
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void manejarCancelacion(ReservaEvento evento) {
        if (!ReservaEvento.CANCELACION.equals(evento.tipo())) {
            return;
        }

        try {
            notificarSiguienteEnCola(evento);
        } catch (Exception e) {
            log.error("Error al procesar cola de espera tras cancelación: {}", e.getMessage());
        }
    }

    private void notificarSiguienteEnCola(ReservaEvento evento) {
        var solicitudOpt = solicitudColaRepository.encontrarPrimeraPendientePorRecurso(evento.recursoId());
        if (solicitudOpt.isEmpty()) {
            return;
        }

        var solicitud = solicitudOpt.get();
        var usuario = userRepository.findById(solicitud.usuarioId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        var recurso = recursoService.obtener(evento.recursoId());

        notificacionPort.enviarNotificacionCola(
                usuario.getEmail(), usuario.getNombre(), recurso.nombre(), solicitud.id());

        solicitudColaRepository.guardar(solicitud.conEstado(EstadoSolicitud.NOTIFICADA));

        log.info("Notificada solicitud {} de cola para recurso {} a usuario {}",
                solicitud.id(), evento.recursoId(), usuario.getEmail());
    }

    private SolicitudColaResponse toResponse(SolicitudCola solicitud) {
        return new SolicitudColaResponse(solicitud.id(), solicitud.recursoId(), solicitud.usuarioId(),
                solicitud.estado().name(), solicitud.creado());
    }
}
