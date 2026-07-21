import { Component, Inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatDialogModule, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatOptionModule } from '@angular/material/core';
import { AdminService } from '../../../core/services/admin.service';
import { UsuarioAdminResponse } from '../../../core/models/admin.models';

@Component({
  selector: 'app-usuario-dialog',
  standalone: true,
  imports: [CommonModule, FormsModule, MatButtonModule, MatDialogModule, MatFormFieldModule, MatInputModule, MatSelectModule, MatOptionModule],
  template: `
    <h2 mat-dialog-title>{{ data ? 'Editar usuario' : 'Nuevo usuario' }}</h2>
    <mat-dialog-content>
      <div class="flex flex-col gap-4 pt-4">
        <mat-form-field appearance="fill">
          <mat-label>Nombre</mat-label>
          <input matInput [(ngModel)]="nombre" required />
        </mat-form-field>
        <mat-form-field appearance="fill">
          <mat-label>Email</mat-label>
          <input matInput type="email" [(ngModel)]="email" required />
        </mat-form-field>
        @if (!data) {
          <mat-form-field appearance="fill">
            <mat-label>Contraseña</mat-label>
            <input matInput type="password" [(ngModel)]="password" required minlength="6" />
            @if (passwordCorto) {
              <mat-error>Mínimo 6 caracteres</mat-error>
            }
          </mat-form-field>
        }
        <mat-form-field appearance="fill">
          <mat-label>Rol</mat-label>
          <mat-select [(ngModel)]="rol" required>
            <mat-option value="USER">USER</mat-option>
            <mat-option value="ADMIN">ADMIN</mat-option>
          </mat-select>
        </mat-form-field>
        @if (error) {
          <div class="text-sm text-red-600 bg-red-50 border border-red-200 rounded px-3 py-2">{{ error }}</div>
        }
      </div>
    </mat-dialog-content>
    <mat-dialog-actions align="end">
      <button mat-button mat-dialog-close [disabled]="cargando">Cancelar</button>
      <button mat-flat-button color="primary" (click)="guardar()" [disabled]="!puedeGuardar || cargando">
        {{ cargando ? 'Guardando...' : 'Guardar' }}
      </button>
    </mat-dialog-actions>
  `
})
export class UsuarioDialogComponent {
  nombre = '';
  email = '';
  password = '';
  rol = 'USER';
  cargando = false;
  error = '';

  constructor(
    private adminService: AdminService,
    private dialogRef: MatDialogRef<UsuarioDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: UsuarioAdminResponse | null
  ) {
    if (data) {
      this.nombre = data.nombre;
      this.email = data.email;
      this.rol = data.rol;
    }
  }

  get passwordCorto(): boolean {
    return !!this.password && this.password.length > 0 && this.password.length < 6;
  }

  get puedeGuardar(): boolean {
    if (!this.nombre || !this.email || !this.rol) return false;
    if (!this.data && (!this.password || this.passwordCorto)) return false;
    return true;
  }

  guardar(): void {
    if (!this.puedeGuardar) return;

    this.cargando = true;
    this.error = '';

    const siguiente = {
      next: () => this.dialogRef.close(true),
      error: (err: any) => {
        this.error = err.error?.message || err.error?.error || 'Error al guardar';
        this.cargando = false;
      }
    };

    if (this.data) {
      this.adminService.actualizarUsuario(this.data.id, {
        nombre: this.nombre,
        email: this.email
      }).subscribe(siguiente);
    } else {
      this.adminService.crearUsuario({
        nombre: this.nombre,
        email: this.email,
        password: this.password,
        rol: this.rol
      }).subscribe(siguiente);
    }
  }
}
