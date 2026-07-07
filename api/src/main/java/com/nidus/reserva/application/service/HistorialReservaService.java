package com.nidus.reserva.application.service;

import com.nidus.reserva.domain.evento.ReservaEvento;
import com.nidus.reserva.infrastructure.persistence.entity.HistorialReservaEntity;
import com.nidus.reserva.infrastructure.persistence.repository.JpaHistorialReservaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;

@Service
public class HistorialReservaService {

    private static final Logger log = LoggerFactory.getLogger(HistorialReservaService.class);

    private final JpaHistorialReservaRepository historialRepository;

    public HistorialReservaService(JpaHistorialReservaRepository historialRepository) {
        this.historialRepository = historialRepository;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void manejarEvento(ReservaEvento evento) {
        try {
            var entity = new HistorialReservaEntity(
                evento.reservaId(), evento.usuarioId(),
                evento.tipo(), evento.descripcion());
            historialRepository.save(entity);
            log.debug("Historial guardado: {} - reserva {}", evento.tipo(), evento.reservaId());
        } catch (Exception e) {
            log.error("Error al guardar historial para reserva {}: {}",
                evento.reservaId(), e.getMessage());
        }
    }

    public List<HistorialReservaEntity> obtenerHistorial(Long reservaId) {
        return historialRepository.findByReservaIdOrderByCreadoDesc(reservaId);
    }
}
