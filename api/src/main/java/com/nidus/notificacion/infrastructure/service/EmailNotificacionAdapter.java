package com.nidus.notificacion.infrastructure.service;

import com.nidus.notificacion.application.port.NotificacionPort;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Component
public class EmailNotificacionAdapter implements NotificacionPort {

    private static final Logger log = LoggerFactory.getLogger(EmailNotificacionAdapter.class);
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;
    private final String from;

    public EmailNotificacionAdapter(JavaMailSender mailSender, SpringTemplateEngine templateEngine,
                                    @Value("${app.mail.from}") String from) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
        this.from = from;
    }

    @Async
    @Override
    public void enviarConfirmacion(String emailDestino, String nombreUsuario, Long idReserva,
                                   String recursoNombre, LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        enviar(emailDestino, "Reserva confirmada", "email/confirmacion",
                variables(nombreUsuario, idReserva, recursoNombre, fechaInicio, fechaFin, "CONFIRMADA"));
    }

    @Async
    @Override
    public void enviarModificacion(String emailDestino, String nombreUsuario, Long idReserva,
                                   String recursoNombre, LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        enviar(emailDestino, "Reserva modificada", "email/modificacion",
                variables(nombreUsuario, idReserva, recursoNombre, fechaInicio, fechaFin, "MODIFICADA"));
    }

    @Async
    @Override
    public void enviarCancelacion(String emailDestino, String nombreUsuario, Long idReserva,
                                  String recursoNombre, LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        enviar(emailDestino, "Reserva cancelada", "email/cancelacion",
                variables(nombreUsuario, idReserva, recursoNombre, fechaInicio, fechaFin, "CANCELADA"));
    }

    private void enviar(String to, String asunto, String template, Map<String, Object> variables) {
        try {
            var mimeMessage = mailSender.createMimeMessage();
            var helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(asunto);

            var context = new Context();
            context.setVariables(variables);
            var html = templateEngine.process(template, context);
            helper.setText(html, true);

            mailSender.send(mimeMessage);
            log.info("Email enviado a {}: {}", to, asunto);
        } catch (Exception e) {
            log.error("Error al enviar email a {}: {}", to, e.getMessage());
        }
    }

    private Map<String, Object> variables(String nombreUsuario, Long idReserva, String recursoNombre,
                                          LocalDateTime fechaInicio, LocalDateTime fechaFin, String estado) {
        return Map.of(
                "nombreUsuario", nombreUsuario,
                "idReserva", idReserva,
                "recursoNombre", recursoNombre,
                "fechaInicio", fechaInicio.format(FMT),
                "fechaFin", fechaFin.format(FMT),
                "estado", estado
        );
    }
}
