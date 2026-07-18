import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Page } from '../models/pagination.models';
import { RecursoResponse, CrearRecursoRequest, ActualizarRecursoRequest } from '../models/recurso.models';

@Injectable({ providedIn: 'root' })
export class RecursoService {
  private api = `${environment.apiUrl}/recursos`;

  constructor(private http: HttpClient) {}

  listar(page = 0, size = 20): Observable<Page<RecursoResponse>> {
    return this.http.get<Page<RecursoResponse>>(`${this.api}?page=${page}&size=${size}`);
  }

  obtener(id: number): Observable<RecursoResponse> {
    return this.http.get<RecursoResponse>(`${this.api}/${id}`);
  }

  crear(peticion: CrearRecursoRequest): Observable<RecursoResponse> {
    return this.http.post<RecursoResponse>(this.api, peticion);
  }

  actualizar(id: number, peticion: ActualizarRecursoRequest): Observable<RecursoResponse> {
    return this.http.put<RecursoResponse>(`${this.api}/${id}`, peticion);
  }

  desactivar(id: number): Observable<void> {
    return this.http.delete<void>(`${this.api}/${id}`);
  }
}
