package com.nidus.admin.infrastructure.web;

import com.nidus.admin.application.dto.DashboardResponse;
import com.nidus.admin.application.dto.ReservaAdminResponse;
import com.nidus.admin.application.dto.UsuarioAdminResponse;
import com.nidus.admin.application.service.AdminService;
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
    public ResponseEntity<List<UsuarioAdminResponse>> listarUsuarios() {
        return ResponseEntity.ok(adminService.listarUsuarios());
    }

    @GetMapping("/usuarios/{id}")
    public ResponseEntity<UsuarioAdminResponse> obtenerUsuario(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.obtenerUsuario(id));
    }

    @GetMapping("/reservas")
    public ResponseEntity<List<ReservaAdminResponse>> listarReservas(
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) Long recursoId,
            @RequestParam(required = false) Long usuarioId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin) {
        return ResponseEntity.ok(
            adminService.listarReservas(estado, recursoId, usuarioId, fechaInicio, fechaFin));
    }

    @GetMapping("/reservas/{id}")
    public ResponseEntity<ReservaAdminResponse> obtenerReserva(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.obtenerReserva(id));
    }
}
