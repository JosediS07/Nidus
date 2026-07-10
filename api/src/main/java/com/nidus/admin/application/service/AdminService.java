package com.nidus.admin.application.service;

import com.nidus.admin.application.dto.DashboardResponse;
import com.nidus.admin.application.dto.HistorialResponse;
import com.nidus.admin.application.dto.ReservaAdminResponse;
import com.nidus.admin.application.dto.UsuarioAdminResponse;
import com.nidus.auth.infrastructure.persistence.repository.JpaUserRepository;
import com.nidus.cola.application.dto.SolicitudColaResponse;
import com.nidus.cola.application.port.input.SolicitudColaService;
import com.nidus.recurso.infrastructure.persistence.repository.JpaRecursoRepository;
import com.nidus.reserva.domain.EstadoReserva;
import com.nidus.reserva.infrastructure.persistence.entity.ReservaEntity;
import com.nidus.reserva.application.service.HistorialReservaService;
import com.nidus.reserva.infrastructure.persistence.repository.JpaReservaRepository;
import com.nidus.shared.exception.ResourceNotFoundException;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AdminService {

    private final JpaUserRepository userRepository;
    private final JpaRecursoRepository recursoRepository;
    private final JpaReservaRepository reservaRepository;
    private final HistorialReservaService historialService;
    private final SolicitudColaService solicitudColaService;

    public AdminService(JpaUserRepository userRepository,
                        JpaRecursoRepository recursoRepository,
                        JpaReservaRepository reservaRepository,
                        HistorialReservaService historialService,
                        SolicitudColaService solicitudColaService) {
        this.userRepository = userRepository;
        this.recursoRepository = recursoRepository;
        this.reservaRepository = reservaRepository;
        this.historialService = historialService;
        this.solicitudColaService = solicitudColaService;
    }

    @Transactional(readOnly = true)
    public DashboardResponse dashboard() {
        long totalUsuarios = userRepository.count();
        long totalRecursos = recursoRepository.count();
        long totalReservas = reservaRepository.count();

        Map<String, Long> reservasPorEstado = Arrays.stream(EstadoReserva.values())
            .collect(Collectors.toMap(
                Enum::name,
                e -> reservaRepository.countByEstado(e)
            ));

        LocalDateTime hoyInicio = LocalDate.now().atStartOfDay();
        LocalDateTime hoyFin = LocalDate.now().atTime(LocalTime.MAX);
        long reservasHoy = reservaRepository.countByFechaInicioBetween(hoyInicio, hoyFin);

        String recursoMasReservado = reservaRepository.findTopRecursoId()
            .map(id -> recursoRepository.findById(id)
                .map(r -> r.getNombre())
                .orElse("—"))
            .orElse("—");

        return new DashboardResponse(
            totalUsuarios, totalRecursos, totalReservas,
            reservasPorEstado, reservasHoy, recursoMasReservado
        );
    }

    @Transactional(readOnly = true)
    public Page<UsuarioAdminResponse> listarUsuarios(Pageable pageable) {
        return userRepository.findAll(pageable)
            .map(u -> new UsuarioAdminResponse(
                u.getId(), u.getNombre(), u.getEmail(),
                u.getRol().name(), u.isActivo(), u.getCreado()));
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

        Specification<ReservaEntity> spec = (root, query, cb) -> {
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

        return reservaRepository.findAll(spec, pageable).map(this::toReservaAdminResponse);
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
                    h.getId(), h.getReservaId(), h.getUsuarioId(),
                    h.getTipoEvento(), h.getDescripcion(), h.getCreado()))
                .toList();
    }

    private ReservaAdminResponse toReservaAdminResponse(ReservaEntity r) {
        return new ReservaAdminResponse(
            r.getId(), r.getRecursoId(), r.getUsuarioId(),
            r.getFechaInicio(), r.getFechaFin(), r.getEstado().name());
    }
}
