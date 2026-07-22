package com.nidus.auth;

import com.nidus.auth.domain.Role;
import com.nidus.auth.infrastructure.persistence.entity.UserEntity;
import com.nidus.auth.infrastructure.persistence.repository.JpaUserRepository;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@Tag("integration")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    private JpaUserRepository jpaUserRepository;

    @Test
    void guardarYBuscarPorEmail() {
        var user = new UserEntity("Juan", "juan@mail.com", "hash", Role.USER);
        jpaUserRepository.save(user);

        var encontrado = jpaUserRepository.findByEmail("juan@mail.com");

        assertTrue(encontrado.isPresent());
        assertEquals("Juan", encontrado.get().getNombre());
        assertEquals(Role.USER, encontrado.get().getRol());
        assertNotNull(encontrado.get().getCreado());
    }

    @Test
    void existsByEmail_retornaTrueSiExiste() {
        var user = new UserEntity("Juan", "juan@mail.com", "hash", Role.USER);
        jpaUserRepository.save(user);

        assertTrue(jpaUserRepository.existsByEmail("juan@mail.com"));
        assertFalse(jpaUserRepository.existsByEmail("otro@mail.com"));
    }
}
