package com.nidus.reserva.infrastructure.persistence.repository;

import com.nidus.reserva.infrastructure.persistence.entity.HistorialReservaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JpaHistorialReservaRepository extends JpaRepository<HistorialReservaEntity, Long> {

    List<HistorialReservaEntity> findByReservaIdOrderByCreadoDesc(Long reservaId);
}
