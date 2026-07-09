package com.nidus.notificacion.application.port;

import java.time.LocalDateTime;

public interface NotificacionPort {

    void enviarConfirmacion(String emailDestino, String nombreUsuario, Long idReserva,
                            String recursoNombre, LocalDateTime fechaInicio, LocalDateTime fechaFin);

    void enviarModificacion(String emailDestino, String nombreUsuario, Long idReserva,
                            String recursoNombre, LocalDateTime fechaInicio, LocalDateTime fechaFin);

    void enviarCancelacion(String emailDestino, String nombreUsuario, Long idReserva,
                           String recursoNombre, LocalDateTime fechaInicio, LocalDateTime fechaFin);

    void enviarNotificacionCola(String emailDestino, String nombreUsuario,
                                String recursoNombre, Long idSolicitud);
}
