# 001 · Autenticación y registro — Plan

## Enfoque

Autenticación stateless con JWT. Spring Security sin sesión (`SessionCreationPolicy.STATELESS`). Filtro personalizado (`JwtAuthenticationFilter`) extrae y valida el token del header. Passwords con BCrypt.

## Implementación

### 1. Dependencias

`spring-boot-starter-security`, `spring-boot-starter-validation`, `jjwt-api/impl/jackson 0.12.6`, `spring-security-test`.

### 2. Modelo y repositorio

`Role` (enum), `User` (entidad JPA), `UserRepository` (findByEmail, existsByEmail).

### 3. DTOs

`RegisterRequest`, `LoginRequest`, `AuthResponse`.

### 4. Servicios

`JwtService` (generar/validar token), `AuthService` (registrar/login).

### 5. Web

`AuthController` (POST /register, POST /login), `JwtAuthenticationFilter`.

### 6. Seguridad

`SecurityConfig` (CSRF off, stateless, rutas públicas, filtro JWT, BCrypt), `DataInitializer` (seed admin).

### 7. Errores

`DuplicateResourceException` (409), `GlobalExceptionHandler` (@RestControllerAdvice).

### 8. Pruebas

`AuthServiceTest` (unitario), `JwtServiceTest` (unitario), `UserRepositoryTest` (@DataJpaTest), `AuthControllerTest` (@SpringBootTest + @AutoConfigureMockMvc).
