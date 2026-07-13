import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { MatDialogModule, MatDialog } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { FormsModule } from '@angular/forms';
import { AdminService } from '../../../core/services/admin.service';
import { ReservaAdminResponse } from '../../../core/models/admin.models';
import { ConfirmDialogComponent } from '../../../shared/confirm-dialog/confirm-dialog.component';

@Component({
  selector: 'app-admin-reservas',
  standalone: true,
  imports: [CommonModule, MatButtonModule, MatDialogModule, MatFormFieldModule, MatInputModule, MatSelectModule, FormsModule],
  templateUrl: './admin-reservas.component.html',
  styleUrl: './admin-reservas.component.css'
})
export class AdminReservasComponent implements OnInit {
  reservas: ReservaAdminResponse[] = [];
  total = 0;
  pagina = 0;
  cargando = false;
  error = '';

  filtroEstado = '';
  filtroRecursoId: number | null = null;
  filtroUsuarioId: number | null = null;

  constructor(private adminService: AdminService, private dialog: MatDialog) {}

  ngOnInit(): void {
    this.cargar();
  }

  cargar(page = 0): void {
    this.pagina = page;
    this.cargando = true;
    this.error = '';
    const params: any = { page, size: 20 };
    if (this.filtroEstado) params.estado = this.filtroEstado;
    if (this.filtroRecursoId) params.recursoId = this.filtroRecursoId;
    if (this.filtroUsuarioId) params.usuarioId = this.filtroUsuarioId;

    this.adminService.listarReservas(params).subscribe({
      next: (res) => {
        this.reservas = res.content;
        this.total = res.totalElements;
        this.cargando = false;
      },
      error: (err) => {
        this.error = err.error?.message || 'Error al cargar reservas';
        this.cargando = false;
      }
    });
  }

  cambiarPagina(e: any): void {
    this.cargar(e.pageIndex);
  }

  buscar(): void {
    this.cargar(0);
  }

  confirmarCancelar(r: ReservaAdminResponse): void {
    const ref = this.dialog.open(ConfirmDialogComponent, {
      data: {
        titulo: 'Cancelar reserva',
        mensaje: `¿Cancelar la reserva #${r.id}?`
      }
    });
    ref.afterClosed().subscribe((confirmado) => {
      if (confirmado) {
        this.adminService.cancelarReserva(r.id).subscribe(() => this.cargar(this.pagina));
      }
    });
  }
}
