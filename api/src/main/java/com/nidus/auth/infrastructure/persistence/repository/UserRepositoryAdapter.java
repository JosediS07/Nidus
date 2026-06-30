package com.nidus.auth.infrastructure.persistence.repository;

import com.nidus.auth.application.port.output.UserRepository;
import com.nidus.auth.domain.User;
import com.nidus.auth.infrastructure.persistence.mapper.UserEntityMapper;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class UserRepositoryAdapter implements UserRepository {

    private final JpaUserRepository jpaUserRepository;
    private final UserEntityMapper mapper;

    public UserRepositoryAdapter(JpaUserRepository jpaUserRepository, UserEntityMapper mapper) {
        this.jpaUserRepository = jpaUserRepository;
        this.mapper = mapper;
    }

    @Override
    public Optional<User> findById(Long id) {
        return jpaUserRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return jpaUserRepository.findByEmail(email).map(mapper::toDomain);
    }

    @Override
    public boolean existsByEmail(String email) {
        return jpaUserRepository.existsByEmail(email);
    }

    @Override
    public User save(User user) {
        var entity = mapper.toEntity(user);
        var saved = jpaUserRepository.save(entity);
        return mapper.toDomain(saved);
    }
}
