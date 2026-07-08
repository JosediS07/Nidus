package com.nidus.reserva.application.port.output;

import com.nidus.reserva.domain.Reserva;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ReservaRepository {

    Reserva guardar(Reserva reserva);

    Optional<Reserva> encontrarPorId(Long id);

    Page<Reserva> encontrarPorUsuarioId(Long usuarioId, Pageable pageable);

    Page<Reserva> encontrarTodas(Pageable pageable);

    List<Reserva> encontrarSolapamientos(Long recursoId, java.time.LocalDateTime fechaInicio,
                                          java.time.LocalDateTime fechaFin, Long excluyendoId);
}
