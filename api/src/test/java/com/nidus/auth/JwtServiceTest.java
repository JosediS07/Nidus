package com.nidus.auth;

import com.nidus.auth.infrastructure.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = JwtService.class)
@TestPropertySource(properties = {
    "app.jwt.secret=VGVzdFNlY3JldEZvck5pZHVzVGVzdHNPbmx5MTIzNDU2Nzg5MA==",
    "app.jwt.expiration=86400000"
})
class JwtServiceTest {

    @Autowired
    private JwtService jwtService;

    @Test
    void generarToken_retornaTokenValido() {
        var token = jwtService.generarToken("test@mail.com", "USER");
        assertNotNull(token);
        assertTrue(jwtService.esValido(token));
    }

    @Test
    void extraerEmail_desdeTokenValido() {
        var token = jwtService.generarToken("test@mail.com", "USER");
        assertEquals("test@mail.com", jwtService.extraerEmail(token));
    }

    @Test
    void esValido_tokenInvalidoRetornaFalse() {
        assertFalse(jwtService.esValido("token-invalido"));
    }
}
