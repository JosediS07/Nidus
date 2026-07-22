package com.nidus.reserva;

import com.nidus.reserva.domain.EstadoReserva;
import com.nidus.reserva.infrastructure.persistence.entity.ReservaEntity;
import com.nidus.reserva.infrastructure.persistence.repository.JpaReservaRepository;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@Tag("integration")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class ReservaRepositoryTest {

    @Autowired
    private JpaReservaRepository jpaReservaRepository;

    private final LocalDateTime maniana = LocalDateTime.now().plusDays(1);
    private final LocalDateTime pasadoManiana = LocalDateTime.now().plusDays(2);

    @Test
    void guardarYBuscarPorId() {
        var reserva = new ReservaEntity(null, 1L, 1L, maniana, pasadoManiana, EstadoReserva.CONFIRMADA, 0);
        var guardada = jpaReservaRepository.save(reserva);

        assertNotNull(guardada.getId());

        var encontrada = jpaReservaRepository.findById(guardada.getId());

        assertTrue(encontrada.isPresent());
        assertEquals(1L, encontrada.get().getRecursoId());
        assertEquals(1L, encontrada.get().getUsuarioId());
        assertEquals(EstadoReserva.CONFIRMADA, encontrada.get().getEstado());
    }

    @Test
    void encontrarSolapamientos_detectaConflicto() {
        var existente = new ReservaEntity(null, 1L, 1L, maniana, pasadoManiana, EstadoReserva.CONFIRMADA, 0);
        jpaReservaRepository.save(existente);

        var nuevoInicio = maniana.plusHours(1);
        var nuevoFin = pasadoManiana.minusHours(1);

        var solapamientos = jpaReservaRepository.encontrarSolapamientos(
                1L, nuevoInicio, nuevoFin, null, EstadoReserva.CONFIRMADA);

        assertEquals(1, solapamientos.size());
    }

    @Test
    void encontrarSolapamientos_excluyePropiaReserva() {
        var existente = new ReservaEntity(null, 1L, 1L, maniana, pasadoManiana, EstadoReserva.CONFIRMADA, 0);
        var guardada = jpaReservaRepository.save(existente);

        var solapamientos = jpaReservaRepository.encontrarSolapamientos(
                1L, maniana.plusHours(1), pasadoManiana.minusHours(1), guardada.getId(), EstadoReserva.CONFIRMADA);

        assertTrue(solapamientos.isEmpty());
    }

    @Test
    void encontrarSolapamientos_ignoraCanceladas() {
        var cancelada = new ReservaEntity(null, 1L, 1L, maniana, pasadoManiana, EstadoReserva.CANCELADA, 0);
        jpaReservaRepository.save(cancelada);

        var solapamientos = jpaReservaRepository.encontrarSolapamientos(
                1L, maniana, pasadoManiana, null, EstadoReserva.CONFIRMADA);

        assertTrue(solapamientos.isEmpty());
    }

    @Test
    void encontrarSolapamientos_recursoDiferenteNoConflicto() {
        var existente = new ReservaEntity(null, 1L, 1L, maniana, pasadoManiana, EstadoReserva.CONFIRMADA, 0);
        jpaReservaRepository.save(existente);

        var solapamientos = jpaReservaRepository.encontrarSolapamientos(
                2L, maniana, pasadoManiana, null, EstadoReserva.CONFIRMADA);

        assertTrue(solapamientos.isEmpty());
    }

    @Test
    void findByUsuarioIdOrderByFechaInicioDesc() {
        var r1 = new ReservaEntity(null, 1L, 1L, maniana, pasadoManiana, EstadoReserva.CONFIRMADA, 0);
        var r2 = new ReservaEntity(null, 1L, 1L, maniana.plusDays(3), pasadoManiana.plusDays(3), EstadoReserva.CONFIRMADA, 0);
        jpaReservaRepository.save(r1);
        jpaReservaRepository.save(r2);

        var reservas = jpaReservaRepository.findByUsuarioIdOrderByFechaInicioDesc(1L, PageRequest.of(0, 10));

        assertEquals(2, reservas.getContent().size());
    }
}
