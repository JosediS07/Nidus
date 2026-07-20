import { Component, ChangeDetectionStrategy, ChangeDetectorRef } from '@angular/core';
import { RouterModule, Router } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../core/services/auth.service';
import { AuthResponse } from '../../core/models/auth.models';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [CommonModule, RouterModule, MatButtonModule],
  templateUrl: './header.component.html',
  styleUrl: './header.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class HeaderComponent {
  usuario: AuthResponse | null;

  constructor(
    private authService: AuthService,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) {
    this.usuario = this.authService.getUser();
    this.authService.usuario$.subscribe((u) => {
      this.usuario = u;
      this.cdr.markForCheck();
    });
  }

  get esAdmin() {
    return this.usuario?.rol === 'ADMIN';
  }

  get inicialUsuario() {
    return this.usuario?.nombre.charAt(0).toUpperCase() || '';
  }

  salir(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
