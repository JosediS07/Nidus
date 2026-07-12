import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { MatDialogModule, MatDialog } from '@angular/material/dialog';
import { AdminService } from '../../../core/services/admin.service';
import { SolicitudColaResponse } from '../../../core/models/admin.models';
import { ConfirmDialogComponent } from '../../../shared/confirm-dialog/confirm-dialog.component';

@Component({
  selector: 'app-admin-cola',
  standalone: true,
  imports: [CommonModule, MatButtonModule, MatDialogModule],
  templateUrl: './admin-cola.component.html',
  styleUrl: './admin-cola.component.css'
})
export class AdminColaComponent implements OnInit {
  solicitudes: SolicitudColaResponse[] = [];
  total = 0;
  pagina = 0;

  constructor(private adminService: AdminService, private dialog: MatDialog) {}

  ngOnInit(): void {
    this.cargar();
  }

  cargar(page = 0): void {
    this.pagina = page;
    this.adminService.listarCola(page).subscribe((res: any) => {
      this.solicitudes = res.content;
      this.total = res.totalElements;
    });
  }

  cambiarPagina(e: any): void {
    this.cargar(e.pageIndex);
  }

  confirmarEliminar(s: SolicitudColaResponse): void {
    const ref = this.dialog.open(ConfirmDialogComponent, {
      data: {
        titulo: 'Eliminar solicitud',
        mensaje: `¿Eliminar la solicitud #${s.id} de la cola?`
      }
    });
    ref.afterClosed().subscribe((confirmado) => {
      if (confirmado) {
        this.adminService.eliminarSolicitudCola(s.id).subscribe(() => this.cargar(this.pagina));
      }
    });
  }
}
