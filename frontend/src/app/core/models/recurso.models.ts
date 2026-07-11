export interface RecursoResponse {
  id: number;
  nombre: string;
  tipo: string;
  descripcion: string;
  capacidad: number;
  activo: boolean;
}

export interface CrearRecursoRequest {
  nombre: string;
  tipo: string;
  descripcion: string;
  capacidad: number;
}

export interface ActualizarRecursoRequest {
  nombre: string;
  tipo: string;
  descripcion: string;
  capacidad: number;
  activo: boolean;
}
