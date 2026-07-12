import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { MatDialogModule, MatDialog } from '@angular/material/dialog';
import { AdminService } from '../../../core/services/admin.service';
import { UsuarioAdminResponse } from '../../../core/models/admin.models';
import { UsuarioDialogComponent } from './usuario-dialog.component';
import { ConfirmDialogComponent } from '../../../shared/confirm-dialog/confirm-dialog.component';

@Component({
  selector: 'app-admin-usuarios',
  standalone: true,
  imports: [CommonModule, MatButtonModule, MatDialogModule],
  templateUrl: './admin-usuarios.component.html',
  styleUrl: './admin-usuarios.component.css'
})
export class AdminUsuariosComponent implements OnInit {
  usuarios: UsuarioAdminResponse[] = [];
  total = 0;
  pagina = 0;

  constructor(private adminService: AdminService, private dialog: MatDialog) {}

  ngOnInit(): void {
    this.cargar();
  }

  cargar(page = 0): void {
    this.pagina = page;
    this.adminService.listarUsuarios(page).subscribe((res: any) => {
      this.usuarios = res.content;
      this.total = res.totalElements;
    });
  }

  cambiarPagina(e: any): void {
    this.cargar(e.pageIndex);
  }

  abrirCrear(): void {
    const ref = this.dialog.open(UsuarioDialogComponent, { width: '500px' });
    ref.afterClosed().subscribe((result) => {
      if (result) this.cargar(0);
    });
  }

  abrirEditar(u: UsuarioAdminResponse): void {
    const ref = this.dialog.open(UsuarioDialogComponent, { width: '500px', data: u });
    ref.afterClosed().subscribe((result) => {
      if (result) this.cargar(this.pagina);
    });
  }

  confirmarEliminar(u: UsuarioAdminResponse): void {
    const ref = this.dialog.open(ConfirmDialogComponent, {
      data: { titulo: 'Eliminar usuario', mensaje: `¿Eliminar a "${u.nombre}" (${u.email})?` }
    });
    ref.afterClosed().subscribe((confirmado) => {
      if (confirmado) {
        this.adminService.eliminarUsuario(u.id).subscribe(() => this.cargar(this.pagina));
      }
    });
  }
}
