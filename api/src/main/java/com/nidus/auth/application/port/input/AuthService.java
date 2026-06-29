package com.nidus.auth.application.port.input;

import com.nidus.auth.application.dto.AuthResponse;
import com.nidus.auth.application.dto.LoginRequest;
import com.nidus.auth.application.dto.RegisterRequest;

public interface AuthService {
    AuthResponse registrar(RegisterRequest request);
    AuthResponse login(LoginRequest request);
}
