import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Page } from '../models/pagination.models';
import {
  DashboardResponse, UsuarioAdminResponse, ReservaAdminResponse,
  HistorialResponse, SolicitudColaResponse, CrearUsuarioAdminRequest,
  ActualizarUsuarioAdminRequest, CambiarRolRequest, ListarReservasParams
} from '../models/admin.models';
import { RecursoResponse, CrearRecursoRequest, ActualizarRecursoRequest } from '../models/recurso.models';

@Injectable({ providedIn: 'root' })
export class AdminService {
  private api = `${environment.apiUrl}/admin`;

  constructor(private http: HttpClient) {}

  dashboard(): Observable<DashboardResponse> {
    return this.http.get<DashboardResponse>(`${this.api}/dashboard`);
  }

  listarUsuarios(page = 0, size = 20): Observable<Page<UsuarioAdminResponse>> {
    return this.http.get<Page<UsuarioAdminResponse>>(`${this.api}/usuarios?page=${page}&size=${size}`);
  }

  obtenerUsuario(id: number): Observable<UsuarioAdminResponse> {
    return this.http.get<UsuarioAdminResponse>(`${this.api}/usuarios/${id}`);
  }

  crearUsuario(data: CrearUsuarioAdminRequest): Observable<UsuarioAdminResponse> {
    return this.http.post<UsuarioAdminResponse>(`${this.api}/usuarios`, data);
  }

  actualizarUsuario(id: number, data: ActualizarUsuarioAdminRequest): Observable<UsuarioAdminResponse> {
    return this.http.put<UsuarioAdminResponse>(`${this.api}/usuarios/${id}`, data);
  }

  cambiarRol(id: number, data: CambiarRolRequest): Observable<void> {
    return this.http.put<void>(`${this.api}/usuarios/${id}/rol`, data);
  }

  eliminarUsuario(id: number): Observable<void> {
    return this.http.delete<void>(`${this.api}/usuarios/${id}`);
  }

  listarRecursos(page = 0, size = 20): Observable<Page<RecursoResponse>> {
    return this.http.get<Page<RecursoResponse>>(`${this.api}/recursos?page=${page}&size=${size}`);
  }

  obtenerRecurso(id: number): Observable<RecursoResponse> {
    return this.http.get<RecursoResponse>(`${this.api}/recursos/${id}`);
  }

  crearRecurso(data: CrearRecursoRequest): Observable<RecursoResponse> {
    return this.http.post<RecursoResponse>(`${this.api}/recursos`, data);
  }

  actualizarRecurso(id: number, data: ActualizarRecursoRequest): Observable<RecursoResponse> {
    return this.http.put<RecursoResponse>(`${this.api}/recursos/${id}`, data);
  }

  eliminarRecurso(id: number): Observable<void> {
    return this.http.delete<void>(`${this.api}/recursos/${id}`);
  }

  listarReservas(params: ListarReservasParams): Observable<Page<ReservaAdminResponse>> {
    let httpParams = new HttpParams()
      .set('page', params.page)
      .set('size', params.size);
    if (params.estado) httpParams = httpParams.set('estado', params.estado);
    if (params.recursoNombre) httpParams = httpParams.set('recursoNombre', params.recursoNombre);
    if (params.usuarioNombre) httpParams = httpParams.set('usuarioNombre', params.usuarioNombre);
    if (params.fechaInicio) httpParams = httpParams.set('fechaInicio', params.fechaInicio);
    if (params.fechaFin) httpParams = httpParams.set('fechaFin', params.fechaFin);
    return this.http.get<Page<ReservaAdminResponse>>(`${this.api}/reservas`, { params: httpParams });
  }

  obtenerReserva(id: number): Observable<ReservaAdminResponse> {
    return this.http.get<ReservaAdminResponse>(`${this.api}/reservas/${id}`);
  }

  cancelarReserva(id: number): Observable<void> {
    return this.http.put<void>(`${this.api}/reservas/${id}/cancelar`, {});
  }

  obtenerHistorial(reservaId: number): Observable<HistorialResponse[]> {
    return this.http.get<HistorialResponse[]>(`${this.api}/reservas/${reservaId}/historial`);
  }

  listarCola(page = 0, size = 20): Observable<Page<SolicitudColaResponse>> {
    return this.http.get<Page<SolicitudColaResponse>>(`${this.api}/cola?page=${page}&size=${size}`);
  }

  eliminarSolicitudCola(id: number): Observable<void> {
    return this.http.delete<void>(`${this.api}/cola/${id}`);
  }
}
