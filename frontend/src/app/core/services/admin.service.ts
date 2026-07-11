import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { DashboardResponse, UsuarioAdminResponse, ReservaAdminResponse, HistorialResponse } from '../models/admin.models';

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

  listarReservas(params?: any): Observable<any> {
    return this.http.get<any>(`${this.api}/reservas`, { params });
  }

  obtenerReserva(id: number): Observable<ReservaAdminResponse> {
    return this.http.get<ReservaAdminResponse>(`${this.api}/reservas/${id}`);
  }

  obtenerHistorial(reservaId: number): Observable<HistorialResponse[]> {
    return this.http.get<HistorialResponse[]>(`${this.api}/reservas/${reservaId}/historial`);
  }

  listarCola(page = 0, size = 20): Observable<any> {
    return this.http.get<any>(`${this.api}/cola?page=${page}&size=${size}`);
  }
}
