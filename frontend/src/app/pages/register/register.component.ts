import { Component, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatInputModule } from '@angular/material/input';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../core/services/auth.service';
import { RegisterRequest } from '../../core/models/auth.models';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule, MatButtonModule, MatInputModule],
  templateUrl: './register.component.html',
  styleUrl: './register.component.css'
})
export class RegisterComponent {
  private fb = inject(FormBuilder);
  form = this.fb.group({
    nombre: ['', Validators.required],
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(6)]],
  });
  cargando = false;
  error = '';

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  get nombreInvalido() {
    return this.form.get('nombre')?.invalid && this.form.get('nombre')?.touched;
  }

  get emailInvalido() {
    return this.form.get('email')?.invalid && this.form.get('email')?.touched;
  }

  get passwordInvalido() {
    return this.form.get('password')?.invalid && this.form.get('password')?.touched;
  }

  get textoBoton() {
    return this.cargando ? 'Creando...' : 'Registrarse';
  }

  enviar(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    this.cargando = true;
    this.error = '';

    this.authService.register(this.form.value as RegisterRequest).subscribe({
      next: () => this.router.navigate(['/recursos']),
      error: (err) => {
        this.error = err.error?.message || 'Error al registrarse';
        this.cargando = false;
      }
    });
  }
}
