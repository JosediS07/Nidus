package com.nidus.admin.infrastructure.web;

import com.nidus.admin.application.dto.DashboardResponse;
import com.nidus.admin.application.dto.ReservaAdminResponse;
import com.nidus.admin.application.dto.UsuarioAdminResponse;
import com.nidus.admin.application.service.AdminService;
import com.nidus.cola.application.dto.SolicitudColaResponse;
import com.nidus.reserva.infrastructure.persistence.entity.HistorialReservaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<DashboardResponse> dashboard() {
        return ResponseEntity.ok(adminService.dashboard());
    }

    @GetMapping("/usuarios")
    public ResponseEntity<Page<UsuarioAdminResponse>> listarUsuarios(@PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(adminService.listarUsuarios(pageable));
    }

    @GetMapping("/usuarios/{id}")
    public ResponseEntity<UsuarioAdminResponse> obtenerUsuario(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.obtenerUsuario(id));
    }

    @GetMapping("/reservas")
    public ResponseEntity<Page<ReservaAdminResponse>> listarReservas(
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) Long recursoId,
            @RequestParam(required = false) Long usuarioId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(
            adminService.listarReservas(estado, recursoId, usuarioId, fechaInicio, fechaFin, pageable));
    }

    @GetMapping("/reservas/{id}")
    public ResponseEntity<ReservaAdminResponse> obtenerReserva(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.obtenerReserva(id));
    }

    @GetMapping("/cola")
    public ResponseEntity<Page<SolicitudColaResponse>> listarSolicitudesCola(@PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(adminService.listarSolicitudesCola(pageable));
    }

    @GetMapping("/reservas/{id}/historial")
    public ResponseEntity<List<HistorialReservaEntity>> obtenerHistorial(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.obtenerHistorial(id));
    }
}
