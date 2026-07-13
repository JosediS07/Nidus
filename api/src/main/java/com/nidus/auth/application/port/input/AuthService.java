package com.nidus.auth.application.port.input;

import com.nidus.auth.application.dto.AuthResponse;
import com.nidus.auth.application.dto.CambiarRolRequest;
import com.nidus.auth.application.dto.LoginRequest;
import com.nidus.auth.application.dto.RegisterRequest;
import com.nidus.auth.application.dto.UserResponse;

public interface AuthService {
    AuthResponse registrar(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    void cambiarRol(Long userId, CambiarRolRequest request);
    UserResponse obtenerPerfil(String email);
    UserResponse actualizarPerfil(String email, ActualizarPerfilRequest request);
}
