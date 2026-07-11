import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { environment } from '../../../environments/environment';
import { LoginRequest, RegisterRequest, AuthResponse, UserResponse } from '../models/auth.models';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private api = `${environment.apiUrl}/auth`;

  constructor(private http: HttpClient) {}

  login(req: LoginRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.api}/login`, req).pipe(
      map((res) => {
        localStorage.setItem('token', res.token);
        localStorage.setItem('user', JSON.stringify(res));
        return res;
      })
    );
  }

  register(req: RegisterRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.api}/register`, req).pipe(
      map((res) => {
        localStorage.setItem('token', res.token);
        localStorage.setItem('user', JSON.stringify(res));
        return res;
      })
    );
  }

  me(): Observable<UserResponse> {
    return this.http.get<UserResponse>(`${this.api}/me`);
  }

  getToken(): string | null {
    return localStorage.getItem('token');
  }

  getUser(): AuthResponse | null {
    const raw = localStorage.getItem('user');
    return raw ? JSON.parse(raw) : null;
  }

  isAdmin(): boolean {
    return this.getUser()?.rol === 'ADMIN';
  }

  logout(): void {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
  }
}
