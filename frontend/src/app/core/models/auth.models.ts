export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  nombre: string;
  email: string;
  password: string;
}

export interface AuthResponse {
  id: number;
  token: string;
  nombre: string;
  email: string;
  rol: string;
}

export interface UserResponse {
  id: number;
  nombre: string;
  email: string;
  rol: string;
  activo: boolean;
}

export interface ActualizarPerfilRequest {
  nombre?: string;
  email?: string;
  password?: string;
  currentPassword?: string;
}
