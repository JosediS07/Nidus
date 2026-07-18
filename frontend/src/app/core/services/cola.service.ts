import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Page } from '../models/pagination.models';
import { SolicitudColaResponse } from '../models/admin.models';

@Injectable({ providedIn: 'root' })
export class ColaService {
  private api = `${environment.apiUrl}/cola`;

  constructor(private http: HttpClient) {}

  apuntarse(recursoId: number): Observable<SolicitudColaResponse> {
    return this.http.post<SolicitudColaResponse>(this.api, { recursoId });
  }

  listar(recursoId: number): Observable<Page<SolicitudColaResponse>> {
    return this.http.get<Page<SolicitudColaResponse>>(`${this.api}?recursoId=${recursoId}`);
  }

  salir(id: number): Observable<void> {
    return this.http.delete<void>(`${this.api}/${id}`);
  }
}
