import { Component, OnInit, ChangeDetectionStrategy, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { MatDialogModule, MatDialog } from '@angular/material/dialog';
import { MatSnackBarModule, MatSnackBar } from '@angular/material/snack-bar';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { FormsModule } from '@angular/forms';
import { PaginationComponent } from '../../../shared/pagination/pagination.component';
import { AdminService } from '../../../core/services/admin.service';
import { ReservaAdminResponse, ListarReservasParams } from '../../../core/models/admin.models';
import { ConfirmDialogComponent } from '../../../shared/confirm-dialog/confirm-dialog.component';

@Component({
  selector: 'app-admin-reservas',
  standalone: true,
  imports: [CommonModule, MatButtonModule, MatDialogModule, MatSnackBarModule, MatFormFieldModule, MatInputModule, MatSelectModule, FormsModule, PaginationComponent],
  templateUrl: './admin-reservas.component.html',
  styleUrl: './admin-reservas.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class AdminReservasComponent implements OnInit {
  reservas: ReservaAdminResponse[] = [];
  total = 0;
  pagina = 0;
  cargando = false;
  error = '';

  filtroEstado = '';
  filtroRecursoNombre = '';
  filtroUsuarioNombre = '';

  constructor(private adminService: AdminService, private dialog: MatDialog, private snackBar: MatSnackBar, private cdr: ChangeDetectorRef) {}

  ngOnInit(): void {
    this.cargar();
  }

  cargar(page = 0): void {
    this.pagina = page;
    this.cargando = true;
    this.error = '';
    const params: ListarReservasParams = { page, size: 20 };
    if (this.filtroEstado) params.estado = this.filtroEstado;
    if (this.filtroRecursoNombre) params.recursoNombre = this.filtroRecursoNombre;
    if (this.filtroUsuarioNombre) params.usuarioNombre = this.filtroUsuarioNombre;

    this.adminService.listarReservas(params).subscribe({
      next: (res) => {
        this.reservas = res.content;
        this.total = res.totalElements;
        this.cargando = false;
        this.cdr.markForCheck();
      },
      error: (err) => {
        this.error = err.error?.message || 'Error al cargar reservas';
        this.cargando = false;
      }
    });
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
              this.cdr.markForCheck();
            },
            error: (error) => {
              this.snackBar.open(error.error?.message || 'Error al cancelar', 'Cerrar', { duration: 4000 });
            }
          });
        }
        this.cdr.markForCheck();
      }
    });
  }
}
