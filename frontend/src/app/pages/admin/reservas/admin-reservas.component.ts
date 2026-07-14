import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { MatDialogModule, MatDialog } from '@angular/material/dialog';
import { MatSnackBarModule, MatSnackBar } from '@angular/material/snack-bar';
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
  imports: [CommonModule, MatButtonModule, MatDialogModule, MatSnackBarModule, MatFormFieldModule, MatInputModule, MatSelectModule, FormsModule],
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

  private readonly TAMANIO_PAGINA = 20;

  get hayPaginaSiguiente(): boolean {
    return (this.pagina + 1) * this.TAMANIO_PAGINA < this.total;
  }

  get hayPaginacion(): boolean {
    return this.total > this.TAMANIO_PAGINA;
  }

  constructor(private adminService: AdminService, private dialog: MatDialog, private snackBar: MatSnackBar) {}

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

  cambiarPagina(evento: any): void {
    this.cargar(evento.pageIndex);
  }

  buscar(): void {
    this.cargar(0);
  }

  confirmarCancelar(reserva: ReservaAdminResponse): void {
    const ref = this.dialog.open(ConfirmDialogComponent, {
      data: {
        titulo: 'Cancelar reserva',
        mensaje: `¿Cancelar la reserva #${reserva.id}?`
      }
    });
    ref.afterClosed().subscribe({
      next: (confirmado) => {
        if (confirmado) {
          this.adminService.cancelarReserva(reserva.id).subscribe({
            next: () => {
              this.snackBar.open('Reserva cancelada', 'Cerrar', { duration: 3000 });
              this.cargar(this.pagina);
            },
            error: (error) => {
              this.snackBar.open(error.error?.message || 'Error al cancelar', 'Cerrar', { duration: 4000 });
            }
          });
        }
      }
    });
  }
}
