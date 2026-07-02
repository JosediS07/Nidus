package com.nidus.reserva.infrastructure.persistence.repository;

import com.nidus.reserva.application.port.output.ReservaRepository;
import com.nidus.reserva.domain.EstadoReserva;
import com.nidus.reserva.domain.Reserva;
import com.nidus.reserva.infrastructure.persistence.mapper.ReservaEntityMapper;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
public class ReservaRepositoryAdapter implements ReservaRepository {

    private final JpaReservaRepository jpaReservaRepository;
    private final ReservaEntityMapper mapper;

    public ReservaRepositoryAdapter(JpaReservaRepository jpaReservaRepository, ReservaEntityMapper mapper) {
        this.jpaReservaRepository = jpaReservaRepository;
        this.mapper = mapper;
    }

    @Override
    public Reserva guardar(Reserva reserva) {
        var entity = mapper.toEntity(reserva);
        var guardada = jpaReservaRepository.save(entity);
        return mapper.toDomain(guardada);
    }

    @Override
    public Optional<Reserva> encontrarPorId(Long id) {
        return jpaReservaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Reserva> encontrarPorUsuarioId(Long usuarioId) {
        return jpaReservaRepository.findByUsuarioIdOrderByFechaInicioDesc(usuarioId).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<Reserva> encontrarTodas() {
        return jpaReservaRepository.findAllByOrderByFechaInicioDesc().stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<Reserva> encontrarSolapamientos(Long recursoId, LocalDateTime fechaInicio,
                                                  LocalDateTime fechaFin, Long excluyendoId) {
        return jpaReservaRepository.encontrarSolapamientos(
                recursoId, fechaInicio, fechaFin, excluyendoId, EstadoReserva.CONFIRMADA
        ).stream().map(mapper::toDomain).toList();
    }
}
