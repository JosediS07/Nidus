package com.nidus.reserva.application.port.output;

import com.nidus.reserva.domain.HistorialReserva;

import java.util.List;

public interface HistorialReservaRepository {

    void guardar(HistorialReserva historial);

    List<HistorialReserva> obtenerPorReservaId(Long reservaId);
}
