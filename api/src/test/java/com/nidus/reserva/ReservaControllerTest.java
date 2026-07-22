package com.nidus.reserva;

import com.nidus.auth.application.port.output.UserRepository;
import com.nidus.auth.domain.Role;
import com.nidus.auth.domain.User;
import com.nidus.reserva.application.dto.ReservaResponse;
import com.nidus.reserva.application.port.input.ReservaService;
import com.nidus.shared.exception.InvalidStateException;
import com.nidus.shared.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Tag("integration")
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ReservaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ReservaService reservaService;

    @MockitoBean
    private UserRepository userRepository;

    private final LocalDateTime maniana = LocalDateTime.now().plusDays(1);
    private final LocalDateTime pasadoManiana = LocalDateTime.now().plusDays(2);

    private User usuarioConId(Long id) {
        var user = new User("User", "user@mail.com", "hash", Role.USER);
        user.setId(id);
        return user;
    }

    @Test
    void crear_201() throws Exception {
        var response = new ReservaResponse(1L, 1L, 1L, maniana, pasadoManiana, "CONFIRMADA", "User", "Sala A");

        when(userRepository.findByEmail("user@mail.com")).thenReturn(Optional.of(usuarioConId(1L)));
        when(reservaService.crear(any(), eq(1L))).thenReturn(response);

        var json = jsonCrear(1L, maniana, pasadoManiana);

        mockMvc.perform(post("/api/v1/reservas")
                        .with(user("user@mail.com").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.recursoId").value(1L))
                .andExpect(jsonPath("$.estado").value("CONFIRMADA"));
    }

    @Test
    void crear_409_solapamiento() throws Exception {
        when(userRepository.findByEmail("user@mail.com")).thenReturn(Optional.of(usuarioConId(1L)));
        when(reservaService.crear(any(), eq(1L))).thenThrow(new InvalidStateException("El recurso ya está reservado en ese horario"));

        var json = jsonCrear(1L, maniana, pasadoManiana);

        mockMvc.perform(post("/api/v1/reservas")
                        .with(user("user@mail.com").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("invalid_state"));
    }

    @Test
    void crear_400_datosInvalidos() throws Exception {
        mockMvc.perform(post("/api/v1/reservas")
                        .with(user("user@mail.com").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"recursoId\": null, \"fechaInicio\": null, \"fechaFin\": null}"))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void listarMisReservas_200() throws Exception {
        var reserva = new ReservaResponse(1L, 1L, 1L, maniana, pasadoManiana, "CONFIRMADA", "User", "Sala A");
        var page = new PageImpl<>(List.of(reserva), PageRequest.of(0, 20), 1);

        when(userRepository.findByEmail("user@mail.com")).thenReturn(Optional.of(usuarioConId(1L)));
        when(reservaService.listarPorUsuario(1L, PageRequest.of(0, 20))).thenReturn(page);

        mockMvc.perform(get("/api/v1/reservas").with(user("user@mail.com").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1L))
                .andExpect(jsonPath("$.content[0].estado").value("CONFIRMADA"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void listarTodas_200_admin() throws Exception {
        var reserva = new ReservaResponse(1L, 1L, 1L, maniana, pasadoManiana, "CONFIRMADA", "User", "Sala A");
        var page = new PageImpl<>(List.of(reserva), PageRequest.of(0, 20), 1);

        when(reservaService.listarTodas(PageRequest.of(0, 20))).thenReturn(page);

        mockMvc.perform(get("/api/v1/reservas/todas").with(user("admin@mail.com").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1L))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void listarTodas_403_noAdmin() throws Exception {
        mockMvc.perform(get("/api/v1/reservas/todas").with(user("user@mail.com").roles("USER")))
                .andExpect(status().isForbidden());
    }

    @Test
    void obtener_200() throws Exception {
        var reserva = new ReservaResponse(1L, 1L, 1L, maniana, pasadoManiana, "CONFIRMADA", "User", "Sala A");

        when(userRepository.findByEmail("user@mail.com")).thenReturn(Optional.of(usuarioConId(1L)));
        when(reservaService.obtenerPorId(1L, 1L)).thenReturn(reserva);

        mockMvc.perform(get("/api/v1/reservas/1").with(user("user@mail.com").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void obtener_404() throws Exception {
        when(userRepository.findByEmail("user@mail.com")).thenReturn(Optional.of(usuarioConId(1L)));
        when(reservaService.obtenerPorId(99L, 1L)).thenThrow(new ResourceNotFoundException("Reserva con id 99 no encontrada"));

        mockMvc.perform(get("/api/v1/reservas/99").with(user("user@mail.com").roles("USER")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("not_found"));
    }

    @Test
    void modificar_200() throws Exception {
        var response = new ReservaResponse(1L, 1L, 1L, maniana, pasadoManiana, "MODIFICADA", "User", "Sala A");

        when(userRepository.findByEmail("user@mail.com")).thenReturn(Optional.of(usuarioConId(1L)));
        when(reservaService.modificar(any(), any(), eq(1L))).thenReturn(response);

        var json = jsonModificar(maniana, pasadoManiana);

        mockMvc.perform(put("/api/v1/reservas/1")
                        .with(user("user@mail.com").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("MODIFICADA"));
    }

    @Test
    void cancelar_204() throws Exception {
        when(userRepository.findByEmail("user@mail.com")).thenReturn(Optional.of(usuarioConId(1L)));

        mockMvc.perform(delete("/api/v1/reservas/1").with(user("user@mail.com").roles("USER")))
                .andExpect(status().isNoContent());
    }

    @Test
    void cancelar_409_yaCancelada() throws Exception {
        when(userRepository.findByEmail("user@mail.com")).thenReturn(Optional.of(usuarioConId(1L)));
        doThrow(new InvalidStateException("La reserva con id 1 ya está cancelada"))
                .when(reservaService).cancelar(1L, 1L);

        mockMvc.perform(delete("/api/v1/reservas/1").with(user("user@mail.com").roles("USER")))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("invalid_state"));
    }

    @Test
    void modificar_403_otroUsuario() throws Exception {
        when(userRepository.findByEmail("user@mail.com")).thenReturn(Optional.of(usuarioConId(1L)));
        when(reservaService.modificar(any(), any(), eq(1L)))
                .thenThrow(new InvalidStateException("No tienes permiso para modificar esta reserva"));

        var json = jsonModificar(maniana, pasadoManiana);

        mockMvc.perform(put("/api/v1/reservas/1")
                        .with(user("user@mail.com").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("invalid_state"));
    }

    private String jsonCrear(Long recursoId, LocalDateTime inicio, LocalDateTime fin) {
        return "{\"recursoId\": " + recursoId + ", \"fechaInicio\": \"" + inicio + "\", \"fechaFin\": \"" + fin + "\"}";
    }

    private String jsonModificar(LocalDateTime inicio, LocalDateTime fin) {
        return "{\"fechaInicio\": \"" + inicio + "\", \"fechaFin\": \"" + fin + "\"}";
    }
}
