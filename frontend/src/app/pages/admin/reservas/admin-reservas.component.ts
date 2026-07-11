import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { FormsModule } from '@angular/forms';
import { AdminService } from '../../../core/services/admin.service';
import { ReservaAdminResponse } from '../../../core/models/admin.models';

@Component({
  selector: 'app-admin-reservas',
  standalone: true,
  imports: [CommonModule, MatButtonModule, MatFormFieldModule, MatInputModule, MatSelectModule, FormsModule],
  templateUrl: './admin-reservas.component.html',
  styleUrl: './admin-reservas.component.css'
})
export class AdminReservasComponent implements OnInit {
  reservas: ReservaAdminResponse[] = [];
  total = 0;
  pagina = 0;

  filtroEstado = '';
  filtroRecursoId: number | null = null;
  filtroUsuarioId: number | null = null;

  constructor(private adminService: AdminService) {}

  ngOnInit(): void {
    this.cargar();
  }

  cargar(page = 0): void {
    this.pagina = page;
    const params: any = { page, size: 20 };
    if (this.filtroEstado) params.estado = this.filtroEstado;
    if (this.filtroRecursoId) params.recursoId = this.filtroRecursoId;
    if (this.filtroUsuarioId) params.usuarioId = this.filtroUsuarioId;

    this.adminService.listarReservas(params).subscribe((res) => {
      this.reservas = res.content;
      this.total = res.totalElements;
    });
  }

  cambiarPagina(e: any): void {
    this.cargar(e.pageIndex);
  }

  buscar(): void {
    this.cargar(0);
  }
}
