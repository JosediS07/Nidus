package com.nidus.reserva.infrastructure.web;

import com.nidus.auth.application.port.output.UserRepository;
import com.nidus.reserva.application.dto.CrearReservaRequest;
import com.nidus.reserva.application.dto.ModificarReservaRequest;
import com.nidus.reserva.application.dto.ReservaResponse;
import com.nidus.reserva.application.port.input.ReservaService;
import com.nidus.shared.exception.ResourceNotFoundException;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/reservas")
public class ReservaController {

    private final ReservaService reservaService;
    private final UserRepository userRepository;

    public ReservaController(ReservaService reservaService, UserRepository userRepository) {
        this.reservaService = reservaService;
        this.userRepository = userRepository;
    }

    @PostMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ReservaResponse> crear(@Valid @RequestBody CrearReservaRequest request,
                                                  Principal principal) {
        var usuarioId = obtenerUsuarioId(principal);
        var response = reservaService.crear(request, usuarioId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Page<ReservaResponse>> listarMisReservas(
            Principal principal, @PageableDefault(size = 20) Pageable pageable) {
        var usuarioId = obtenerUsuarioId(principal);
        var reservas = reservaService.listarPorUsuario(usuarioId, pageable);
        return ResponseEntity.ok(reservas);
    }

    @GetMapping("/todas")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<ReservaResponse>> listarTodas(@PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(reservaService.listarTodas(pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ReservaResponse> obtener(@PathVariable Long id, Principal principal) {
        var usuarioId = obtenerUsuarioId(principal);
        return ResponseEntity.ok(reservaService.obtenerPorId(id, usuarioId));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<ReservaResponse> modificar(@PathVariable Long id,
                                                      @Valid @RequestBody ModificarReservaRequest request,
                                                      Principal principal) {
        var usuarioId = obtenerUsuarioId(principal);
        return ResponseEntity.ok(reservaService.modificar(id, request, usuarioId));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Void> cancelar(@PathVariable Long id, Principal principal) {
        var usuarioId = obtenerUsuarioId(principal);
        reservaService.cancelar(id, usuarioId);
        return ResponseEntity.noContent().build();
    }

    private Long obtenerUsuarioId(Principal principal) {
        var email = principal.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"))
                .getId();
    }
}
