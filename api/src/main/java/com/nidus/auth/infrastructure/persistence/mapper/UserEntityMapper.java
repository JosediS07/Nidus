package com.nidus.auth.infrastructure.persistence.mapper;

import com.nidus.auth.domain.User;
import com.nidus.auth.infrastructure.persistence.entity.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class UserEntityMapper {

    public UserEntity toEntity(User domain) {
        if (domain == null) return null;

        var entity = new UserEntity(
                domain.getNombre(),
                domain.getEmail(),
                domain.getPassword(),
                domain.getRol()
        );
        entity.setId(domain.getId());
        entity.setActivo(domain.isActivo());
        return entity;
    }

    public User toDomain(UserEntity entity) {
        if (entity == null) return null;

        var domain = new User(
                entity.getNombre(),
                entity.getEmail(),
                entity.getPassword(),
                entity.getRol()
        );
        domain.setId(entity.getId());
        domain.setActivo(entity.isActivo());
        return domain;
    }
}
