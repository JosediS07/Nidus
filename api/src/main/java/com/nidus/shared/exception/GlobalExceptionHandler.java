package com.nidus.shared.exception;

import com.nidus.shared.exception.dto.ErrorResponse;
import com.nidus.shared.exception.dto.ValidationError;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ErrorResponse> handleDuplicate(DuplicateResourceException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(cuerpo("duplicate_resource", ex.getMessage(), 409));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentials(BadCredentialsException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(cuerpo("unauthorized", ex.getMessage(), 401));
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(cuerpo("not_found", ex.getMessage(), 404));
    }

    @ExceptionHandler(InvalidStateException.class)
    public ResponseEntity<ErrorResponse> handleInvalidState(InvalidStateException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(cuerpo("invalid_state", ex.getMessage(), 409));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(cuerpo("forbidden", "Acceso denegado", 403));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        return erroresValidacion(ex.getBindingResult().getFieldErrors().stream()
                .map(e -> new ValidationError(e.getField(), e.getDefaultMessage(), e.getRejectedValue()))
                .toList(), 422);
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<ErrorResponse> handleBind(BindException ex) {
        return erroresValidacion(ex.getBindingResult().getFieldErrors().stream()
                .map(e -> new ValidationError(e.getField(), e.getDefaultMessage(), e.getRejectedValue()))
                .toList(), 422);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraint(ConstraintViolationException ex) {
        var errores = ex.getConstraintViolations().stream()
                .map(v -> new ValidationError(
                        v.getPropertyPath().toString(),
                        v.getMessage(),
                        v.getInvalidValue()
                ))
                .toList();
        return erroresValidacion(errores, 422);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneral(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(cuerpo(
                "internal_error",
                "Error interno del servidor",
                500
        ));
    }

    private ErrorResponse cuerpo(String error, String message, int status) {
        return new ErrorResponse(error, message, status, LocalDateTime.now());
    }

    private ResponseEntity<ErrorResponse> erroresValidacion(List<ValidationError> errores, int status) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(new ErrorResponse(
                "validation_error",
                "Datos inválidos",
                status,
                LocalDateTime.now(),
                errores
        ));
    }
}
