import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { MatDialogModule, MatDialog } from '@angular/material/dialog';
import { MatSnackBarModule, MatSnackBar } from '@angular/material/snack-bar';
import { AdminService } from '../../../core/services/admin.service';
import { SolicitudColaResponse } from '../../../core/models/admin.models';
import { ConfirmDialogComponent } from '../../../shared/confirm-dialog/confirm-dialog.component';

@Component({
  selector: 'app-admin-cola',
  standalone: true,
  imports: [CommonModule, MatButtonModule, MatDialogModule, MatSnackBarModule],
  templateUrl: './admin-cola.component.html',
  styleUrl: './admin-cola.component.css'
})
export class AdminColaComponent implements OnInit {
  solicitudes: SolicitudColaResponse[] = [];
  total = 0;
  pagina = 0;
  cargando = false;
  error = '';

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
    this.adminService.listarCola(page).subscribe({
      next: (res: any) => {
        this.solicitudes = res.content;
        this.total = res.totalElements;
        this.cargando = false;
      },
      error: (err) => {
        this.error = err.error?.message || 'Error al cargar cola';
        this.cargando = false;
      }
    });
  }

  cambiarPagina(evento: any): void {
    this.cargar(evento.pageIndex);
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
            },
            error: (error) => {
              this.snackBar.open(error.error?.message || 'Error al eliminar', 'Cerrar', { duration: 4000 });
            }
          });
        }
      }
    });
  }
}
