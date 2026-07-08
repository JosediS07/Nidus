package com.nidus.admin;

import com.nidus.admin.application.dto.DashboardResponse;
import com.nidus.admin.application.dto.ReservaAdminResponse;
import com.nidus.admin.application.dto.UsuarioAdminResponse;
import com.nidus.admin.application.service.AdminService;
import com.nidus.reserva.infrastructure.persistence.entity.HistorialReservaEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AdminService adminService;

    @Test
    void dashboard_200() throws Exception {
        var response = new DashboardResponse(5, 10, 100,
                Map.of("CONFIRMADA", 60L, "CANCELADA", 30L, "MODIFICADA", 10L),
                3, "Sala A");

        when(adminService.dashboard()).thenReturn(response);

        mockMvc.perform(get("/api/v1/admin/dashboard").with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalUsuarios").value(5))
                .andExpect(jsonPath("$.totalReservas").value(100))
                .andExpect(jsonPath("$.recursoMasReservado").value("Sala A"));
    }

    @Test
    void dashboard_403_noAdmin() throws Exception {
        mockMvc.perform(get("/api/v1/admin/dashboard").with(user("user").roles("USER")))
                .andExpect(status().isForbidden());
    }

    @Test
    void listarUsuarios_200() throws Exception {
        var users = List.of(
                new UsuarioAdminResponse(1L, "Admin", "admin@mail.com", "ADMIN", true, LocalDateTime.now()),
                new UsuarioAdminResponse(2L, "Juan", "juan@mail.com", "USER", true, LocalDateTime.now()));

        var page = new PageImpl<>(users, PageRequest.of(0, 20), 2);
        when(adminService.listarUsuarios(any())).thenReturn(page);

        mockMvc.perform(get("/api/v1/admin/usuarios").with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].email").value("admin@mail.com"))
                .andExpect(jsonPath("$.totalElements").value(2));
    }

    @Test
    void listarUsuarios_403_noAdmin() throws Exception {
        mockMvc.perform(get("/api/v1/admin/usuarios").with(user("user").roles("USER")))
                .andExpect(status().isForbidden());
    }

    @Test
    void obtenerUsuario_200() throws Exception {
        var user = new UsuarioAdminResponse(1L, "Admin", "admin@mail.com", "ADMIN", true, LocalDateTime.now());

        when(adminService.obtenerUsuario(1L)).thenReturn(user);

        mockMvc.perform(get("/api/v1/admin/usuarios/1").with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Admin"))
                .andExpect(jsonPath("$.rol").value("ADMIN"));
    }

    @Test
    void listarReservas_200() throws Exception {
        var reservas = List.of(
                new ReservaAdminResponse(1L, 1L, 1L,
                        LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), "CONFIRMADA"));

        when(adminService.listarReservas(null, null, null, null, null)).thenReturn(reservas);

        mockMvc.perform(get("/api/v1/admin/reservas").with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    void listarReservas_403_noAdmin() throws Exception {
        mockMvc.perform(get("/api/v1/admin/reservas").with(user("user").roles("USER")))
                .andExpect(status().isForbidden());
    }

    @Test
    void obtenerReserva_200() throws Exception {
        var reserva = new ReservaAdminResponse(1L, 1L, 1L,
                LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), "CONFIRMADA");

        when(adminService.obtenerReserva(1L)).thenReturn(reserva);

        mockMvc.perform(get("/api/v1/admin/reservas/1").with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("CONFIRMADA"));
    }

    @Test
    void obtenerReserva_403_noAdmin() throws Exception {
        mockMvc.perform(get("/api/v1/admin/reservas/1").with(user("user").roles("USER")))
                .andExpect(status().isForbidden());
    }

    @Test
    void obtenerHistorial_200() throws Exception {
        var eventos = List.of(
                new HistorialReservaEntity(1L, 1L, "CREACION", "Reserva creada"),
                new HistorialReservaEntity(1L, 1L, "MODIFICACION", "Reserva modificada"));

        when(adminService.obtenerHistorial(1L)).thenReturn(eventos);

        mockMvc.perform(get("/api/v1/admin/reservas/1/historial").with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].tipoEvento").value("CREACION"));
    }

    @Test
    void obtenerHistorial_403_noAdmin() throws Exception {
        mockMvc.perform(get("/api/v1/admin/reservas/1/historial").with(user("user").roles("USER")))
                .andExpect(status().isForbidden());
    }
}
