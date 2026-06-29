package com.nidus.auth.application.port.output;

import com.nidus.auth.domain.User;

import java.util.Optional;

public interface UserRepository {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    User save(User user);
}
