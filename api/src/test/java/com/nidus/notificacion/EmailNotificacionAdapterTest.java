package com.nidus.notificacion;

import com.nidus.notificacion.infrastructure.service.EmailNotificacionAdapter;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailNotificacionAdapterTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private SpringTemplateEngine templateEngine;

    @Mock
    private MimeMessage mimeMessage;

    private EmailNotificacionAdapter adapter;

    private final String email = "usuario@mail.com";
    private final String nombre = "Juan";
    private final Long idReserva = 1L;
    private final String recursoNombre = "Sala A";
    private final LocalDateTime inicio = LocalDateTime.of(2026, 7, 10, 10, 0);
    private final LocalDateTime fin = LocalDateTime.of(2026, 7, 10, 12, 0);

    @BeforeEach
    void setUp() {
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        when(templateEngine.process(anyString(), any(Context.class))).thenReturn("<html>email</html>");
        adapter = new EmailNotificacionAdapter(mailSender, templateEngine, "noreply@nidus.com");
    }

    @Test
    void enviarConfirmacion_enviaEmail() {
        adapter.enviarConfirmacion(email, nombre, idReserva, recursoNombre, inicio, fin);

        verify(mailSender).send(mimeMessage);
    }

    @Test
    void enviarModificacion_enviaEmail() {
        adapter.enviarModificacion(email, nombre, idReserva, recursoNombre, inicio, fin);

        verify(mailSender).send(mimeMessage);
    }

    @Test
    void enviarCancelacion_enviaEmail() {
        adapter.enviarCancelacion(email, nombre, idReserva, recursoNombre, inicio, fin);

        verify(mailSender).send(mimeMessage);
    }

    @Test
    void enviarConfirmacion_usaTemplateCorrecto() {
        var captor = ArgumentCaptor.forClass(String.class);

        adapter.enviarConfirmacion(email, nombre, idReserva, recursoNombre, inicio, fin);

        verify(mailSender).send(mimeMessage);
        verify(templateEngine).process(captor.capture(), any(Context.class));
        assertEquals("email/confirmacion", captor.getValue());
    }

    @Test
    void enviarModificacion_usaTemplateCorrecto() {
        var captor = ArgumentCaptor.forClass(String.class);

        adapter.enviarModificacion(email, nombre, idReserva, recursoNombre, inicio, fin);

        verify(templateEngine).process(captor.capture(), any(Context.class));
        assertEquals("email/modificacion", captor.getValue());
    }

    @Test
    void enviarCancelacion_usaTemplateCorrecto() {
        var captor = ArgumentCaptor.forClass(String.class);

        adapter.enviarCancelacion(email, nombre, idReserva, recursoNombre, inicio, fin);

        verify(templateEngine).process(captor.capture(), any(Context.class));
        assertEquals("email/cancelacion", captor.getValue());
    }

    @Test
    void errorAlEnviar_noPropagaExcepcion() {
        doThrow(new RuntimeException("SMTP error")).when(mailSender).send(any(MimeMessage.class));

        adapter.enviarConfirmacion(email, nombre, idReserva, recursoNombre, inicio, fin);

        verify(mailSender).send(mimeMessage);
    }

}
