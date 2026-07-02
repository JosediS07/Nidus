package com.nidus.reserva.infrastructure.persistence.repository;

import com.nidus.reserva.domain.EstadoReserva;
import com.nidus.reserva.infrastructure.persistence.entity.ReservaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface JpaReservaRepository extends JpaRepository<ReservaEntity, Long> {

    List<ReservaEntity> findByUsuarioIdOrderByFechaInicioDesc(Long usuarioId);

    List<ReservaEntity> findAllByOrderByFechaInicioDesc();

    @Query("""
        SELECT r FROM ReservaEntity r
        WHERE r.recursoId = :recursoId
          AND r.estado = :estado
          AND r.fechaInicio < :fechaFin
          AND r.fechaFin > :fechaInicio
          AND (:excluyendoId IS NULL OR r.id <> :excluyendoId)
        ORDER BY r.fechaInicio
    """)
    List<ReservaEntity> encontrarSolapamientos(
        @Param("recursoId") Long recursoId,
        @Param("fechaInicio") LocalDateTime fechaInicio,
        @Param("fechaFin") LocalDateTime fechaFin,
        @Param("excluyendoId") Long excluyendoId,
        @Param("estado") EstadoReserva estado
    );
}
