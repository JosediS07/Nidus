package com.nidus.cola.infrastructure.web;

import com.nidus.auth.application.port.output.UserRepository;
import com.nidus.cola.application.dto.SolicitudColaResponse;
import com.nidus.cola.application.port.input.SolicitudColaService;
import com.nidus.shared.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/cola")
public class ColaController {

    private final SolicitudColaService solicitudColaService;
    private final UserRepository userRepository;

    public ColaController(SolicitudColaService solicitudColaService, UserRepository userRepository) {
        this.solicitudColaService = solicitudColaService;
        this.userRepository = userRepository;
    }

    @PostMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<SolicitudColaResponse> apuntarse(
            @RequestBody SolicitudColaRequest request, Principal principal) {
        var usuarioId = obtenerUsuarioId(principal);
        var response = solicitudColaService.apuntarse(request.recursoId(), usuarioId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Page<SolicitudColaResponse>> listarMisSolicitudes(
            Principal principal, @PageableDefault(size = 20) Pageable pageable) {
        var usuarioId = obtenerUsuarioId(principal);
        return ResponseEntity.ok(solicitudColaService.listarPorUsuario(usuarioId, pageable));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Void> salir(@PathVariable Long id, Principal principal) {
        var usuarioId = obtenerUsuarioId(principal);
        solicitudColaService.salir(id, usuarioId);
        return ResponseEntity.noContent().build();
    }

    record SolicitudColaRequest(Long recursoId) {}

    private Long obtenerUsuarioId(Principal principal) {
        var email = principal.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"))
                .getId();
    }
}
