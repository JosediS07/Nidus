package com.nidus.auth.service;

import com.nidus.auth.dto.AuthResponse;
import com.nidus.auth.dto.LoginRequest;
import com.nidus.auth.dto.RegisterRequest;
import com.nidus.auth.model.Role;
import com.nidus.auth.model.User;
import com.nidus.auth.repository.UserRepository;
import com.nidus.shared.exception.DuplicateResourceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional
    public AuthResponse registrar(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new DuplicateResourceException("El email " + request.email() + " ya está registrado");
        }

        var user = new User(request.nombre(), request.email(),
                passwordEncoder.encode(request.password()), Role.USER);
        userRepository.save(user);

        var token = jwtService.generarToken(user.getEmail(), user.getRol().name());
        return new AuthResponse(token, user.getNombre(), user.getEmail(), user.getRol().name());
    }

    public AuthResponse login(LoginRequest request) {
        var user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new BadCredentialsException("Credenciales inválidas"));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new BadCredentialsException("Credenciales inválidas");
        }

        var token = jwtService.generarToken(user.getEmail(), user.getRol().name());
        return new AuthResponse(token, user.getNombre(), user.getEmail(), user.getRol().name());
    }
}
