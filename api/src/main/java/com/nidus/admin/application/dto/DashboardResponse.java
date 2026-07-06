package com.nidus.admin.application.dto;

import java.util.Map;

public record DashboardResponse(
    long totalUsuarios,
    long totalRecursos,
    long totalReservas,
    Map<String, Long> reservasPorEstado,
    long reservasHoy,
    String recursoMasReservado
) {}
