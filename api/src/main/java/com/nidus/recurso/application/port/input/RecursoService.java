package com.nidus.recurso.application.port.input;

import com.nidus.recurso.application.dto.ActualizarRecursoRequest;
import com.nidus.recurso.application.dto.CrearRecursoRequest;
import com.nidus.recurso.application.dto.RecursoResponse;

import java.util.List;

public interface RecursoService {
    List<RecursoResponse> listar();
    RecursoResponse obtener(Long id);
    RecursoResponse crear(CrearRecursoRequest request);
    RecursoResponse actualizar(Long id, ActualizarRecursoRequest request);
    void desactivar(Long id);
}
