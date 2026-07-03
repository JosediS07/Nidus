package com.nidus.reserva;

import com.nidus.auth.application.port.output.UserRepository;
import com.nidus.auth.domain.Role;
import com.nidus.auth.domain.User;
import com.nidus.notificacion.application.port.NotificacionPort;
import com.nidus.recurso.application.dto.RecursoResponse;
import com.nidus.recurso.application.port.input.RecursoService;
import com.nidus.recurso.domain.TipoRecurso;
import com.nidus.reserva.application.dto.CrearReservaRequest;
import com.nidus.reserva.application.dto.ModificarReservaRequest;
import com.nidus.reserva.application.port.output.ReservaRepository;
import com.nidus.reserva.application.service.ReservaServiceImpl;
import com.nidus.reserva.domain.EstadoReserva;
import com.nidus.reserva.domain.Reserva;
import com.nidus.shared.exception.InvalidStateException;
import com.nidus.shared.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservaServiceTest {

    @Mock
    private ReservaRepository reservaRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RecursoService recursoService;

    @Mock
    private NotificacionPort notificacionPort;

    private ReservaServiceImpl reservaService;

    private final RecursoResponse recursoResponse = new RecursoResponse(10L, "Sala A", TipoRecurso.SALA, "Desc", 10, true);

    private final Long usuarioId = 1L;
    private final Long recursoId = 10L;
    private final LocalDateTime maniana = LocalDateTime.now().plusDays(1);
    private final LocalDateTime pasadoManiana = LocalDateTime.now().plusDays(2);

    @BeforeEach
    void setUp() {
        reservaService = new ReservaServiceImpl(reservaRepository, userRepository, recursoService, notificacionPort);
    }

    @Test
    void crear_guardaYRetornaResponse() {
        var request = new CrearReservaRequest(recursoId, maniana, pasadoManiana);
        var usuario = new User("Juan", "juan@mail.com", "hash", Role.USER);
        usuario.setId(usuarioId);
        var reserva = new Reserva(null, recursoId, usuarioId, maniana, pasadoManiana, EstadoReserva.CONFIRMADA, 0);
        reserva.setId(1L);

        when(userRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));
        when(reservaRepository.encontrarSolapamientos(any(), any(), any(), any())).thenReturn(List.of());
        when(reservaRepository.guardar(any(Reserva.class))).thenReturn(reserva);
        when(recursoService.obtener(recursoId)).thenReturn(recursoResponse);

        var resultado = reservaService.crear(request, usuarioId);

        assertEquals(1L, resultado.id());
        assertEquals(recursoId, resultado.recursoId());
        assertEquals(usuarioId, resultado.usuarioId());
        assertEquals(EstadoReserva.CONFIRMADA, resultado.estado());
        verify(reservaRepository).guardar(any(Reserva.class));
        verify(notificacionPort).enviarConfirmacion(eq("juan@mail.com"), eq("Juan"), eq(1L),
                eq("Sala A"), any(), any());
    }

    @Test
    void crear_conSolapamientoLanzaExcepcion() {
        var request = new CrearReservaRequest(recursoId, maniana, pasadoManiana);
        var reservaSolapada = new Reserva(2L, recursoId, 2L, maniana, pasadoManiana, EstadoReserva.CONFIRMADA, 0);

        when(reservaRepository.encontrarSolapamientos(recursoId, maniana, pasadoManiana, null))
                .thenReturn(List.of(reservaSolapada));

        assertThrows(InvalidStateException.class, () -> reservaService.crear(request, usuarioId));
        verify(reservaRepository, never()).guardar(any());
    }

    @Test
    void crear_fechaFinAntesDeFechaInicioLanzaExcepcion() {
        var request = new CrearReservaRequest(recursoId, pasadoManiana, maniana);

        assertThrows(InvalidStateException.class, () -> reservaService.crear(request, usuarioId));
        verify(reservaRepository, never()).guardar(any());
    }

    @Test
    void modificar_actualizaFechasYEstado() {
        var reserva = new Reserva(1L, recursoId, usuarioId, maniana, pasadoManiana, EstadoReserva.CONFIRMADA, 0);
        var nuevaInicio = LocalDateTime.now().plusDays(3);
        var nuevaFin = LocalDateTime.now().plusDays(4);
        var request = new ModificarReservaRequest(nuevaInicio, nuevaFin);
        var usuario = new User("Juan", "juan@mail.com", "hash", Role.USER);
        usuario.setId(usuarioId);

        when(reservaRepository.encontrarPorId(1L)).thenReturn(Optional.of(reserva));
        when(reservaRepository.encontrarSolapamientos(any(), any(), any(), any())).thenReturn(List.of());
        when(reservaRepository.guardar(any(Reserva.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(userRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));
        when(recursoService.obtener(recursoId)).thenReturn(recursoResponse);

        var resultado = reservaService.modificar(1L, request, usuarioId);

        assertEquals(nuevaInicio, resultado.fechaInicio());
        assertEquals(nuevaFin, resultado.fechaFin());
        assertEquals(EstadoReserva.MODIFICADA, resultado.estado());
        verify(notificacionPort).enviarModificacion(any(), any(), any(), any(), any(), any());
    }

    @Test
    void modificar_reservaInexistenteLanzaExcepcion() {
        when(reservaRepository.encontrarPorId(99L)).thenReturn(Optional.empty());

        var request = new ModificarReservaRequest(maniana, pasadoManiana);
        assertThrows(ResourceNotFoundException.class, () -> reservaService.modificar(99L, request, usuarioId));
    }

    @Test
    void modificar_reservaCanceladaLanzaExcepcion() {
        var reserva = new Reserva(1L, recursoId, usuarioId, maniana, pasadoManiana, EstadoReserva.CANCELADA, 0);

        when(reservaRepository.encontrarPorId(1L)).thenReturn(Optional.of(reserva));

        var request = new ModificarReservaRequest(maniana, pasadoManiana);
        assertThrows(InvalidStateException.class, () -> reservaService.modificar(1L, request, usuarioId));
    }

    @Test
    void modificar_otroUsuarioNoAdminLanzaExcepcion() {
        var reserva = new Reserva(1L, recursoId, 999L, maniana, pasadoManiana, EstadoReserva.CONFIRMADA, 0);

        when(reservaRepository.encontrarPorId(1L)).thenReturn(Optional.of(reserva));
        when(userRepository.findById(usuarioId)).thenReturn(Optional.of(
                usuarioSinId("otro@mail.com")));

        var request = new ModificarReservaRequest(maniana, pasadoManiana);
        assertThrows(InvalidStateException.class, () -> reservaService.modificar(1L, request, usuarioId));
    }

    @Test
    void cancelar_cambiaEstado() {
        var reserva = new Reserva(1L, recursoId, usuarioId, maniana, pasadoManiana, EstadoReserva.CONFIRMADA, 0);
        var usuario = new User("Juan", "juan@mail.com", "hash", Role.USER);
        usuario.setId(usuarioId);

        when(reservaRepository.encontrarPorId(1L)).thenReturn(Optional.of(reserva));
        when(reservaRepository.guardar(any(Reserva.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(userRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));
        when(recursoService.obtener(recursoId)).thenReturn(recursoResponse);

        reservaService.cancelar(1L, usuarioId);

        assertEquals(EstadoReserva.CANCELADA, reserva.getEstado());
        verify(reservaRepository).guardar(reserva);
        verify(notificacionPort).enviarCancelacion(any(), any(), any(), any(), any(), any());
    }

    @Test
    void cancelar_yaCanceladaLanzaExcepcion() {
        var reserva = new Reserva(1L, recursoId, usuarioId, maniana, pasadoManiana, EstadoReserva.CANCELADA, 0);

        when(reservaRepository.encontrarPorId(1L)).thenReturn(Optional.of(reserva));

        assertThrows(InvalidStateException.class, () -> reservaService.cancelar(1L, usuarioId));
        verify(reservaRepository, never()).guardar(any());
    }

    @Test
    void cancelar_reservaInexistenteLanzaExcepcion() {
        when(reservaRepository.encontrarPorId(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> reservaService.cancelar(99L, usuarioId));
    }

    @Test
    void obtenerPorId_retornaReserva() {
        var reserva = new Reserva(1L, recursoId, usuarioId, maniana, pasadoManiana, EstadoReserva.CONFIRMADA, 0);

        when(reservaRepository.encontrarPorId(1L)).thenReturn(Optional.of(reserva));

        var resultado = reservaService.obtenerPorId(1L, usuarioId);

        assertEquals(1L, resultado.id());
        assertEquals(EstadoReserva.CONFIRMADA, resultado.estado());
    }

    @Test
    void obtenerPorId_deOtroUsuarioLanzaResourceNotFound() {
        var reserva = new Reserva(1L, recursoId, 999L, maniana, pasadoManiana, EstadoReserva.CONFIRMADA, 0);

        when(reservaRepository.encontrarPorId(1L)).thenReturn(Optional.of(reserva));
        when(userRepository.findById(usuarioId)).thenReturn(Optional.of(
                usuarioSinId("otro@mail.com")));

        assertThrows(ResourceNotFoundException.class, () -> reservaService.obtenerPorId(1L, usuarioId));
    }

    @Test
    void listarPorUsuario_devuelveReservasDelUsuario() {
        var reserva = new Reserva(1L, recursoId, usuarioId, maniana, pasadoManiana, EstadoReserva.CONFIRMADA, 0);

        when(reservaRepository.encontrarPorUsuarioId(usuarioId)).thenReturn(List.of(reserva));

        var resultado = reservaService.listarPorUsuario(usuarioId);

        assertEquals(1, resultado.size());
        assertEquals(usuarioId, resultado.getFirst().usuarioId());
    }

    @Test
    void listarTodas_devuelveTodas() {
        var r1 = new Reserva(1L, recursoId, 1L, maniana, pasadoManiana, EstadoReserva.CONFIRMADA, 0);
        var r2 = new Reserva(2L, recursoId, 2L, maniana.plusDays(5), pasadoManiana.plusDays(5), EstadoReserva.CONFIRMADA, 0);

        when(reservaRepository.encontrarTodas()).thenReturn(List.of(r1, r2));

        var resultado = reservaService.listarTodas();

        assertEquals(2, resultado.size());
    }

    @Test
    void cancelar_adminPuedeCancelarReservaDeOtro() {
        var adminId = 2L;
        var reserva = new Reserva(1L, recursoId, 999L, maniana, pasadoManiana, EstadoReserva.CONFIRMADA, 0);
        var admin = new User("Admin", "admin@mail.com", "hash", Role.ADMIN);
        admin.setId(adminId);
        var duenio = new User("Otro", "otro@mail.com", "hash", Role.USER);
        duenio.setId(999L);

        when(reservaRepository.encontrarPorId(1L)).thenReturn(Optional.of(reserva));
        when(reservaRepository.guardar(any(Reserva.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(userRepository.findById(adminId)).thenReturn(Optional.of(admin));
        when(userRepository.findById(999L)).thenReturn(Optional.of(duenio));
        when(recursoService.obtener(recursoId)).thenReturn(recursoResponse);

        reservaService.cancelar(1L, adminId);

        assertEquals(EstadoReserva.CANCELADA, reserva.getEstado());
        verify(notificacionPort).enviarCancelacion(eq("otro@mail.com"), any(), any(), any(), any(), any());
    }

    private User usuarioSinId(String email) {
        return new User("Otro", email, "hash", Role.USER);
    }
}
