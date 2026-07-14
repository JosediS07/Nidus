import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { CrearReservaRequest, ModificarReservaRequest, ReservaResponse } from '../models/reserva.models';

@Injectable({ providedIn: 'root' })
export class ReservaService {
  private api = `${environment.apiUrl}/reservas`;

  constructor(private http: HttpClient) {}

  crear(peticion: CrearReservaRequest): Observable<ReservaResponse> {
    return this.http.post<ReservaResponse>(this.api, peticion);
  }

  listarMisReservas(page = 0, size = 20): Observable<any> {
    return this.http.get<any>(`${this.api}?page=${page}&size=${size}`);
  }

  listarTodas(page = 0, size = 20): Observable<any> {
    return this.http.get<any>(`${this.api}/todas?page=${page}&size=${size}`);
  }

  obtener(id: number): Observable<ReservaResponse> {
    return this.http.get<ReservaResponse>(`${this.api}/${id}`);
  }

  modificar(id: number, peticion: ModificarReservaRequest): Observable<ReservaResponse> {
    return this.http.put<ReservaResponse>(`${this.api}/${id}`, peticion);
  }

  cancelar(id: number): Observable<void> {
    return this.http.delete<void>(`${this.api}/${id}`);
  }
}
