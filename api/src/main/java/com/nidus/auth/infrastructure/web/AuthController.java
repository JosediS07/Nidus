package com.nidus.auth.infrastructure.web;

import com.nidus.auth.application.dto.ActualizarPerfilRequest;
import com.nidus.auth.application.dto.AuthResponse;
import com.nidus.auth.application.dto.CambiarRolRequest;
import com.nidus.auth.application.dto.LoginRequest;
import com.nidus.auth.application.dto.RegisterRequest;
import com.nidus.auth.application.dto.UserResponse;
import com.nidus.auth.application.port.input.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.registrar(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<UserResponse> perfil(Principal principal) {
        return ResponseEntity.ok(authService.obtenerPerfil(principal.getName()));
    }

    @PutMapping("/me")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<UserResponse> actualizarPerfil(Principal principal, @Valid @RequestBody ActualizarPerfilRequest request) {
        return ResponseEntity.ok(authService.actualizarPerfil(principal.getName(), request));
    }

    @PutMapping("/usuarios/{id}/rol")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> cambiarRol(@PathVariable Long id, @Valid @RequestBody CambiarRolRequest request) {
        authService.cambiarRol(id, request);
        return ResponseEntity.ok().build();
    }
}
