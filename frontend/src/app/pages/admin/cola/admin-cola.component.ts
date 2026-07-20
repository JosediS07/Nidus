import { Component, OnInit, ChangeDetectionStrategy, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { MatDialogModule, MatDialog } from '@angular/material/dialog';
import { MatSnackBarModule, MatSnackBar } from '@angular/material/snack-bar';
import { PaginationComponent } from '../../../shared/pagination/pagination.component';
import { AdminService } from '../../../core/services/admin.service';
import { SolicitudColaResponse } from '../../../core/models/admin.models';
import { ConfirmDialogComponent } from '../../../shared/confirm-dialog/confirm-dialog.component';

@Component({
  selector: 'app-admin-cola',
  standalone: true,
  imports: [CommonModule, MatButtonModule, MatDialogModule, MatSnackBarModule, PaginationComponent],
  templateUrl: './admin-cola.component.html',
  styleUrl: './admin-cola.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class AdminColaComponent implements OnInit {
  solicitudes: SolicitudColaResponse[] = [];
  total = 0;
  pagina = 0;
  cargando = false;
  error = '';

  constructor(private adminService: AdminService, private dialog: MatDialog, private snackBar: MatSnackBar, private cdr: ChangeDetectorRef) {}

  ngOnInit(): void {
    this.cargar();
  }

  cargar(page = 0): void {
    this.pagina = page;
    this.cargando = true;
    this.error = '';
    this.adminService.listarCola(page).subscribe({
      next: (res) => {
        this.solicitudes = res.content;
        this.total = res.totalElements;
        this.cargando = false;
        this.cdr.markForCheck();
      },
      error: (err) => {
        this.error = err.error?.message || 'Error al cargar cola';
        this.cargando = false;
      }
    });
  }

  confirmarEliminar(solicitud: SolicitudColaResponse): void {
    const ref = this.dialog.open(ConfirmDialogComponent, {
      data: {
        titulo: 'Eliminar solicitud',
        mensaje: `¿Eliminar la solicitud #${solicitud.id} de la cola?`
      }
    });
    ref.afterClosed().subscribe({
      next: (confirmado) => {
        if (confirmado) {
          this.adminService.eliminarSolicitudCola(solicitud.id).subscribe({
            next: () => {
              this.snackBar.open('Solicitud eliminada', 'Cerrar', { duration: 3000 });
              this.cargar(this.pagina);
              this.cdr.markForCheck();
            },
            error: (error) => {
              this.snackBar.open(error.error?.message || 'Error al eliminar', 'Cerrar', { duration: 4000 });
            }
          });
        }
        this.cdr.markForCheck();
      }
    });
  }
}
