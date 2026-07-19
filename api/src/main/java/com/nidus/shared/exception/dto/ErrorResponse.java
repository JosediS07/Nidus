package com.nidus.shared.exception.dto;

import java.time.LocalDateTime;
import java.util.List;

public record ErrorResponse(
    String error,
    String message,
    int status,
    LocalDateTime timestamp,
    List<ValidationError> errores
) {
    public ErrorResponse(String error, String message, int status, LocalDateTime timestamp) {
        this(error, message, status, timestamp, null);
    }
}
