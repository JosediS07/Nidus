package com.nidus.reserva.domain;

import java.time.LocalDateTime;

public class Reserva {

    private Long id;
    private Long recursoId;
    private Long usuarioId;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private EstadoReserva estado;
    private int version;

    public Reserva() {}

    public Reserva(Long id, Long recursoId, Long usuarioId, LocalDateTime fechaInicio,
                   LocalDateTime fechaFin, EstadoReserva estado, int version) {
        this.id = id;
        this.recursoId = recursoId;
        this.usuarioId = usuarioId;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.estado = estado;
        this.version = version;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getRecursoId() { return recursoId; }
    public void setRecursoId(Long recursoId) { this.recursoId = recursoId; }

    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }

    public LocalDateTime getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDateTime fechaInicio) { this.fechaInicio = fechaInicio; }

    public LocalDateTime getFechaFin() { return fechaFin; }
    public void setFechaFin(LocalDateTime fechaFin) { this.fechaFin = fechaFin; }

    public EstadoReserva getEstado() { return estado; }
    public void setEstado(EstadoReserva estado) { this.estado = estado; }

    public int getVersion() { return version; }
    public void setVersion(int version) { this.version = version; }
}
