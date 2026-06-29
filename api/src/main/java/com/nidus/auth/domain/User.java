package com.nidus.auth.domain;

import java.time.LocalDateTime;

public class User {

    private Long id;
    private String nombre;
    private String email;
    private String password;
    private Role rol;
    private LocalDateTime creado;
    private boolean activo = true;

    public User() {}

    public User(String nombre, String email, String password, Role rol) {
        this.nombre = nombre;
        this.email = email;
        this.password = password;
        this.rol = rol;
        this.creado = LocalDateTime.now();
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
    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
}
