package com.nidus.recurso.infrastructure.web;

import com.nidus.recurso.application.dto.ActualizarRecursoRequest;
import com.nidus.recurso.application.dto.CrearRecursoRequest;
import com.nidus.recurso.application.dto.RecursoResponse;
import com.nidus.recurso.application.port.input.RecursoService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/recursos")
public class RecursoController {

    private final RecursoService recursoService;

    public RecursoController(RecursoService recursoService) {
        this.recursoService = recursoService;
    }

    @GetMapping
    public ResponseEntity<Page<RecursoResponse>> listar(@PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(recursoService.listar(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RecursoResponse> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(recursoService.obtener(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RecursoResponse> crear(@Valid @RequestBody CrearRecursoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(recursoService.crear(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RecursoResponse> actualizar(@PathVariable Long id, @Valid @RequestBody ActualizarRecursoRequest request) {
        return ResponseEntity.ok(recursoService.actualizar(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> desactivar(@PathVariable Long id) {
        recursoService.desactivar(id);
        return ResponseEntity.noContent().build();
    }
}
