package com.nidus.reserva.application.port.input;

import com.nidus.reserva.application.dto.CrearReservaRequest;
import com.nidus.reserva.application.dto.ModificarReservaRequest;
import com.nidus.reserva.application.dto.ReservaResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReservaService {

    ReservaResponse crear(CrearReservaRequest request, Long usuarioId);

    ReservaResponse modificar(Long id, ModificarReservaRequest request, Long usuarioId);

    void cancelar(Long id, Long usuarioId);

    ReservaResponse obtenerPorId(Long id, Long usuarioId);

    Page<ReservaResponse> listarPorUsuario(Long usuarioId, Pageable pageable);

    Page<ReservaResponse> listarTodas(Pageable pageable);
}
