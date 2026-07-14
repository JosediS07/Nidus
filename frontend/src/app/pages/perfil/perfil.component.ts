import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatInputModule } from '@angular/material/input';
import { MatSnackBarModule, MatSnackBar } from '@angular/material/snack-bar';
import { AuthService } from '../../core/services/auth.service';

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
    if (this.form.value.password && this.form.value.password !== this.form.value.confirmarPassword) {
      this.snackBar.open('Las contraseñas no coinciden', 'Cerrar', { duration: 4000 });
      return;
    }

    this.guardando = true;
    const body: any = { nombre: this.form.value.nombre, email: this.form.value.email };
    if (this.form.value.password) {
      body.password = this.form.value.password;
      body.currentPassword = this.form.value.currentPassword;
    }

    this.authService.actualizarPerfil(body).subscribe({
      next: () => {
        this.snackBar.open('Perfil actualizado', 'Cerrar', { duration: 3000 });
        this.form.patchValue({ password: '', confirmarPassword: '', currentPassword: '' });
        this.guardando = false;
      },
      error: (err) => {
        this.snackBar.open(err.error?.message || 'Error al actualizar', 'Cerrar', { duration: 4000 });
        this.guardando = false;
      }
    });
  }
}
