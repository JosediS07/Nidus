package com.nidus.auth;

import com.nidus.auth.model.Role;
import com.nidus.auth.model.User;
import com.nidus.auth.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void guardarYBuscarPorEmail() {
        var user = new User("Juan", "juan@mail.com", "hash", Role.USER);
        userRepository.save(user);

        var encontrado = userRepository.findByEmail("juan@mail.com");

        assertTrue(encontrado.isPresent());
        assertEquals("Juan", encontrado.get().getNombre());
        assertEquals(Role.USER, encontrado.get().getRol());
    }

    @Test
    void existsByEmail_retornaTrueSiExiste() {
        var user = new User("Juan", "juan@mail.com", "hash", Role.USER);
        userRepository.save(user);

        assertTrue(userRepository.existsByEmail("juan@mail.com"));
        assertFalse(userRepository.existsByEmail("otro@mail.com"));
    }
}
