package com.nidus.reserva.application.service;

import com.nidus.reserva.application.port.output.HistorialReservaRepository;
import com.nidus.reserva.domain.HistorialReserva;
import com.nidus.reserva.domain.evento.ReservaEvento;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;

@Service
public class HistorialReservaService {

    private static final Logger log = LoggerFactory.getLogger(HistorialReservaService.class);

    private final HistorialReservaRepository historialRepository;

    public HistorialReservaService(HistorialReservaRepository historialRepository) {
        this.historialRepository = historialRepository;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void manejarEvento(ReservaEvento evento) {
        try {
            var historial = new HistorialReserva(
                null, evento.reservaId(), evento.usuarioId(),
                evento.tipo(), evento.descripcion(), null);
            historialRepository.guardar(historial);
            log.debug("Historial guardado: {} - reserva {}", evento.tipo(), evento.reservaId());
        } catch (Exception e) {
            log.error("Error al guardar historial para reserva {}: {}",
                evento.reservaId(), e.getMessage());
        }
    }

    public List<HistorialReserva> obtenerHistorial(Long reservaId) {
        return historialRepository.obtenerPorReservaId(reservaId);
    }
}
