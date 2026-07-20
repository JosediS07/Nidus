package com.nidus.admin;

import com.nidus.admin.application.dto.ActualizarUsuarioAdminRequest;
import com.nidus.admin.application.dto.CrearUsuarioAdminRequest;
import com.nidus.admin.application.dto.DashboardResponse;
import com.nidus.admin.application.dto.UsuarioAdminResponse;
import com.nidus.admin.application.service.AdminServiceImpl;
import com.nidus.auth.domain.Role;
import com.nidus.auth.infrastructure.persistence.entity.UserEntity;
import com.nidus.auth.infrastructure.persistence.repository.JpaUserRepository;
import com.nidus.cola.application.port.input.SolicitudColaService;
import com.nidus.recurso.application.dto.ActualizarRecursoRequest;
import com.nidus.recurso.application.dto.CrearRecursoRequest;
import com.nidus.recurso.application.dto.RecursoResponse;
import com.nidus.recurso.application.port.input.RecursoService;
import com.nidus.recurso.domain.TipoRecurso;
import com.nidus.recurso.infrastructure.persistence.entity.RecursoEntity;
import com.nidus.recurso.infrastructure.persistence.repository.JpaRecursoRepository;
import com.nidus.reserva.application.service.HistorialReservaService;
import com.nidus.reserva.domain.EstadoReserva;
import com.nidus.reserva.domain.HistorialReserva;
import com.nidus.reserva.infrastructure.persistence.entity.ReservaEntity;
import com.nidus.reserva.infrastructure.persistence.repository.JpaReservaRepository;
import com.nidus.reserva.application.port.input.ReservaService;
import com.nidus.shared.exception.DuplicateResourceException;
import com.nidus.shared.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminServiceImplTest {

    @Mock
    private JpaUserRepository userRepository;

    @Mock
    private JpaRecursoRepository recursoRepository;

    @Mock
    private JpaReservaRepository reservaRepository;

    @Mock
    private HistorialReservaService historialService;

    @Mock
    private SolicitudColaService solicitudColaService;

    @Mock
    private RecursoService recursoService;

    @Mock
    private ReservaService reservaService;

    @Mock
    private PasswordEncoder passwordEncoder;

    private AdminServiceImpl adminService;

    @BeforeEach
    void setUp() {
        adminService = new AdminServiceImpl(userRepository, recursoRepository, reservaRepository,
                historialService, solicitudColaService, recursoService, reservaService, passwordEncoder);
    }

    @Test
    void dashboard_calculaTodo() {
        when(userRepository.count()).thenReturn(5L);
        when(recursoRepository.count()).thenReturn(3L);
        when(reservaRepository.count()).thenReturn(10L);
        when(reservaRepository.countByEstado(EstadoReserva.CONFIRMADA)).thenReturn(6L);
        when(reservaRepository.countByEstado(EstadoReserva.CANCELADA)).thenReturn(3L);
        when(reservaRepository.countByEstado(EstadoReserva.MODIFICADA)).thenReturn(1L);
        when(reservaRepository.countByFechaInicioBetween(any(), any())).thenReturn(2L);
        when(reservaRepository.findTopRecursoId()).thenReturn(Optional.of(1L));
        when(recursoRepository.findById(1L)).thenReturn(Optional.of(recursoEntity("Sala A")));

        DashboardResponse result = adminService.dashboard();

        assertEquals(5L, result.totalUsuarios());
        assertEquals(3L, result.totalRecursos());
        assertEquals(10L, result.totalReservas());
        assertEquals(6L, result.reservasPorEstado().get("CONFIRMADA"));
        assertEquals(2L, result.reservasHoy());
        assertEquals("Sala A", result.recursoMasReservado());
    }

    @Test
    void dashboard_sinRecursoMasReservado_retornaGuion() {
        when(userRepository.count()).thenReturn(0L);
        when(recursoRepository.count()).thenReturn(0L);
        when(reservaRepository.count()).thenReturn(0L);
        when(reservaRepository.countByEstado(any())).thenReturn(0L);
        when(reservaRepository.countByFechaInicioBetween(any(), any())).thenReturn(0L);
        when(reservaRepository.findTopRecursoId()).thenReturn(Optional.empty());

        DashboardResponse result = adminService.dashboard();

        assertEquals("—", result.recursoMasReservado());
    }

    @Test
    void listarUsuarios_devuelvePage() {
        var pageable = PageRequest.of(0, 20);
        var usuario = userEntity(1L, "Juan", Role.USER);
        when(userRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(usuario), pageable, 1));

        Page<UsuarioAdminResponse> result = adminService.listarUsuarios(pageable);

        assertEquals(1, result.getContent().size());
        assertEquals("Juan", result.getContent().getFirst().nombre());
    }

    @Test
    void obtenerUsuario_porIdExistente_retornaResponse() {
        var usuario = userEntity(1L, "Ana", Role.ADMIN);
        when(userRepository.findById(1L)).thenReturn(Optional.of(usuario));

        var result = adminService.obtenerUsuario(1L);

        assertEquals("Ana", result.nombre());
        assertEquals("ADMIN", result.rol());
    }

    @Test
    void obtenerUsuario_porIdInexistente_lanzaException() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> adminService.obtenerUsuario(99L));
    }

    @Test
    void crearUsuario_emailDuplicado_lanzaException() {
        var request = new CrearUsuarioAdminRequest("Juan", "juan@mail.com", "pass", Role.USER);
        when(userRepository.existsByEmail("juan@mail.com")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> adminService.crearUsuario(request));
        verify(userRepository, never()).save(any());
    }

    @Test
    void crearUsuario_happyPath_retornaResponse() {
        var request = new CrearUsuarioAdminRequest("Juan", "juan@mail.com", "pass", Role.USER);
        var entity = userEntity(1L, "Juan", Role.USER);

        when(passwordEncoder.encode("pass")).thenReturn("encoded");
        when(userRepository.existsByEmail("juan@mail.com")).thenReturn(false);
        when(userRepository.save(any())).thenReturn(entity);

        var result = adminService.crearUsuario(request);

        assertEquals("Juan", result.nombre());
        assertEquals("USER", result.rol());
    }

    @Test
    void actualizarUsuario_camposNulosNoSobrescribe() {
        var entity = userEntity(1L, "Juan", Role.USER);
        var request = new ActualizarUsuarioAdminRequest(null, "nuevo@mail.com", null);

        when(userRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(userRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        var result = adminService.actualizarUsuario(1L, request);

        assertEquals("nuevo@mail.com", result.email());
        assertEquals("USER", result.rol());
    }

    @Test
    void actualizarUsuario_inexistente_lanzaException() {
        var request = new ActualizarUsuarioAdminRequest("Nuevo", null, null);
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> adminService.actualizarUsuario(99L, request));
    }

    @Test
    void eliminarUsuario_existente_elimina() {
        when(userRepository.existsById(1L)).thenReturn(true);

        adminService.eliminarUsuario(1L);

        verify(userRepository).deleteById(1L);
    }

    @Test
    void eliminarUsuario_inexistente_lanzaException() {
        when(userRepository.existsById(99L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> adminService.eliminarUsuario(99L));
        verify(userRepository, never()).deleteById(any());
    }

    @Test
    void listarRecursos_devuelvePage() {
        var pageable = PageRequest.of(0, 20);
        var entity = new RecursoEntity("Sala A", TipoRecurso.SALA, "Desc", 10);
        entity.setId(1L);
        when(recursoRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(entity), pageable, 1));

        var result = adminService.listarRecursos(pageable);

        assertEquals(1, result.getContent().size());
        assertEquals("Sala A", result.getContent().getFirst().nombre());
    }

    @Test
    void obtenerRecurso_porIdInexistente_lanzaException() {
        when(recursoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> adminService.obtenerRecurso(99L));
    }

    @Test
    void eliminarRecurso_existente_elimina() {
        when(recursoRepository.existsById(1L)).thenReturn(true);

        adminService.eliminarRecurso(1L);

        verify(recursoRepository).deleteById(1L);
    }

    @Test
    void eliminarRecurso_inexistente_lanzaException() {
        when(recursoRepository.existsById(99L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> adminService.eliminarRecurso(99L));
    }

    @Test
    void crearRecurso_delega() {
        var request = new CrearRecursoRequest("Sala B", TipoRecurso.SALA, "Desc", 5);
        var response = new RecursoResponse(2L, "Sala B", TipoRecurso.SALA, "Desc", 5, true);
        when(recursoService.crear(request)).thenReturn(response);

        var result = adminService.crearRecurso(request);

        assertEquals(2L, result.id());
        assertEquals("Sala B", result.nombre());
    }

    @Test
    void actualizarRecurso_delega() {
        var request = new ActualizarRecursoRequest("Sala B", null, null, null);
        var response = new RecursoResponse(1L, "Sala B", TipoRecurso.SALA, "Desc", 5, true);
        when(recursoService.actualizar(1L, request)).thenReturn(response);

        var result = adminService.actualizarRecurso(1L, request);

        assertEquals("Sala B", result.nombre());
    }

    @Test
    void cancelarReserva_delega() {
        adminService.cancelarReserva(1L, 2L);

        verify(reservaService).cancelar(1L, 2L);
    }

    @Test
    void eliminarSolicitudCola_delega() {
        adminService.eliminarSolicitudCola(1L);

        verify(solicitudColaService).eliminar(1L);
    }

    @Test
    void listarSolicitudesCola_delega() {
        var pageable = PageRequest.of(0, 20);
        when(solicitudColaService.listarTodas(pageable)).thenReturn(Page.empty());

        var result = adminService.listarSolicitudesCola(pageable);

        assertTrue(result.isEmpty());
    }

    @Test
    void obtenerHistorial_reservaExistente_retornaLista() {
        var historial = new HistorialReserva(1L, 1L, 1L, "CREACION", "Reserva creada", LocalDateTime.now());
        when(reservaRepository.existsById(1L)).thenReturn(true);
        when(historialService.obtenerHistorial(1L)).thenReturn(List.of(historial));

        var result = adminService.obtenerHistorial(1L);

        assertEquals(1, result.size());
        assertEquals("CREACION", result.getFirst().tipoEvento());
    }

    @Test
    void obtenerHistorial_reservaInexistente_lanzaException() {
        when(reservaRepository.existsById(99L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> adminService.obtenerHistorial(99L));
    }

    private UserEntity userEntity(Long id, String nombre, Role rol) {
        var user = new UserEntity(nombre, nombre.toLowerCase() + "@mail.com", "encoded", rol);
        user.setId(id);
        return user;
    }

    private RecursoEntity recursoEntity(String nombre) {
        var entity = new RecursoEntity(nombre, TipoRecurso.SALA, "Desc", 10);
        entity.setId(1L);
        return entity;
    }
}
