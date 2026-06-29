package com.nidus.auth.application.port.output;

public interface TokenService {
    String generarToken(String email, String rol);
}
