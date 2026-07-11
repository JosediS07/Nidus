import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class ColaService {
  private api = `${environment.apiUrl}/cola`;

  constructor(private http: HttpClient) {}

  apuntarse(recursoId: number): Observable<any> {
    return this.http.post(this.api, { recursoId });
  }

  listar(recursoId: number): Observable<any> {
    return this.http.get(`${this.api}?recursoId=${recursoId}`);
  }

  salir(id: number): Observable<void> {
    return this.http.delete<void>(`${this.api}/${id}`);
  }
}
