package com.nidus.admin.infrastructure.web;

import com.nidus.admin.application.dto.ActualizarUsuarioAdminRequest;
import com.nidus.admin.application.dto.CrearUsuarioAdminRequest;
import com.nidus.admin.application.dto.DashboardResponse;
import com.nidus.admin.application.dto.HistorialResponse;
import com.nidus.admin.application.dto.ReservaAdminResponse;
import com.nidus.admin.application.dto.UsuarioAdminResponse;
import com.nidus.admin.application.service.AdminService;
import com.nidus.auth.application.dto.CambiarRolRequest;
import com.nidus.auth.application.port.input.AuthService;
import com.nidus.cola.application.dto.SolicitudColaResponse;
import com.nidus.recurso.application.dto.ActualizarRecursoRequest;
import com.nidus.recurso.application.dto.CrearRecursoRequest;
import com.nidus.recurso.application.dto.RecursoResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;
    private final AuthService authService;

    public AdminController(AdminService adminService, AuthService authService) {
        this.adminService = adminService;
        this.authService = authService;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<DashboardResponse> dashboard() {
        return ResponseEntity.ok(adminService.dashboard());
    }

    // ── Usuarios ──────────────────────────────────

    @GetMapping("/usuarios")
    public ResponseEntity<Page<UsuarioAdminResponse>> listarUsuarios(@PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(adminService.listarUsuarios(pageable));
    }

    @GetMapping("/usuarios/{id}")
    public ResponseEntity<UsuarioAdminResponse> obtenerUsuario(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.obtenerUsuario(id));
    }

    @PostMapping("/usuarios")
    public ResponseEntity<UsuarioAdminResponse> crearUsuario(@Valid @RequestBody CrearUsuarioAdminRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adminService.crearUsuario(request));
    }

    @PutMapping("/usuarios/{id}")
    public ResponseEntity<UsuarioAdminResponse> actualizarUsuario(
            @PathVariable Long id, @Valid @RequestBody ActualizarUsuarioAdminRequest request) {
        return ResponseEntity.ok(adminService.actualizarUsuario(id, request));
    }

    @PutMapping("/usuarios/{id}/rol")
    public ResponseEntity<Void> cambiarRol(@PathVariable Long id, @Valid @RequestBody CambiarRolRequest request) {
        authService.cambiarRol(id, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/usuarios/{id}")
    public ResponseEntity<Void> eliminarUsuario(@PathVariable Long id) {
        adminService.eliminarUsuario(id);
        return ResponseEntity.noContent().build();
    }

    // ── Recursos ──────────────────────────────────

    @GetMapping("/recursos")
    public ResponseEntity<Page<RecursoResponse>> listarRecursos(@PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(adminService.listarRecursos(pageable));
    }

    @GetMapping("/recursos/{id}")
    public ResponseEntity<RecursoResponse> obtenerRecurso(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.obtenerRecurso(id));
    }

    @PostMapping("/recursos")
    public ResponseEntity<RecursoResponse> crearRecurso(@Valid @RequestBody CrearRecursoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adminService.crearRecurso(request));
    }

    @PutMapping("/recursos/{id}")
    public ResponseEntity<RecursoResponse> actualizarRecurso(
            @PathVariable Long id, @Valid @RequestBody ActualizarRecursoRequest request) {
        return ResponseEntity.ok(adminService.actualizarRecurso(id, request));
    }

    @DeleteMapping("/recursos/{id}")
    public ResponseEntity<Void> eliminarRecurso(@PathVariable Long id) {
        adminService.eliminarRecurso(id);
        return ResponseEntity.noContent().build();
    }

    // ── Reservas ──────────────────────────────────

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

    @PutMapping("/reservas/{id}/cancelar")
    public ResponseEntity<Void> cancelarReserva(@PathVariable Long id, Principal principal) {
        var adminId = authService.obtenerPerfil(principal.getName()).id();
        adminService.cancelarReserva(id, adminId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/reservas/{id}/historial")
    public ResponseEntity<List<HistorialResponse>> obtenerHistorial(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.obtenerHistorial(id));
    }

    // ── Cola de espera ────────────────────────────

    @GetMapping("/cola")
    public ResponseEntity<Page<SolicitudColaResponse>> listarSolicitudesCola(@PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(adminService.listarSolicitudesCola(pageable));
    }

    @DeleteMapping("/cola/{id}")
    public ResponseEntity<Void> eliminarSolicitudCola(@PathVariable Long id) {
        adminService.eliminarSolicitudCola(id);
        return ResponseEntity.noContent().build();
    }
}
