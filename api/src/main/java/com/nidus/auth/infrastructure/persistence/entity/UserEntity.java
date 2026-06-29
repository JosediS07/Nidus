package com.nidus.auth.infrastructure.persistence.entity;

import com.nidus.auth.domain.Role;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role rol;

    @Column(nullable = false, updatable = false)
    private LocalDateTime creado;

    @Column(nullable = false)
    private boolean activo = true;

    @PrePersist
    protected void onCreate() {
        if (creado == null) {
            this.creado = LocalDateTime.now();
        }
    }

    public UserEntity() {}

    public UserEntity(String nombre, String email, String password, Role rol) {
        this.nombre = nombre;
        this.email = email;
        this.password = password;
        this.rol = rol;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public Role getRol() { return rol; }
    public void setRol(Role rol) { this.rol = rol; }
    public LocalDateTime getCreado() { return creado; }
    public void setCreado(LocalDateTime creado) { this.creado = creado; }
    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
}
