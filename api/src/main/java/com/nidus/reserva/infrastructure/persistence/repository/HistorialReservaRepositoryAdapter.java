package com.nidus.reserva.infrastructure.persistence.repository;

import com.nidus.reserva.application.port.output.HistorialReservaRepository;
import com.nidus.reserva.domain.HistorialReserva;
import com.nidus.reserva.infrastructure.persistence.mapper.HistorialReservaMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class HistorialReservaRepositoryAdapter implements HistorialReservaRepository {

    private final JpaHistorialReservaRepository jpaRepository;
    private final HistorialReservaMapper mapper;

    public HistorialReservaRepositoryAdapter(JpaHistorialReservaRepository jpaRepository, HistorialReservaMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public void guardar(HistorialReserva historial) {
        var entity = mapper.toEntity(historial);
        jpaRepository.save(entity);
    }

    @Override
    public List<HistorialReserva> obtenerPorReservaId(Long reservaId) {
        return jpaRepository.findByReservaIdOrderByCreadoDesc(reservaId)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }
}
