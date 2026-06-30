package com.nidus.recurso;

import com.nidus.recurso.application.dto.ActualizarRecursoRequest;
import com.nidus.recurso.application.dto.CrearRecursoRequest;
import com.nidus.recurso.application.port.output.RecursoRepository;
import com.nidus.recurso.application.service.RecursoServiceImpl;
import com.nidus.recurso.domain.Recurso;
import com.nidus.recurso.domain.TipoRecurso;
import com.nidus.shared.exception.InvalidStateException;
import com.nidus.shared.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecursoServiceTest {

    @Mock
    private RecursoRepository recursoRepository;

    private RecursoServiceImpl recursoService;

    @BeforeEach
    void setUp() {
        recursoService = new RecursoServiceImpl(recursoRepository);
    }

    @Test
    void listar_devuelveSoloActivos() {
        var sala = new Recurso("Sala A", TipoRecurso.SALA, "Sala principal", 10);
        sala.setId(1L);
        when(recursoRepository.findAllActivos()).thenReturn(List.of(sala));

        var resultado = recursoService.listar();

        assertEquals(1, resultado.size());
        assertEquals("Sala A", resultado.getFirst().nombre());
        assertEquals(TipoRecurso.SALA, resultado.getFirst().tipo());
    }

    @Test
    void obtener_porIdExistente() {
        var recurso = new Recurso("Proyector X", TipoRecurso.PROYECTOR, null, null);
        recurso.setId(1L);
        when(recursoRepository.findById(1L)).thenReturn(Optional.of(recurso));

        var resultado = recursoService.obtener(1L);

        assertEquals("Proyector X", resultado.nombre());
        assertEquals(TipoRecurso.PROYECTOR, resultado.tipo());
    }

    @Test
    void obtener_porIdInexistenteLanzaExcepcion() {
        when(recursoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> recursoService.obtener(99L));
    }

    @Test
    void crear_guardaYRetornaResponse() {
        var request = new CrearRecursoRequest("Sala B", TipoRecurso.SALA, "Oficina", 6);
        var recurso = new Recurso("Sala B", TipoRecurso.SALA, "Oficina", 6);
        recurso.setId(2L);

        when(recursoRepository.save(any(Recurso.class))).thenReturn(recurso);

        var resultado = recursoService.crear(request);

        assertEquals("Sala B", resultado.nombre());
        assertEquals(TipoRecurso.SALA, resultado.tipo());
        assertEquals(6, resultado.capacidad());
        assertTrue(resultado.activo());
        verify(recursoRepository).save(any(Recurso.class));
    }

    @Test
    void actualizar_modificaSoloCamposPresentes() {
        var existente = new Recurso("Sala A", TipoRecurso.SALA, "Vieja desc", 10);
        existente.setId(1L);

        var request = new ActualizarRecursoRequest("Sala A renovada", null, "Nueva desc", null);

        when(recursoRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(recursoRepository.save(any(Recurso.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var resultado = recursoService.actualizar(1L, request);

        assertEquals("Sala A renovada", resultado.nombre());
        assertEquals(TipoRecurso.SALA, resultado.tipo());
        assertEquals("Nueva desc", resultado.descripcion());
        assertEquals(10, resultado.capacidad());
    }

    @Test
    void actualizar_idInexistenteLanzaExcepcion() {
        when(recursoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> recursoService.actualizar(99L, new ActualizarRecursoRequest(null, null, null, null)));
    }

    @Test
    void desactivar_cambiaActivoAFalse() {
        var recurso = new Recurso("Sala A", TipoRecurso.SALA, null, null);
        recurso.setId(1L);

        when(recursoRepository.findById(1L)).thenReturn(Optional.of(recurso));
        when(recursoRepository.save(any(Recurso.class))).thenAnswer(invocation -> invocation.getArgument(0));

        recursoService.desactivar(1L);

        assertFalse(recurso.isActivo());
        verify(recursoRepository).save(recurso);
    }

    @Test
    void desactivar_recursoYaInactivoLanzaExcepcion() {
        var recurso = new Recurso("Sala A", TipoRecurso.SALA, null, null);
        recurso.setId(1L);
        recurso.setActivo(false);

        when(recursoRepository.findById(1L)).thenReturn(Optional.of(recurso));

        assertThrows(InvalidStateException.class, () -> recursoService.desactivar(1L));
        verify(recursoRepository, never()).save(any());
    }

    @Test
    void desactivar_idInexistenteLanzaExcepcion() {
        when(recursoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> recursoService.desactivar(99L));
    }
}
