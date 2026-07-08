package com.nidus.recurso.application.service;

import com.nidus.recurso.application.dto.ActualizarRecursoRequest;
import com.nidus.recurso.application.dto.CrearRecursoRequest;
import com.nidus.recurso.application.dto.RecursoResponse;
import com.nidus.recurso.application.port.input.RecursoService;
import com.nidus.recurso.application.port.output.RecursoRepository;
import com.nidus.recurso.domain.Recurso;
import com.nidus.shared.exception.InvalidStateException;
import com.nidus.shared.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RecursoServiceImpl implements RecursoService {

    private final RecursoRepository recursoRepository;

    public RecursoServiceImpl(RecursoRepository recursoRepository) {
        this.recursoRepository = recursoRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RecursoResponse> listar(Pageable pageable) {
        return recursoRepository.findAllActivos(pageable)
                .map(this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public RecursoResponse obtener(Long id) {
        var recurso = recursoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Recurso con id " + id + " no encontrado"));
        return toResponse(recurso);
    }

    @Override
    @Transactional
    public RecursoResponse crear(CrearRecursoRequest request) {
        var recurso = new Recurso(request.nombre(), request.tipo(), request.descripcion(), request.capacidad());
        var guardado = recursoRepository.save(recurso);
        return toResponse(guardado);
    }

    @Override
    @Transactional
    public RecursoResponse actualizar(Long id, ActualizarRecursoRequest request) {
        var recurso = recursoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Recurso con id " + id + " no encontrado"));

        if (request.nombre() != null) recurso.setNombre(request.nombre());
        if (request.tipo() != null) recurso.setTipo(request.tipo());
        if (request.descripcion() != null) recurso.setDescripcion(request.descripcion());
        if (request.capacidad() != null) recurso.setCapacidad(request.capacidad());

        var guardado = recursoRepository.save(recurso);
        return toResponse(guardado);
    }

    @Override
    @Transactional
    public void desactivar(Long id) {
        var recurso = recursoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Recurso con id " + id + " no encontrado"));

        if (!recurso.isActivo()) {
            throw new InvalidStateException("El recurso con id " + id + " ya está inactivo");
        }

        recurso.setActivo(false);
        recursoRepository.save(recurso);
    }

    private RecursoResponse toResponse(Recurso r) {
        return new RecursoResponse(r.getId(), r.getNombre(), r.getTipo(), r.getDescripcion(), r.getCapacidad(), r.isActivo());
    }
}
