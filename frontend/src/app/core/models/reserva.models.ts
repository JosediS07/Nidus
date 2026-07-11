export interface CrearReservaRequest {
  recursoId: number;
  fechaInicio: string;
  fechaFin: string;
}

export interface ModificarReservaRequest {
  fechaInicio: string;
  fechaFin: string;
}

export interface ReservaResponse {
  id: number;
  recursoId: number;
  usuarioId: number;
  fechaInicio: string;
  fechaFin: string;
  estado: string;
}
