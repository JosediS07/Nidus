import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatInputModule } from '@angular/material/input';
import { MatSnackBarModule, MatSnackBar } from '@angular/material/snack-bar';
import { HttpErrorResponse } from '@angular/common/http';
import { AuthService } from '../../core/services/auth.service';
import { ActualizarPerfilRequest } from '../../core/models/auth.models';

@Component({
  selector: 'app-perfil',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, MatButtonModule, MatInputModule, MatSnackBarModule],
  templateUrl: './perfil.component.html',
  styleUrl: './perfil.component.css'
})
export class PerfilComponent implements OnInit {
  form;
  guardando = false;

  get textoBoton() {
    return this.guardando ? 'Guardando...' : 'Guardar cambios';
  }

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private snackBar: MatSnackBar
  ) {
    const user = this.authService.getUser();
    this.form = this.fb.group({
      nombre: [user?.nombre || '', Validators.required],
      email: [user?.email || '', [Validators.required, Validators.email]],
      password: [''],
      confirmarPassword: [''],
      currentPassword: [''],
    });
  }

  ngOnInit(): void {
    this.authService.me().subscribe({
      next: (usuario) => {
        this.form.patchValue({ nombre: usuario.nombre, email: usuario.email });
      },
      error: () => this.snackBar.open('Error al cargar perfil', 'Cerrar', { duration: 3000 })
    });
  }

  guardar(): void {
    if (this.form.invalid) return;
    if (!this.validarContrasenas()) return;

    this.guardando = true;
    const body = this.construirBody();
    this.authService.actualizarPerfil(body).subscribe(this.handleActualizarResponse());
  }

  private validarContrasenas(): boolean {
    if (this.form.value.password && this.form.value.password !== this.form.value.confirmarPassword) {
      this.snackBar.open('Las contraseñas no coinciden', 'Cerrar', { duration: 4000 });
      return false;
    }
    return true;
  }

  private construirBody(): ActualizarPerfilRequest {
    const body: ActualizarPerfilRequest = { nombre: this.form.value.nombre ?? '', email: this.form.value.email ?? '' };
    if (this.form.value.password) {
      body.password = this.form.value.password;
      body.currentPassword = this.form.value.currentPassword ?? undefined;
    }
    return body;
  }

  private handleActualizarResponse() {
    return {
      next: () => {
        this.snackBar.open('Perfil actualizado', 'Cerrar', { duration: 3000 });
        this.form.patchValue({ password: '', confirmarPassword: '', currentPassword: '' });
        this.guardando = false;
      },
      error: (err: HttpErrorResponse) => {
        this.snackBar.open(err.error?.message || 'Error al actualizar', 'Cerrar', { duration: 4000 });
        this.guardando = false;
      }
    };
  }
}
