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
import { RecursoResponse } from '../../../core/models/recurso.models';

@Component({
  selector: 'app-recurso-dialog',
  standalone: true,
  imports: [CommonModule, FormsModule, MatButtonModule, MatDialogModule, MatFormFieldModule, MatInputModule, MatSelectModule, MatOptionModule],
  template: `
    <h2 mat-dialog-title>{{ data ? 'Editar recurso' : 'Nuevo recurso' }}</h2>
    <mat-dialog-content>
      <div class="flex flex-col gap-4 pt-4">
        <mat-form-field appearance="fill">
          <mat-label>Nombre</mat-label>
          <input matInput [(ngModel)]="nombre" required />
        </mat-form-field>
        <mat-form-field appearance="fill">
          <mat-label>Tipo</mat-label>
          <mat-select [(ngModel)]="tipo" required>
            <mat-option value="SALA">SALA</mat-option>
            <mat-option value="PROYECTOR">PROYECTOR</mat-option>
            <mat-option value="VEHICULO">VEHICULO</mat-option>
            <mat-option value="OTRO">OTRO</mat-option>
          </mat-select>
        </mat-form-field>
        <mat-form-field appearance="fill">
          <mat-label>Descripción</mat-label>
          <textarea matInput [(ngModel)]="descripcion" rows="3"></textarea>
        </mat-form-field>
        <mat-form-field appearance="fill">
          <mat-label>Capacidad</mat-label>
          <input matInput type="number" [(ngModel)]="capacidad" />
        </mat-form-field>
      </div>
    </mat-dialog-content>
    <mat-dialog-actions align="end">
      <button mat-button mat-dialog-close>Cancelar</button>
      <button mat-flat-button color="primary" (click)="guardar()" [disabled]="!nombre || !tipo">Guardar</button>
    </mat-dialog-actions>
  `
})
export class RecursoDialogComponent {
  nombre = '';
  tipo = '';
  descripcion = '';
  capacidad: number | null = null;

  constructor(
    private adminService: AdminService,
    private dialogRef: MatDialogRef<RecursoDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: RecursoResponse | null
  ) {
    if (data) {
      this.nombre = data.nombre;
      this.tipo = data.tipo;
      this.descripcion = data.descripcion;
      this.capacidad = data.capacidad;
    }
  }

  guardar(): void {
    if (!this.nombre || !this.tipo) return;
    const body: any = {
      nombre: this.nombre,
      tipo: this.tipo,
      descripcion: this.descripcion,
      capacidad: this.capacidad || undefined
    };
    const peticion = this.data
      ? this.adminService.actualizarRecurso(this.data.id, body)
      : this.adminService.crearRecurso(body);
    peticion.subscribe({
      next: () => this.dialogRef.close(true),
      error: () => this.dialogRef.close(false)
    });
  }
}
