package com.nidus.recurso;

import com.nidus.recurso.domain.TipoRecurso;
import com.nidus.recurso.infrastructure.persistence.entity.RecursoEntity;
import com.nidus.recurso.infrastructure.persistence.repository.JpaRecursoRepository;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@Tag("integration")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class RecursoRepositoryTest {

    @Autowired
    private JpaRecursoRepository jpaRecursoRepository;

    @Test
    void guardarYBuscarPorId() {
        var recurso = new RecursoEntity("Sala A", TipoRecurso.SALA, "Sala principal", 10);
        var guardado = jpaRecursoRepository.save(recurso);

        assertNotNull(guardado.getId());

        var encontrado = jpaRecursoRepository.findById(guardado.getId());

        assertTrue(encontrado.isPresent());
        assertEquals("Sala A", encontrado.get().getNombre());
        assertEquals(TipoRecurso.SALA, encontrado.get().getTipo());
        assertEquals(10, encontrado.get().getCapacidad());
        assertTrue(encontrado.get().isActivo());
    }

    @Test
    void findByActivoTrue_devuelveSoloActivos() {
        var activo = new RecursoEntity("Sala Activa", TipoRecurso.SALA, null, null);
        var inactivo = new RecursoEntity("Sala Inactiva", TipoRecurso.SALA, null, null);
        inactivo.setActivo(false);

        jpaRecursoRepository.save(activo);
        jpaRecursoRepository.save(inactivo);

        var activos = jpaRecursoRepository.findByActivoTrue(PageRequest.of(0, 10));

        assertEquals(1, activos.getContent().size());
        assertEquals("Sala Activa", activos.getContent().getFirst().getNombre());
    }
}
