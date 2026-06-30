package com.nidus.recurso;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nidus.recurso.application.dto.ActualizarRecursoRequest;
import com.nidus.recurso.application.dto.CrearRecursoRequest;
import com.nidus.recurso.application.dto.RecursoResponse;
import com.nidus.recurso.application.port.input.RecursoService;
import com.nidus.recurso.domain.TipoRecurso;
import com.nidus.shared.exception.InvalidStateException;
import com.nidus.shared.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class RecursoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RecursoService recursoService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void listar_200() throws Exception {
        var response = new RecursoResponse(1L, "Sala A", TipoRecurso.SALA, "Sala principal", 10, true);
        when(recursoService.listar()).thenReturn(List.of(response));

        mockMvc.perform(get("/api/v1/recursos").with(user("user")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Sala A"))
                .andExpect(jsonPath("$[0].tipo").value("SALA"));
    }

    @Test
    void obtener_200() throws Exception {
        var response = new RecursoResponse(1L, "Proyector X", TipoRecurso.PROYECTOR, null, null, true);
        when(recursoService.obtener(1L)).thenReturn(response);

        mockMvc.perform(get("/api/v1/recursos/1").with(user("user")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Proyector X"))
                .andExpect(jsonPath("$.tipo").value("PROYECTOR"));
    }

    @Test
    void obtener_404() throws Exception {
        when(recursoService.obtener(99L)).thenThrow(new ResourceNotFoundException("Recurso con id 99 no encontrado"));

        mockMvc.perform(get("/api/v1/recursos/99").with(user("user")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("not_found"));
    }

    @Test
    void crear_201() throws Exception {
        var request = new CrearRecursoRequest("Sala B", TipoRecurso.SALA, "Nueva sala", 8);
        var response = new RecursoResponse(2L, "Sala B", TipoRecurso.SALA, "Nueva sala", 8, true);

        when(recursoService.crear(any())).thenReturn(response);

        mockMvc.perform(post("/api/v1/recursos")
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre").value("Sala B"))
                .andExpect(jsonPath("$.tipo").value("SALA"));
    }

    @Test
    void actualizar_200() throws Exception {
        var request = new ActualizarRecursoRequest("Sala B editada", null, null, null);
        var response = new RecursoResponse(2L, "Sala B editada", TipoRecurso.SALA, "Nueva sala", 8, true);

        when(recursoService.actualizar(any(), any())).thenReturn(response);

        mockMvc.perform(put("/api/v1/recursos/2")
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Sala B editada"));
    }

    @Test
    void desactivar_204() throws Exception {
        mockMvc.perform(delete("/api/v1/recursos/1")
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isNoContent());
    }

    @Test
    void desactivar_409_recursoYaInactivo() throws Exception {
        doThrow(new InvalidStateException("El recurso con id 1 ya está inactivo"))
                .when(recursoService).desactivar(1L);

        mockMvc.perform(delete("/api/v1/recursos/1")
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("conflict"));
    }

    @Test
    void crear_403_usuarioNoAdmin() throws Exception {
        var request = new CrearRecursoRequest("Sala B", TipoRecurso.SALA, null, null);

        mockMvc.perform(post("/api/v1/recursos")
                        .with(user("user").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    void actualizar_403_usuarioNoAdmin() throws Exception {
        var request = new ActualizarRecursoRequest("Sala B", null, null, null);

        mockMvc.perform(put("/api/v1/recursos/1")
                        .with(user("user").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    void desactivar_403_usuarioNoAdmin() throws Exception {
        mockMvc.perform(delete("/api/v1/recursos/1")
                        .with(user("user").roles("USER")))
                .andExpect(status().isForbidden());
    }

    @Test
    void crear_400_datosInvalidos() throws Exception {
        var request = new CrearRecursoRequest("", null, null, null);

        mockMvc.perform(post("/api/v1/recursos")
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
