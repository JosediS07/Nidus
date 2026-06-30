package com.nidus.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nidus.auth.application.dto.AuthResponse;
import com.nidus.auth.application.dto.CambiarRolRequest;
import com.nidus.auth.application.dto.LoginRequest;
import com.nidus.auth.application.dto.RegisterRequest;
import com.nidus.auth.application.port.input.AuthService;
import com.nidus.shared.exception.DuplicateResourceException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void register_201() throws Exception {
        var request = new RegisterRequest("Juan", "juan@mail.com", "123456");
        var response = new AuthResponse("token", "Juan", "juan@mail.com", "USER");

        when(authService.registrar(any())).thenReturn(response);

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").value("token"))
                .andExpect(jsonPath("$.nombre").value("Juan"))
                .andExpect(jsonPath("$.email").value("juan@mail.com"))
                .andExpect(jsonPath("$.rol").value("USER"));
    }

    @Test
    void register_409_emailDuplicado() throws Exception {
        var request = new RegisterRequest("Juan", "juan@mail.com", "123456");

        when(authService.registrar(any()))
                .thenThrow(new DuplicateResourceException("El email juan@mail.com ya está registrado"));

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("conflict"));
    }

    @Test
    void login_200() throws Exception {
        var request = new LoginRequest("juan@mail.com", "123456");
        var response = new AuthResponse("token", "Juan", "juan@mail.com", "USER");

        when(authService.login(any())).thenReturn(response);

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("token"));
    }

    @Test
    void login_401_credencialesInvalidas() throws Exception {
        var request = new LoginRequest("juan@mail.com", "wrong");

        when(authService.login(any()))
                .thenThrow(new BadCredentialsException("Credenciales inválidas"));

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("unauthorized"));
    }

    @Test
    void register_400_datosInvalidos() throws Exception {
        var request = new RegisterRequest("", "invalido", "12");

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void cambiarRol_200() throws Exception {
        var request = new CambiarRolRequest(com.nidus.auth.domain.Role.ADMIN);

        mockMvc.perform(put("/api/v1/auth/usuarios/1/rol")
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void cambiarRol_403_usuarioNoAdmin() throws Exception {
        var request = new CambiarRolRequest(com.nidus.auth.domain.Role.ADMIN);

        mockMvc.perform(put("/api/v1/auth/usuarios/1/rol")
                        .with(user("user").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }
}
