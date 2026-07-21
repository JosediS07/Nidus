import { Component, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatInputModule } from '@angular/material/input';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../core/services/auth.service';
import { LoginRequest } from '../../core/models/auth.models';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule, MatButtonModule, MatInputModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {
  private fb = inject(FormBuilder);
  form = this.fb.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', Validators.required],
  });
  cargando = false;
  error = '';

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  get emailInvalido() {
    return this.form.get('email')?.invalid && this.form.get('email')?.touched;
  }

  get passwordInvalido() {
    return this.form.get('password')?.invalid && this.form.get('password')?.touched;
  }

  get textoBoton() {
    return this.cargando ? 'Entrando...' : 'Entrar';
  }

  enviar(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    this.cargando = true;
    this.error = '';

    this.authService.login(this.form.value as LoginRequest).subscribe({
      next: () => {
        const destino = this.authService.isAdmin() ? '/dashboard' : '/recursos';
        this.router.navigate([destino]);
      },
      error: (err) => {
        this.error = err.error?.message || 'Error al iniciar sesión';
        this.cargando = false;
      }
    });
  }
}
