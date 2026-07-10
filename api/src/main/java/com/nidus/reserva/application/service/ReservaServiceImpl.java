package com.nidus.reserva.application.service;

import com.nidus.auth.application.port.output.UserRepository;
import com.nidus.notificacion.application.port.NotificacionPort;
import com.nidus.recurso.application.port.input.RecursoService;
import com.nidus.reserva.application.dto.CrearReservaRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nidus.reserva.application.dto.ModificarReservaRequest;
import com.nidus.reserva.application.dto.ReservaResponse;
import com.nidus.reserva.application.port.input.ReservaService;
import com.nidus.reserva.application.port.output.ReservaRepository;
import com.nidus.reserva.domain.EstadoReserva;
import com.nidus.reserva.domain.Reserva;
import com.nidus.reserva.domain.evento.ReservaEvento;
import com.nidus.shared.exception.InvalidStateException;
import com.nidus.shared.exception.ResourceNotFoundException;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReservaServiceImpl implements ReservaService {

    private static final Logger log = LoggerFactory.getLogger(ReservaServiceImpl.class);

    private final ReservaRepository reservaRepository;
    private final UserRepository userRepository;
    private final RecursoService recursoService;
    private final NotificacionPort notificacionPort;
    private final ApplicationEventPublisher eventPublisher;

    public ReservaServiceImpl(ReservaRepository reservaRepository, UserRepository userRepository,
                              RecursoService recursoService, NotificacionPort notificacionPort,
                              ApplicationEventPublisher eventPublisher) {
        this.reservaRepository = reservaRepository;
        this.userRepository = userRepository;
        this.recursoService = recursoService;
        this.notificacionPort = notificacionPort;
        this.eventPublisher = eventPublisher;
    }

    @Override
    @Transactional
    public ReservaResponse crear(CrearReservaRequest request, Long usuarioId) {
        validarFechas(request.fechaInicio(), request.fechaFin());
        validarSinSolapamiento(request.recursoId(), request.fechaInicio(), request.fechaFin(), null);

        var usuario = userRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario con id " + usuarioId + " no encontrado"));

        var reserva = new Reserva(null, request.recursoId(), usuario.getId(),
                request.fechaInicio(), request.fechaFin(), EstadoReserva.CONFIRMADA, 0);

        var guardada = reservaRepository.guardar(reserva);
        notificar(guardada, "confirmacion");
        eventPublisher.publishEvent(new ReservaEvento(
            ReservaEvento.CREACION, guardada.getId(), usuarioId, guardada.getRecursoId(),
            guardada.getFechaInicio(), guardada.getFechaFin(),
            "Reserva creada: recurso " + request.recursoId()
                + " del " + request.fechaInicio() + " al " + request.fechaFin()));
        return toResponse(guardada);
    }

    @Override
    @Transactional
    public ReservaResponse modificar(Long id, ModificarReservaRequest request, Long usuarioId) {
        validarFechas(request.fechaInicio(), request.fechaFin());

        var reserva = reservaRepository.encontrarPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva con id " + id + " no encontrada"));

        if (!reserva.getUsuarioId().equals(usuarioId) && !esAdmin(usuarioId)) {
            throw new InvalidStateException("No tienes permiso para modificar esta reserva");
        }

        if (reserva.getEstado() == EstadoReserva.CANCELADA) {
            throw new InvalidStateException("No se puede modificar una reserva cancelada");
        }

        validarSinSolapamiento(reserva.getRecursoId(), request.fechaInicio(), request.fechaFin(), id);

        reserva.setFechaInicio(request.fechaInicio());
        reserva.setFechaFin(request.fechaFin());
        reserva.setEstado(EstadoReserva.MODIFICADA);

        var guardada = reservaRepository.guardar(reserva);
        notificar(guardada, "modificacion");
        eventPublisher.publishEvent(new ReservaEvento(
            ReservaEvento.MODIFICACION, guardada.getId(), usuarioId, guardada.getRecursoId(),
            guardada.getFechaInicio(), guardada.getFechaFin(),
            "Reserva modificada: nuevas fechas del " + request.fechaInicio()
                + " al " + request.fechaFin()));
        return toResponse(guardada);
    }

    @Override
    @Transactional
    public void cancelar(Long id, Long usuarioId) {
        var reserva = reservaRepository.encontrarPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva con id " + id + " no encontrada"));

        if (!reserva.getUsuarioId().equals(usuarioId) && !esAdmin(usuarioId)) {
            throw new InvalidStateException("No tienes permiso para cancelar esta reserva");
        }

        if (reserva.getEstado() == EstadoReserva.CANCELADA) {
            throw new InvalidStateException("La reserva con id " + id + " ya está cancelada");
        }

        reserva.setEstado(EstadoReserva.CANCELADA);
        reservaRepository.guardar(reserva);
        notificar(reserva, "cancelacion");
        eventPublisher.publishEvent(new ReservaEvento(
            ReservaEvento.CANCELACION, id, usuarioId, reserva.getRecursoId(),
            reserva.getFechaInicio(), reserva.getFechaFin(),
            "Reserva cancelada"));
    }

    @Override
    @Transactional(readOnly = true)
    public ReservaResponse obtenerPorId(Long id, Long usuarioId) {
        var reserva = reservaRepository.encontrarPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva con id " + id + " no encontrada"));

        if (!reserva.getUsuarioId().equals(usuarioId) && !esAdmin(usuarioId)) {
            throw new ResourceNotFoundException("Reserva con id " + id + " no encontrada");
        }

        return toResponse(reserva);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReservaResponse> listarPorUsuario(Long usuarioId, Pageable pageable) {
        return reservaRepository.encontrarPorUsuarioId(usuarioId, pageable)
                .map(this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReservaResponse> listarTodas(Pageable pageable) {
        return reservaRepository.encontrarTodas(pageable)
                .map(this::toResponse);
    }

    private void notificar(Reserva reserva, String tipo) {
        try {
            var usuario = userRepository.findById(reserva.getUsuarioId())
                    .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
            var recurso = recursoService.obtener(reserva.getRecursoId());

            switch (tipo) {
                case "confirmacion" -> notificacionPort.enviarConfirmacion(
                        usuario.getEmail(), usuario.getNombre(), reserva.getId(),
                        recurso.nombre(), reserva.getFechaInicio(), reserva.getFechaFin());
                case "modificacion" -> notificacionPort.enviarModificacion(
                        usuario.getEmail(), usuario.getNombre(), reserva.getId(),
                        recurso.nombre(), reserva.getFechaInicio(), reserva.getFechaFin());
                case "cancelacion" -> notificacionPort.enviarCancelacion(
                        usuario.getEmail(), usuario.getNombre(), reserva.getId(),
                        recurso.nombre(), reserva.getFechaInicio(), reserva.getFechaFin());
            }
        } catch (Exception e) {
            log.warn("No se pudo enviar notificación para reserva {}: {}", reserva.getId(), e.getMessage());
        }
    }

    private void validarFechas(LocalDateTime inicio, LocalDateTime fin) {
        if (!fin.isAfter(inicio)) {
            throw new InvalidStateException("fechaFin debe ser posterior a fechaInicio");
        }
    }

    private void validarSinSolapamiento(Long recursoId, LocalDateTime inicio, LocalDateTime fin, Long excluyendoId) {
        var solapamientos = reservaRepository.encontrarSolapamientos(recursoId, inicio, fin, excluyendoId);
        if (!solapamientos.isEmpty()) {
            throw new InvalidStateException("El recurso ya está reservado en ese horario");
        }
    }

    private boolean esAdmin(Long usuarioId) {
        return userRepository.findById(usuarioId)
                .map(u -> u.getRol().name().equals("ADMIN"))
                .orElse(false);
    }

    private ReservaResponse toResponse(Reserva r) {
        return new ReservaResponse(r.getId(), r.getRecursoId(), r.getUsuarioId(),
                r.getFechaInicio(), r.getFechaFin(), r.getEstado().name());
    }
}
