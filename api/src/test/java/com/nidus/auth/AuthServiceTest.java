package com.nidus.auth;

import com.nidus.auth.dto.LoginRequest;
import com.nidus.auth.dto.RegisterRequest;
import com.nidus.auth.model.Role;
import com.nidus.auth.model.User;
import com.nidus.auth.repository.UserRepository;
import com.nidus.auth.service.AuthService;
import com.nidus.auth.service.JwtService;
import com.nidus.shared.exception.DuplicateResourceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthService(userRepository, passwordEncoder, jwtService);
    }

    @Test
    void registrar_creaUsuarioYRetornaToken() {
        var request = new RegisterRequest("Juan", "juan@mail.com", "123456");
        when(userRepository.existsByEmail("juan@mail.com")).thenReturn(false);
        when(passwordEncoder.encode("123456")).thenReturn("hash");
        when(jwtService.generarToken("juan@mail.com", "USER")).thenReturn("token123");

        var response = authService.registrar(request);

        assertEquals("Juan", response.nombre());
        assertEquals("juan@mail.com", response.email());
        assertEquals("USER", response.rol());
        assertEquals("token123", response.token());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void registrar_emailDuplicadoLanzaExcepcion() {
        var request = new RegisterRequest("Juan", "juan@mail.com", "123456");
        when(userRepository.existsByEmail("juan@mail.com")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> authService.registrar(request));
        verify(userRepository, never()).save(any());
    }

    @Test
    void login_conCredencialesValidasRetornaToken() {
        var request = new LoginRequest("juan@mail.com", "123456");
        var user = new User("Juan", "juan@mail.com", "hash", Role.USER);

        when(userRepository.findByEmail("juan@mail.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("123456", "hash")).thenReturn(true);
        when(jwtService.generarToken("juan@mail.com", "USER")).thenReturn("token123");

        var response = authService.login(request);

        assertEquals("Juan", response.nombre());
        assertEquals("token123", response.token());
    }

    @Test
    void login_emailNoExistenteLanzaExcepcion() {
        var request = new LoginRequest("juan@mail.com", "123456");
        when(userRepository.findByEmail("juan@mail.com")).thenReturn(Optional.empty());

        assertThrows(BadCredentialsException.class, () -> authService.login(request));
    }

    @Test
    void login_passwordIncorrectoLanzaExcepcion() {
        var request = new LoginRequest("juan@mail.com", "wrong");
        var user = new User("Juan", "juan@mail.com", "hash", Role.USER);

        when(userRepository.findByEmail("juan@mail.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "hash")).thenReturn(false);

        assertThrows(BadCredentialsException.class, () -> authService.login(request));
    }
}
