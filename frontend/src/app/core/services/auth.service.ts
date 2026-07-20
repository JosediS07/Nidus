import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { environment } from '../../../environments/environment';
import { LoginRequest, RegisterRequest, AuthResponse, UserResponse, ActualizarPerfilRequest } from '../models/auth.models';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private api = `${environment.apiUrl}/auth`;
  private usuarioSubject = new BehaviorSubject<AuthResponse | null>(this.cargarUsuario());

  usuario$ = this.usuarioSubject.asObservable();

  constructor(private http: HttpClient) {}

  private cargarUsuario(): AuthResponse | null {
    const raw = localStorage.getItem('user');
    return raw ? JSON.parse(raw) : null;
  }

  login(peticion: LoginRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.api}/login`, peticion).pipe(
      map((respuesta) => {
        localStorage.setItem('token', respuesta.token);
        localStorage.setItem('user', JSON.stringify(respuesta));
        this.usuarioSubject.next(respuesta);
        return respuesta;
      })
    );
  }

  register(peticion: RegisterRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.api}/register`, peticion).pipe(
      map((res) => {
        localStorage.setItem('token', res.token);
        localStorage.setItem('user', JSON.stringify(res));
        this.usuarioSubject.next(res);
        return res;
      })
    );
  }

  me(): Observable<UserResponse> {
    return this.http.get<UserResponse>(`${this.api}/me`);
  }

  actualizarPerfil(peticion: ActualizarPerfilRequest): Observable<UserResponse> {
    return this.http.put<UserResponse>(`${this.api}/me`, peticion);
  }

  getToken(): string | null {
    return localStorage.getItem('token');
  }

  getUser(): AuthResponse | null {
    return this.usuarioSubject.getValue();
  }

  isAdmin(): boolean {
    return this.getUser()?.rol === 'ADMIN';
  }

  logout(): void {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    this.usuarioSubject.next(null);
  }

  limpiarSesion(): void {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    this.usuarioSubject.next(null);
  }
}
