package com.nidus.shared;

import com.nidus.shared.exception.DuplicateResourceException;
import com.nidus.shared.exception.GlobalExceptionHandler;
import com.nidus.shared.exception.InvalidStateException;
import com.nidus.shared.exception.ResourceNotFoundException;
import com.nidus.shared.exception.dto.ErrorResponse;
import com.nidus.shared.exception.dto.ValidationError;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    void duplicateResource_retorna409() {
        var ex = new DuplicateResourceException("El email ya existe");

        ResponseEntity<ErrorResponse> result = handler.handleDuplicate(ex);

        assertEquals(HttpStatus.CONFLICT, result.getStatusCode());
        assertEquals("duplicate_resource", result.getBody().error());
        assertEquals("El email ya existe", result.getBody().message());
    }

    @Test
    void badCredentials_retorna401() {
        var ex = new BadCredentialsException("Credenciales inválidas");

        ResponseEntity<ErrorResponse> result = handler.handleBadCredentials(ex);

        assertEquals(HttpStatus.UNAUTHORIZED, result.getStatusCode());
        assertEquals("unauthorized", result.getBody().error());
    }

    @Test
    void notFound_retorna404() {
        var ex = new ResourceNotFoundException("Usuario no encontrado");

        ResponseEntity<ErrorResponse> result = handler.handleNotFound(ex);

        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertEquals("not_found", result.getBody().error());
        assertEquals("Usuario no encontrado", result.getBody().message());
    }

    @Test
    void invalidState_retorna409() {
        var ex = new InvalidStateException("Estado inválido");

        ResponseEntity<ErrorResponse> result = handler.handleInvalidState(ex);

        assertEquals(HttpStatus.CONFLICT, result.getStatusCode());
        assertEquals("invalid_state", result.getBody().error());
    }

    @Test
    void accessDenied_retorna403() {
        var ex = new AccessDeniedException("Acceso denegado");

        ResponseEntity<ErrorResponse> result = handler.handleAccessDenied(ex);

        assertEquals(HttpStatus.FORBIDDEN, result.getStatusCode());
        assertEquals("forbidden", result.getBody().error());
    }

    @Test
    void validation_retorna422_conErrores() {
        var fieldError = new FieldError("object", "email", "valor-invalido", false, null, null, "Email inválido");
        var ex = mock(MethodArgumentNotValidException.class);
        var bindingResult = mock(org.springframework.validation.BindingResult.class);

        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));

        ResponseEntity<ErrorResponse> result = handler.handleValidation(ex);

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, result.getStatusCode());
        assertEquals("validation_error", result.getBody().error());
        assertNotNull(result.getBody().errores());
        assertEquals(1, result.getBody().errores().size());
        assertEquals("email", result.getBody().errores().getFirst().campo());
        assertEquals("Email inválido", result.getBody().errores().getFirst().mensaje());
        assertEquals("valor-invalido", result.getBody().errores().getFirst().valorRechazado());
    }

    @Test
    void bind_retorna422_conErrores() {
        var fieldError = new FieldError("object", "nombre", "Campo requerido");
        var ex = mock(BindException.class);
        var bindingResult = mock(org.springframework.validation.BindingResult.class);

        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));

        ResponseEntity<ErrorResponse> result = handler.handleBind(ex);

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, result.getStatusCode());
        assertEquals("validation_error", result.getBody().error());
        assertEquals(1, result.getBody().errores().size());
    }

    @Test
    void constraint_retorna422_conErrores() {
        var violation = mock(ConstraintViolation.class);
        var path = mock(Path.class);

        when(violation.getPropertyPath()).thenReturn(path);
        when(path.toString()).thenReturn("cantidad");
        when(violation.getMessage()).thenReturn("Debe ser positivo");
        when(violation.getInvalidValue()).thenReturn(-1);

        var ex = new ConstraintViolationException("constraint", Set.of((ConstraintViolation<?>) violation));

        ResponseEntity<ErrorResponse> result = handler.handleConstraint(ex);

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, result.getStatusCode());
        assertEquals("validation_error", result.getBody().error());
        assertEquals(1, result.getBody().errores().size());
        assertEquals("cantidad", result.getBody().errores().getFirst().campo());
        assertEquals("Debe ser positivo", result.getBody().errores().getFirst().mensaje());
        assertEquals(-1, result.getBody().errores().getFirst().valorRechazado());
    }

    @Test
    void general_retorna500() {
        var ex = new RuntimeException("Error inesperado");

        ResponseEntity<ErrorResponse> result = handler.handleGeneral(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
        assertEquals("internal_error", result.getBody().error());
        assertEquals("Error interno del servidor", result.getBody().message());
    }

    @Test
    void allResponses_incluyenTimestamp() {
        var ex = new DuplicateResourceException("test");

        ResponseEntity<ErrorResponse> result = handler.handleDuplicate(ex);

        assertNotNull(result.getBody().timestamp());
    }
}
