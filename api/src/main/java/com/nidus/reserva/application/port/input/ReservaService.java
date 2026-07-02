package com.nidus.reserva.application.port.input;

import com.nidus.reserva.application.dto.CrearReservaRequest;
import com.nidus.reserva.application.dto.ModificarReservaRequest;
import com.nidus.reserva.application.dto.ReservaResponse;
import java.util.List;

public interface ReservaService {

    ReservaResponse crear(CrearReservaRequest request, Long usuarioId);

    ReservaResponse modificar(Long id, ModificarReservaRequest request, Long usuarioId);

    void cancelar(Long id, Long usuarioId);

    ReservaResponse obtenerPorId(Long id, Long usuarioId);

    List<ReservaResponse> listarPorUsuario(Long usuarioId);

    List<ReservaResponse> listarTodas();
}
