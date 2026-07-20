package com.nidus.auth.infrastructure.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @Mock
    private Claims claims;

    private JwtAuthenticationFilter filter;

    @BeforeEach
    void setUp() {
        filter = new JwtAuthenticationFilter(jwtService);
        SecurityContextHolder.clearContext();
    }

    @Test
    void sinHeader_pasaAlFiltro() throws Exception {
        when(request.getHeader("Authorization")).thenReturn(null);

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void headerNoBearer_pasaAlFiltro() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Basic dXNlcjpwYXNz");

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void bearerTokenInvalido_pasaAlFiltro() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Bearer token-invalido");
        when(jwtService.esValido("token-invalido")).thenReturn(false);

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void bearerTokenValido_estableceAutenticacion() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Bearer token-valido");
        when(jwtService.esValido("token-valido")).thenReturn(true);
        when(jwtService.extraerEmail("token-valido")).thenReturn("admin@mail.com");
        when(jwtService.extraerClaims("token-valido")).thenReturn(claims);
        when(claims.get("rol", String.class)).thenReturn("ADMIN");

        filter.doFilterInternal(request, response, filterChain);

        var auth = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(auth);
        assertEquals("admin@mail.com", auth.getPrincipal());
        assertTrue(auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void bearerTokenValido_conectaDetalles() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Bearer token-valido");
        when(jwtService.esValido("token-valido")).thenReturn(true);
        when(jwtService.extraerEmail("token-valido")).thenReturn("user@mail.com");
        when(jwtService.extraerClaims("token-valido")).thenReturn(claims);
        when(claims.get("rol", String.class)).thenReturn("USER");

        filter.doFilterInternal(request, response, filterChain);

        var auth = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(auth);
        assertNotNull(auth.getDetails());
    }
}
