package com.nidus.shared.exception.dto;

public record ValidationError(
    String campo,
    String mensaje,
    Object valorRechazado
) {}