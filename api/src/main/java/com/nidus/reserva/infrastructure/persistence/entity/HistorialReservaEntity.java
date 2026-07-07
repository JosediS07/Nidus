package com.nidus.reserva.infrastructure.persistence.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "historial_reservas")
public class HistorialReservaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long reservaId;

    @Column(nullable = false)
    private Long usuarioId;

    @Column(nullable = false, length = 20)
    private String tipoEvento;

    @Column(nullable = false, length = 500)
    private String descripcion;

    @Column(nullable = false, updatable = false)
    private LocalDateTime creado;

    public HistorialReservaEntity() {}

    public HistorialReservaEntity(Long reservaId, Long usuarioId, String tipoEvento, String descripcion) {
        this.reservaId = reservaId;
        this.usuarioId = usuarioId;
        this.tipoEvento = tipoEvento;
        this.descripcion = descripcion;
    }

    @PrePersist
    protected void onCreate() {
        if (creado == null) {
            this.creado = LocalDateTime.now();
        }
    }

    public Long getId() { return id; }
    public Long getReservaId() { return reservaId; }
    public Long getUsuarioId() { return usuarioId; }
    public String getTipoEvento() { return tipoEvento; }
    public String getDescripcion() { return descripcion; }
    public LocalDateTime getCreado() { return creado; }
}
