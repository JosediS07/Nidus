package com.nidus.auth.infrastructure.config;

import com.nidus.auth.application.port.output.UserRepository;
import com.nidus.auth.domain.Role;
import com.nidus.auth.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (!userRepository.existsByEmail("admin@nidus.com")) {
            var admin = new User("Admin", "admin@nidus.com",
                    passwordEncoder.encode("admin123"), Role.ADMIN);
            userRepository.save(admin);
            log.info("Usuario admin creado: admin@nidus.com / admin123");
        }
    }
}
