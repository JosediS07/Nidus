# 001 · Autenticación y registro

**Estado:** pendiente

## Qué hace

El usuario puede registrarse en la plataforma y posteriormente iniciar sesión para obtener un token JWT. Este token se envía en el header `Authorization: Bearer <token>` para autenticar las peticiones posteriores. El sistema reconoce dos roles: `ADMIN` y `USER`.

## Por qué

Es la puerta de entrada a toda la plataforma. Sin autenticación no se puede reservar, gestionar recursos ni administrar el sistema. Además, los roles son necesarios para aplicar permisos diferenciados.

## Criterios de aceptación

- [ ] `POST /api/v1/auth/register` recibe `{ nombre, email, password }` y crea un usuario con rol `USER` por defecto. Responde con el token JWT y los datos del usuario (sin password).
- [ ] `POST /api/v1/auth/login` recibe `{ email, password }`, valida credenciales y responde con un token JWT y datos del usuario.
- [ ] Si el email ya está registrado, `POST /api/v1/auth/register` responde `409 Conflict` con mensaje claro.
- [ ] Si las credenciales son inválidas, `POST /api/v1/auth/login` responde `401 Unauthorized`.
- [ ] Los endpoints `/api/v1/auth/**` son públicos (no requieren token).
- [ ] Cualquier endpoint fuera de `/api/v1/auth/**` rechaza peticiones sin token o con token inválido/expirado con `401`.
- [ ] El password se almacena hasheado con BCrypt.
- [ ] Las respuestas de error siguen el formato `{ error, message, status, timestamp }`.

## Fuera de alcance

- Refresh tokens.
- Confirmación de email por correo.
- Recuperación de contraseña.
- OAuth2.
