import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import {
  DashboardResponse, UsuarioAdminResponse, ReservaAdminResponse,
  HistorialResponse, SolicitudColaResponse, CrearUsuarioAdminRequest,
  ActualizarUsuarioAdminRequest, CambiarRolRequest
} from '../models/admin.models';
import { RecursoResponse, CrearRecursoRequest, ActualizarRecursoRequest } from '../models/recurso.models';

@Injectable({ providedIn: 'root' })
export class AdminService {
  private api = `${environment.apiUrl}/admin`;

  constructor(private http: HttpClient) {}

  dashboard(): Observable<DashboardResponse> {
    return this.http.get<DashboardResponse>(`${this.api}/dashboard`);
  }

  listarUsuarios(page = 0, size = 20): Observable<any> {
    return this.http.get<any>(`${this.api}/usuarios?page=${page}&size=${size}`);
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

  listarRecursos(page = 0, size = 20): Observable<any> {
    return this.http.get<any>(`${this.api}/recursos?page=${page}&size=${size}`);
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

  listarReservas(params?: any): Observable<any> {
    return this.http.get<any>(`${this.api}/reservas`, { params });
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

  listarCola(page = 0, size = 20): Observable<any> {
    return this.http.get<any>(`${this.api}/cola?page=${page}&size=${size}`);
  }

  eliminarSolicitudCola(id: number): Observable<void> {
    return this.http.delete<void>(`${this.api}/cola/${id}`);
  }
}
