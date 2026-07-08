package com.nidus.recurso.application.port.input;

import com.nidus.recurso.application.dto.ActualizarRecursoRequest;
import com.nidus.recurso.application.dto.CrearRecursoRequest;
import com.nidus.recurso.application.dto.RecursoResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RecursoService {
    Page<RecursoResponse> listar(Pageable pageable);
    RecursoResponse obtener(Long id);
    RecursoResponse crear(CrearRecursoRequest request);
    RecursoResponse actualizar(Long id, ActualizarRecursoRequest request);
    void desactivar(Long id);
}
