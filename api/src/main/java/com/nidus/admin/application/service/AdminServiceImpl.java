package com.nidus.admin.application.service;

import com.nidus.admin.application.dto.ActualizarUsuarioAdminRequest;
import com.nidus.admin.application.dto.CrearUsuarioAdminRequest;
import com.nidus.admin.application.dto.DashboardResponse;
import com.nidus.admin.application.dto.HistorialResponse;
import com.nidus.admin.application.dto.ReservaAdminResponse;
import com.nidus.admin.application.dto.UsuarioAdminResponse;
import com.nidus.admin.application.port.input.AdminServicePort;
import com.nidus.auth.infrastructure.persistence.entity.UserEntity;
import com.nidus.auth.infrastructure.persistence.repository.JpaUserRepository;
import com.nidus.cola.application.dto.SolicitudColaResponse;
import com.nidus.cola.application.port.input.SolicitudColaService;

import com.nidus.recurso.application.dto.ActualizarRecursoRequest;
import com.nidus.recurso.application.dto.CrearRecursoRequest;
import com.nidus.recurso.application.dto.RecursoResponse;
import com.nidus.recurso.application.port.input.RecursoService;
import com.nidus.recurso.infrastructure.persistence.repository.JpaRecursoRepository;
import com.nidus.reserva.application.port.input.ReservaService;
import com.nidus.reserva.domain.EstadoReserva;
import com.nidus.reserva.infrastructure.persistence.entity.ReservaEntity;
import com.nidus.reserva.application.service.HistorialReservaService;
import com.nidus.reserva.infrastructure.persistence.repository.JpaReservaRepository;
import com.nidus.shared.exception.DuplicateResourceException;
import com.nidus.shared.exception.ResourceNotFoundException;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AdminServiceImpl implements AdminServicePort {

    private final JpaUserRepository userRepository;
    private final JpaRecursoRepository recursoRepository;
    private final JpaReservaRepository reservaRepository;
    private final HistorialReservaService historialService;
    private final SolicitudColaService solicitudColaService;
    private final RecursoService recursoService;
    private final ReservaService reservaService;
    private final PasswordEncoder passwordEncoder;

    public AdminServiceImpl(JpaUserRepository userRepository,
                        JpaRecursoRepository recursoRepository,
                        JpaReservaRepository reservaRepository,
                        HistorialReservaService historialService,
                        SolicitudColaService solicitudColaService,
                        RecursoService recursoService,
                        ReservaService reservaService,
                        PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.recursoRepository = recursoRepository;
        this.reservaRepository = reservaRepository;
        this.historialService = historialService;
        this.solicitudColaService = solicitudColaService;
        this.recursoService = recursoService;
        this.reservaService = reservaService;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public DashboardResponse dashboard() {
        long totalUsuarios = userRepository.count();
        long totalRecursos = recursoRepository.count();
        long totalReservas = reservaRepository.count();

        return new DashboardResponse(
            totalUsuarios, totalRecursos, totalReservas,
            obtenerReservasPorEstado(), obtenerReservasHoy(), obtenerRecursoMasReservado()
        );
    }

    private Map<String, Long> obtenerReservasPorEstado() {
        return Arrays.stream(EstadoReserva.values())
            .collect(Collectors.toMap(Enum::name, estado -> reservaRepository.countByEstado(estado)));
    }

    private long obtenerReservasHoy() {
        LocalDateTime hoyInicio = LocalDate.now().atStartOfDay();
        LocalDateTime hoyFin = LocalDate.now().atTime(LocalTime.MAX);
        return reservaRepository.countByFechaInicioBetween(hoyInicio, hoyFin);
    }

    private String obtenerRecursoMasReservado() {
        return reservaRepository.findTopRecursoId()
            .map(id -> recursoRepository.findById(id)
                .map(recurso -> recurso.getNombre())
                .orElse("—"))
            .orElse("—");
    }

    @Transactional(readOnly = true)
    public Page<UsuarioAdminResponse> listarUsuarios(Pageable pageable) {
        return userRepository.findAll(pageable)
            .map(usuario -> new UsuarioAdminResponse(
                usuario.getId(), usuario.getNombre(), usuario.getEmail(),
                usuario.getRol().name(), usuario.isActivo(), usuario.getCreado()));
    }

    @Transactional(readOnly = true)
    public UsuarioAdminResponse obtenerUsuario(Long id) {
        var user = userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Usuario con id " + id + " no encontrado"));
        return new UsuarioAdminResponse(
            user.getId(), user.getNombre(), user.getEmail(),
            user.getRol().name(), user.isActivo(), user.getCreado());
    }

    @Transactional(readOnly = true)
    public Page<ReservaAdminResponse> listarReservas(
            String estado, Long recursoId, Long usuarioId,
            LocalDateTime fechaInicio, LocalDateTime fechaFin,
            Pageable pageable) {
        Specification<ReservaEntity> spec = construirFiltroReservas(
                estado, recursoId, usuarioId, fechaInicio, fechaFin);
        return reservaRepository.findAll(spec, pageable).map(this::toReservaAdminResponse);
    }

    private Specification<ReservaEntity> construirFiltroReservas(
            String estado, Long recursoId, Long usuarioId,
            LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        return (root, query, cb) -> {
            var predicates = new ArrayList<Predicate>();

            if (estado != null && !estado.isBlank()) {
                predicates.add(cb.equal(root.get("estado"), EstadoReserva.valueOf(estado.toUpperCase())));
            }
            if (recursoId != null) {
                predicates.add(cb.equal(root.get("recursoId"), recursoId));
            }
            if (usuarioId != null) {
                predicates.add(cb.equal(root.get("usuarioId"), usuarioId));
            }
            if (fechaInicio != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("fechaFin"), fechaInicio));
            }
            if (fechaFin != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("fechaInicio"), fechaFin));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    @Transactional(readOnly = true)
    public ReservaAdminResponse obtenerReserva(Long id) {
        var reserva = reservaRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Reserva con id " + id + " no encontrada"));
        return toReservaAdminResponse(reserva);
    }

    @Transactional(readOnly = true)
    public Page<SolicitudColaResponse> listarSolicitudesCola(Pageable pageable) {
        return solicitudColaService.listarTodas(pageable);
    }

    public List<HistorialResponse> obtenerHistorial(Long reservaId) {
        if (!reservaRepository.existsById(reservaId)) {
            throw new ResourceNotFoundException("Reserva con id " + reservaId + " no encontrada");
        }
        return historialService.obtenerHistorial(reservaId).stream()
                .map(h -> new HistorialResponse(
                    h.id(), h.reservaId(), h.usuarioId(),
                    h.tipoEvento(), h.descripcion(), h.creado()))
                .toList();
    }

    @Transactional
    public UsuarioAdminResponse crearUsuario(CrearUsuarioAdminRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new DuplicateResourceException("El email " + request.email() + " ya está registrado");
        }
        var entity = new UserEntity(request.nombre(), request.email(),
                passwordEncoder.encode(request.password()), request.rol());
        var guardado = userRepository.save(entity);
        return toUsuarioAdminResponse(guardado);
    }

    @Transactional
    public UsuarioAdminResponse actualizarUsuario(Long id, ActualizarUsuarioAdminRequest request) {
        var user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario con id " + id + " no encontrado"));
        if (request.nombre() != null) user.setNombre(request.nombre());
        if (request.email() != null) user.setEmail(request.email());
        if (request.activo() != null) user.setActivo(request.activo());
        var guardado = userRepository.save(user);
        return toUsuarioAdminResponse(guardado);
    }

    @Transactional
    public void eliminarUsuario(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("Usuario con id " + id + " no encontrado");
        }
        userRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Page<RecursoResponse> listarRecursos(Pageable pageable) {
        return recursoRepository.findAll(pageable)
                .map(recurso -> new RecursoResponse(
                    recurso.getId(), recurso.getNombre(), recurso.getTipo(),
                    recurso.getDescripcion(), recurso.getCapacidad(), recurso.isActivo()));
    }

    @Transactional(readOnly = true)
    public RecursoResponse obtenerRecurso(Long id) {
        var recurso = recursoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Recurso con id " + id + " no encontrado"));
        return new RecursoResponse(
            recurso.getId(), recurso.getNombre(), recurso.getTipo(),
            recurso.getDescripcion(), recurso.getCapacidad(), recurso.isActivo());
    }

    @Transactional
    public RecursoResponse crearRecurso(CrearRecursoRequest request) {
        return recursoService.crear(request);
    }

    @Transactional
    public RecursoResponse actualizarRecurso(Long id, ActualizarRecursoRequest request) {
        return recursoService.actualizar(id, request);
    }

    @Transactional
    public void eliminarRecurso(Long id) {
        if (!recursoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Recurso con id " + id + " no encontrado");
        }
        recursoRepository.deleteById(id);
    }

    @Transactional
    public void cancelarReserva(Long id, Long adminUserId) {
        reservaService.cancelar(id, adminUserId);
    }

    @Transactional
    public void eliminarSolicitudCola(Long id) {
        solicitudColaService.eliminar(id);
    }

    private UsuarioAdminResponse toUsuarioAdminResponse(UserEntity usuario) {
        return new UsuarioAdminResponse(
            usuario.getId(), usuario.getNombre(), usuario.getEmail(),
            usuario.getRol().name(), usuario.isActivo(), usuario.getCreado());
    }

    private ReservaAdminResponse toReservaAdminResponse(ReservaEntity reserva) {
        return new ReservaAdminResponse(
            reserva.getId(), reserva.getRecursoId(), reserva.getUsuarioId(),
            reserva.getFechaInicio(), reserva.getFechaFin(), reserva.getEstado().name());
    }
}
