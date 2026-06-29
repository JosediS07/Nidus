# 001 · Autenticación y registro — Tareas

### Dependencias

- [ ] Agregar security, validation, jjwt al pom.xml.

### Modelo y repositorio

- [ ] Crear `Role`, `User`, `UserRepository`.

### DTOs

- [ ] Crear `RegisterRequest`, `LoginRequest`, `AuthResponse`.

### Servicios

- [ ] Crear `JwtService`, `AuthService`.

### Web

- [ ] Crear `AuthController`, `JwtAuthenticationFilter`.

### Shared

- [ ] Crear `SecurityConfig`, `DataInitializer`, `DuplicateResourceException`, `GlobalExceptionHandler`.

### Pruebas

- [ ] Escribir `AuthServiceTest`, `JwtServiceTest`, `UserRepositoryTest`, `AuthControllerTest`.
- [ ] Validar `./mvnw test` completo.
