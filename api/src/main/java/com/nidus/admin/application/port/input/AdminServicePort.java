package com.nidus.admin.application.port.input;

import com.nidus.admin.application.dto.ActualizarUsuarioAdminRequest;
import com.nidus.admin.application.dto.CrearUsuarioAdminRequest;
import com.nidus.admin.application.dto.DashboardResponse;
import com.nidus.admin.application.dto.HistorialResponse;
import com.nidus.admin.application.dto.ReservaAdminResponse;
import com.nidus.admin.application.dto.UsuarioAdminResponse;
import com.nidus.cola.application.dto.SolicitudColaResponse;
import com.nidus.recurso.application.dto.ActualizarRecursoRequest;
import com.nidus.recurso.application.dto.CrearRecursoRequest;
import com.nidus.recurso.application.dto.RecursoResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface AdminServicePort {

    DashboardResponse dashboard();

    Page<UsuarioAdminResponse> listarUsuarios(Pageable pageable);

    UsuarioAdminResponse obtenerUsuario(Long id);

    UsuarioAdminResponse crearUsuario(CrearUsuarioAdminRequest request);

    UsuarioAdminResponse actualizarUsuario(Long id, ActualizarUsuarioAdminRequest request);

    void eliminarUsuario(Long id);

    Page<RecursoResponse> listarRecursos(Pageable pageable);

    RecursoResponse obtenerRecurso(Long id);

    RecursoResponse crearRecurso(CrearRecursoRequest request);

    RecursoResponse actualizarRecurso(Long id, ActualizarRecursoRequest request);

    void eliminarRecurso(Long id);

    Page<ReservaAdminResponse> listarReservas(String estado, String recursoNombre, String usuarioNombre,
                                              LocalDateTime fechaInicio, LocalDateTime fechaFin, Pageable pageable);

    ReservaAdminResponse obtenerReserva(Long id);

    void cancelarReserva(Long id, Long adminUserId);

    Page<SolicitudColaResponse> listarSolicitudesCola(Pageable pageable);

    void eliminarSolicitudCola(Long id);

    List<HistorialResponse> obtenerHistorial(Long reservaId);
}
