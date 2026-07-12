export interface DashboardResponse {
  totalUsuarios: number;
  totalRecursos: number;
  totalReservas: number;
  reservasPorEstado: Record<string, number>;
  reservasHoy: number;
  recursoMasReservado: string;
}

export interface UsuarioAdminResponse {
  id: number;
  nombre: string;
  email: string;
  rol: string;
  activo: boolean;
  creado: string;
}

export interface CrearUsuarioAdminRequest {
  nombre: string;
  email: string;
  password: string;
  rol: string;
}

export interface ActualizarUsuarioAdminRequest {
  nombre?: string;
  email?: string;
  activo?: boolean;
}

export interface ReservaAdminResponse {
  id: number;
  recursoId: number;
  usuarioId: number;
  fechaInicio: string;
  fechaFin: string;
  estado: string;
}

export interface HistorialResponse {
  id: number;
  reservaId: number;
  usuarioId: number;
  tipoEvento: string;
  descripcion: string;
  creado: string;
}

export interface SolicitudColaResponse {
  id: number;
  recursoId: number;
  usuarioId: number;
  estado: string;
  creado: string;
}

export interface CambiarRolRequest {
  rol: string;
}
