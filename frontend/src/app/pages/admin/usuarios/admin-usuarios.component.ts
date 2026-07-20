import { Component, OnInit, ChangeDetectionStrategy, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { MatDialogModule, MatDialog } from '@angular/material/dialog';
import { MatSnackBarModule, MatSnackBar } from '@angular/material/snack-bar';
import { PaginationComponent } from '../../../shared/pagination/pagination.component';
import { AdminService } from '../../../core/services/admin.service';
import { UsuarioAdminResponse } from '../../../core/models/admin.models';
import { UsuarioDialogComponent } from './usuario-dialog.component';
import { ConfirmDialogComponent } from '../../../shared/confirm-dialog/confirm-dialog.component';

@Component({
  selector: 'app-admin-usuarios',
  standalone: true,
  imports: [CommonModule, MatButtonModule, MatDialogModule, MatSnackBarModule, PaginationComponent],
  templateUrl: './admin-usuarios.component.html',
  styleUrl: './admin-usuarios.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class AdminUsuariosComponent implements OnInit {
  usuarios: UsuarioAdminResponse[] = [];
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
    this.adminService.listarUsuarios(page).subscribe({
      next: (res) => {
        this.usuarios = res.content;
        this.total = res.totalElements;
        this.cargando = false;
        this.cdr.markForCheck();
      },
      error: (err) => {
        this.error = err.error?.message || 'Error al cargar usuarios';
        this.cargando = false;
      }
    });
  }

  abrirCrear(): void {
    const ref = this.dialog.open(UsuarioDialogComponent, { width: '500px' });
    ref.afterClosed().subscribe({
      next: (result) => {
        if (result) {
          this.snackBar.open('Usuario creado', 'Cerrar', { duration: 3000 });
          this.cargar(0);
        }
        this.cdr.markForCheck();
      }
    });
  }

  abrirEditar(usuario: UsuarioAdminResponse): void {
    const ref = this.dialog.open(UsuarioDialogComponent, { width: '500px', data: usuario });
    ref.afterClosed().subscribe({
      next: (result) => {
        if (result) {
          this.snackBar.open('Usuario actualizado', 'Cerrar', { duration: 3000 });
          this.cargar(this.pagina);
        }
        this.cdr.markForCheck();
      }
    });
  }

  confirmarEliminar(usuario: UsuarioAdminResponse): void {
    const ref = this.dialog.open(ConfirmDialogComponent, {
      data: { titulo: 'Eliminar usuario', mensaje: `¿Eliminar a "${usuario.nombre}" (${usuario.email})?` }
    });
    ref.afterClosed().subscribe({
      next: (confirmado) => {
        if (confirmado) {
          this.adminService.eliminarUsuario(usuario.id).subscribe({
            next: () => {
              this.snackBar.open('Usuario eliminado', 'Cerrar', { duration: 3000 });
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
