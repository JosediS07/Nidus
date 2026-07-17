package com.nidus.cola.application.port.input;

import com.nidus.cola.application.dto.SolicitudColaResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SolicitudColaService {

    SolicitudColaResponse apuntarse(Long recursoId, Long usuarioId);

    Page<SolicitudColaResponse> listarPorUsuario(Long usuarioId, Pageable pageable);

    Page<SolicitudColaResponse> listarTodas(Pageable pageable);

    void salir(Long id, Long usuarioId);

    void eliminar(Long id);
}
