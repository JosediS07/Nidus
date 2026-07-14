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
            <input matInput type="password" [(ngModel)]="password" required />
          </mat-form-field>
        }
        <mat-form-field appearance="fill">
          <mat-label>Rol</mat-label>
          <mat-select [(ngModel)]="rol" required>
            <mat-option value="USER">USER</mat-option>
            <mat-option value="ADMIN">ADMIN</mat-option>
          </mat-select>
        </mat-form-field>
      </div>
    </mat-dialog-content>
    <mat-dialog-actions align="end">
      <button mat-button mat-dialog-close>Cancelar</button>
      <button mat-flat-button color="primary" (click)="guardar()" [disabled]="!nombre || !email || (!password && !data) || !rol">Guardar</button>
    </mat-dialog-actions>
  `
})
export class UsuarioDialogComponent {
  nombre = '';
  email = '';
  password = '';
  rol = 'USER';

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

  guardar(): void {
    if (!this.nombre || !this.email || !this.rol) return;
    if (!this.data && !this.password) return;

    if (this.data) {
      this.adminService.actualizarUsuario(this.data.id, {
        nombre: this.nombre,
        email: this.email
      }).subscribe({
        next: () => this.dialogRef.close(true),
        error: () => this.dialogRef.close(false)
      });
    } else {
      this.adminService.crearUsuario({
        nombre: this.nombre,
        email: this.email,
        password: this.password,
        rol: this.rol
      }).subscribe({
        next: () => this.dialogRef.close(true),
        error: () => this.dialogRef.close(false)
      });
    }
  }
}
