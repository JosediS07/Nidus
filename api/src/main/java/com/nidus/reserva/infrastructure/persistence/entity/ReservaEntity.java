package com.nidus.reserva.infrastructure.persistence.entity;

import com.nidus.reserva.domain.EstadoReserva;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "reservas", indexes = {
    @Index(name = "idx_reserva_recurso_fechas", columnList = "recursoId, fechaInicio, fechaFin"),
    @Index(name = "idx_reserva_usuario", columnList = "usuarioId")
})
public class ReservaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long recursoId;

    @Column(nullable = false)
    private Long usuarioId;

    @Column(nullable = false)
    private LocalDateTime fechaInicio;

    @Column(nullable = false)
    private LocalDateTime fechaFin;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoReserva estado;

    @Version
    private int version;

    public ReservaEntity() {}

    public ReservaEntity(Long id, Long recursoId, Long usuarioId, LocalDateTime fechaInicio,
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
