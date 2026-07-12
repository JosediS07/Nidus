import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { MatDialogModule, MatDialog } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatOptionModule } from '@angular/material/core';
import { FormsModule } from '@angular/forms';
import { AdminService } from '../../../core/services/admin.service';
import { RecursoResponse } from '../../../core/models/recurso.models';
import { RecursoDialogComponent } from './recurso-dialog.component';
import { ConfirmDialogComponent } from '../../../shared/confirm-dialog/confirm-dialog.component';

@Component({
  selector: 'app-admin-recursos',
  standalone: true,
  imports: [CommonModule, MatButtonModule, MatDialogModule, MatFormFieldModule, MatInputModule, MatSelectModule, MatOptionModule, FormsModule],
  templateUrl: './admin-recursos.component.html',
  styleUrl: './admin-recursos.component.css'
})
export class AdminRecursosComponent implements OnInit {
  recursos: RecursoResponse[] = [];
  total = 0;
  pagina = 0;

  constructor(private adminService: AdminService, private dialog: MatDialog) {}

  ngOnInit(): void {
    this.cargar();
  }

  cargar(page = 0): void {
    this.pagina = page;
    this.adminService.listarRecursos(page).subscribe((res: any) => {
      this.recursos = res.content;
      this.total = res.totalElements;
    });
  }

  cambiarPagina(e: any): void {
    this.cargar(e.pageIndex);
  }

  abrirCrear(): void {
    const ref = this.dialog.open(RecursoDialogComponent, { width: '500px' });
    ref.afterClosed().subscribe((result) => {
      if (result) this.cargar(0);
    });
  }

  abrirEditar(recurso: RecursoResponse): void {
    const ref = this.dialog.open(RecursoDialogComponent, { width: '500px', data: recurso });
    ref.afterClosed().subscribe((result) => {
      if (result) this.cargar(this.pagina);
    });
  }

  confirmarEliminar(recurso: RecursoResponse): void {
    const ref = this.dialog.open(ConfirmDialogComponent, {
      data: { titulo: 'Eliminar recurso', mensaje: `¿Eliminar "${recurso.nombre}"?` }
    });
    ref.afterClosed().subscribe((confirmado) => {
      if (confirmado) {
        this.adminService.eliminarRecurso(recurso.id).subscribe(() => this.cargar(this.pagina));
      }
    });
  }
}
