package com.nidus.auth.application.service;

import com.nidus.auth.application.dto.ActualizarPerfilRequest;
import com.nidus.auth.application.dto.AuthResponse;
import com.nidus.auth.application.dto.CambiarRolRequest;
import com.nidus.auth.application.dto.LoginRequest;
import com.nidus.auth.application.dto.RegisterRequest;
import com.nidus.auth.application.dto.UserResponse;
import com.nidus.auth.application.port.input.AuthService;
import com.nidus.auth.application.port.output.TokenService;
import com.nidus.auth.application.port.output.UserRepository;
import com.nidus.auth.domain.Role;
import com.nidus.auth.domain.User;
import com.nidus.shared.exception.DuplicateResourceException;
import com.nidus.shared.exception.ResourceNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    public AuthServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, TokenService tokenService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
    }

    @Transactional
    @Override
    public AuthResponse registrar(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new DuplicateResourceException("El email " + request.email() + " ya está registrado");
        }

        var user = new User(request.nombre(), request.email(),
                passwordEncoder.encode(request.password()), Role.USER);
        userRepository.save(user);

        var token = tokenService.generarToken(user.getEmail(), user.getRol().name());
        return new AuthResponse(user.getId(), token, user.getNombre(), user.getEmail(), user.getRol().name());
    }

    @Transactional(readOnly = true)
    @Override
    public AuthResponse login(LoginRequest request) {
        var user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new BadCredentialsException("Credenciales inválidas"));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new BadCredentialsException("Credenciales inválidas");
        }

        var token = tokenService.generarToken(user.getEmail(), user.getRol().name());
        return new AuthResponse(user.getId(), token, user.getNombre(), user.getEmail(), user.getRol().name());
    }

    @Transactional(readOnly = true)
    @Override
    public UserResponse obtenerPerfil(String email) {
        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        return new UserResponse(user.getId(), user.getNombre(), user.getEmail(),
                user.getRol().name(), user.isActivo());
    }

    @Transactional
    @Override
    public void cambiarRol(Long userId, CambiarRolRequest request) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario con id " + userId + " no encontrado"));
        user.setRol(request.rol());
        userRepository.save(user);
    }

    @Transactional
    @Override
    public UserResponse actualizarPerfil(String email, ActualizarPerfilRequest request) {
        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        if (request.currentPassword() != null && request.password() != null) {
            if (!passwordEncoder.matches(request.currentPassword(), user.getPassword())) {
                throw new BadCredentialsException("Contraseña actual incorrecta");
            }
            user.setPassword(passwordEncoder.encode(request.password()));
        }

        if (request.nombre() != null) user.setNombre(request.nombre());
        if (request.email() != null) user.setEmail(request.email());

        var guardado = userRepository.save(user);
        return new UserResponse(guardado.getId(), guardado.getNombre(), guardado.getEmail(),
                guardado.getRol().name(), guardado.isActivo());
    }
}
