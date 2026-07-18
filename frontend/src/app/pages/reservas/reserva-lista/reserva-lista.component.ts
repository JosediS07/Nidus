import { Component, OnInit, ChangeDetectionStrategy, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatSnackBarModule, MatSnackBar } from '@angular/material/snack-bar';
import { ReservaService } from '../../../core/services/reserva.service';
import { ReservaResponse } from '../../../core/models/reserva.models';
import { ConfirmDialogComponent } from '../../../shared/confirm-dialog/confirm-dialog.component';

@Component({
  selector: 'app-reserva-lista',
  standalone: true,
  imports: [CommonModule, RouterModule, MatButtonModule, MatDialogModule, MatSnackBarModule],
  templateUrl: './reserva-lista.component.html',
  styleUrl: './reserva-lista.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ReservaListaComponent implements OnInit {
  reservas: ReservaResponse[] = [];
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

  constructor(
    private reservaService: ReservaService,
    private dialog: MatDialog,
    private snackBar: MatSnackBar,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.cargar();
  }

  cargar(page = 0): void {
    this.pagina = page;
    this.cargando = true;
    this.error = '';
    this.reservaService.listarMisReservas(page).subscribe({
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

  cambiarPagina(evento: { pageIndex: number }): void {
    this.cargar(evento.pageIndex);
  }

  cancelar(r: ReservaResponse): void {
    const ref = this.dialog.open(ConfirmDialogComponent, {
      data: {
        titulo: 'Cancelar reserva',
        mensaje: `¿Cancelar la reserva #${r.id}?`,
        confirmarTexto: 'Cancelar',
      }
    });

    ref.afterClosed().subscribe({
      next: (confirmado) => {
        if (confirmado) {
          this.reservaService.cancelar(r.id).subscribe({
            next: () => {
              this.snackBar.open('Reserva cancelada', 'Cerrar', { duration: 3000 });
              this.cargar();
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
