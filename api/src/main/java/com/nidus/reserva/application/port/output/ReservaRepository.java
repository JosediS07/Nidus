package com.nidus.reserva.application.port.output;

import com.nidus.reserva.domain.Reserva;
import java.util.List;
import java.util.Optional;

public interface ReservaRepository {

    Reserva guardar(Reserva reserva);

    Optional<Reserva> encontrarPorId(Long id);

    List<Reserva> encontrarPorUsuarioId(Long usuarioId);

    List<Reserva> encontrarTodas();

    List<Reserva> encontrarSolapamientos(Long recursoId, java.time.LocalDateTime fechaInicio,
                                          java.time.LocalDateTime fechaFin, Long excluyendoId);
}
