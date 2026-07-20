package com.nidus.cola;

import com.nidus.auth.application.port.output.UserRepository;
import com.nidus.auth.domain.Role;
import com.nidus.auth.domain.User;
import com.nidus.cola.application.dto.SolicitudColaResponse;
import com.nidus.cola.application.port.output.SolicitudColaRepository;
import com.nidus.cola.application.service.SolicitudColaServiceImpl;
import com.nidus.cola.domain.EstadoSolicitud;
import com.nidus.cola.domain.SolicitudCola;
import com.nidus.notificacion.application.port.NotificacionPort;
import com.nidus.recurso.application.dto.RecursoResponse;
import com.nidus.recurso.application.port.input.RecursoService;
import com.nidus.recurso.domain.TipoRecurso;
import com.nidus.reserva.domain.evento.ReservaEvento;
import com.nidus.shared.exception.InvalidStateException;
import java.util.Set;
import com.nidus.shared.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SolicitudColaServiceImplTest {

    @Mock
    private SolicitudColaRepository solicitudColaRepository;

    @Mock
    private NotificacionPort notificacionPort;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RecursoService recursoService;

    private SolicitudColaServiceImpl solicitudColaService;

    private final Long recursoId = 10L;
    private final Long usuarioId = 1L;
    private final LocalDateTime ahora = LocalDateTime.now();
    private final SolicitudCola solicitudPendiente = new SolicitudCola(1L, recursoId, usuarioId, EstadoSolicitud.PENDIENTE, ahora);

    @BeforeEach
    void setUp() {
        solicitudColaService = new SolicitudColaServiceImpl(solicitudColaRepository, notificacionPort, userRepository, recursoService);
    }

    @Test
    void apuntarse_creaPendiente() {
        when(solicitudColaRepository.guardar(any())).thenReturn(solicitudPendiente);

        SolicitudColaResponse result = solicitudColaService.apuntarse(recursoId, usuarioId);

        assertEquals(1L, result.id());
        assertEquals("PENDIENTE", result.estado());
        verify(solicitudColaRepository).guardar(any());
    }

    @Test
    void listarPorUsuario_devuelvePage() {
        var pageable = PageRequest.of(0, 20);
        var page = new PageImpl<>(List.of(solicitudPendiente), pageable, 1);
        when(solicitudColaRepository.encontrarPorUsuarioId(usuarioId, pageable)).thenReturn(page);

        Page<SolicitudColaResponse> result = solicitudColaService.listarPorUsuario(usuarioId, pageable);

        assertEquals(1, result.getContent().size());
        assertEquals(usuarioId, result.getContent().getFirst().usuarioId());
    }

    @Test
    void listarTodas_devuelvePage() {
        var pageable = PageRequest.of(0, 20);
        var page = new PageImpl<>(List.of(solicitudPendiente), pageable, 1);
        when(solicitudColaRepository.encontrarTodas(pageable)).thenReturn(page);

        Page<SolicitudColaResponse> result = solicitudColaService.listarTodas(pageable);

        assertEquals(1, result.getTotalElements());
    }

    @Test
    void salir_conPropietario_cambiaEstado() {
        when(solicitudColaRepository.encontrarPorId(1L)).thenReturn(Optional.of(solicitudPendiente));
        when(solicitudColaRepository.guardar(any())).thenAnswer(invocation -> invocation.getArgument(0));

        solicitudColaService.salir(1L, usuarioId);

        verify(solicitudColaRepository).guardar(argThat(s -> s.estado() == EstadoSolicitud.CANCELADA));
    }

    @Test
    void salir_conOtroUsuario_lanzaException() {
        when(solicitudColaRepository.encontrarPorId(1L)).thenReturn(Optional.of(solicitudPendiente));

        assertThrows(InvalidStateException.class, () -> solicitudColaService.salir(1L, 999L));
        verify(solicitudColaRepository, never()).guardar(any());
    }

    @Test
    void salir_inexistente_lanzaException() {
        when(solicitudColaRepository.encontrarPorId(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> solicitudColaService.salir(99L, usuarioId));
    }

    @Test
    void eliminar_existente_cambiaEstado() {
        when(solicitudColaRepository.encontrarPorId(1L)).thenReturn(Optional.of(solicitudPendiente));
        when(solicitudColaRepository.guardar(any())).thenAnswer(invocation -> invocation.getArgument(0));

        solicitudColaService.eliminar(1L);

        verify(solicitudColaRepository).guardar(argThat(s -> s.estado() == EstadoSolicitud.CANCELADA));
    }

    @Test
    void eliminar_inexistente_lanzaException() {
        when(solicitudColaRepository.encontrarPorId(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> solicitudColaService.eliminar(99L));
    }

    @Test
    void manejarCancelacion_eventoCorrecto_notifica() {
        var evento = new ReservaEvento(ReservaEvento.CANCELACION, 1L, usuarioId, recursoId, ahora, ahora.plusHours(2), "Cancelación");
        var usuario = new User("Juan", "juan@mail.com", "hash", Role.USER);
        usuario.setId(usuarioId);

        when(solicitudColaRepository.encontrarPrimeraPendientePorRecurso(recursoId))
                .thenReturn(Optional.of(solicitudPendiente));
        when(userRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));
        when(recursoService.obtener(recursoId)).thenReturn(
                new RecursoResponse(recursoId, "Sala A", TipoRecurso.SALA, "Desc", 10, true));

        solicitudColaService.manejarCancelacion(evento);

        verify(notificacionPort).enviarNotificacionCola("juan@mail.com", "Juan", "Sala A", 1L);
        verify(solicitudColaRepository).guardar(argThat(s -> s.estado() == EstadoSolicitud.NOTIFICADA));
    }

    @Test
    void manejarCancelacion_sinSolicitudesPendientes_noNotifica() {
        var evento = new ReservaEvento(ReservaEvento.CANCELACION, 1L, usuarioId, recursoId, ahora, ahora.plusHours(2), "Cancelación");

        when(solicitudColaRepository.encontrarPrimeraPendientePorRecurso(recursoId))
                .thenReturn(Optional.empty());

        solicitudColaService.manejarCancelacion(evento);

        verify(notificacionPort, never()).enviarNotificacionCola(any(), any(), any(), any());
        verify(solicitudColaRepository, never()).guardar(any());
    }

    @Test
    void manejarCancelacion_eventoIncorrecto_ignora() {
        var evento = new ReservaEvento(ReservaEvento.CREACION, 1L, usuarioId, recursoId, ahora, ahora.plusHours(2), "Creación");

        solicitudColaService.manejarCancelacion(evento);

        verify(solicitudColaRepository, never()).encontrarPrimeraPendientePorRecurso(any());
        verify(notificacionPort, never()).enviarNotificacionCola(any(), any(), any(), any());
    }

    @Test
    void manejarCancelacion_exceptionEnNotificacion_noPropaga() {
        var evento = new ReservaEvento(ReservaEvento.CANCELACION, 1L, usuarioId, recursoId, ahora, ahora.plusHours(2), "Cancelación");

        when(solicitudColaRepository.encontrarPrimeraPendientePorRecurso(recursoId))
                .thenThrow(new RuntimeException("DB error"));

        assertDoesNotThrow(() -> solicitudColaService.manejarCancelacion(evento));
    }
}
