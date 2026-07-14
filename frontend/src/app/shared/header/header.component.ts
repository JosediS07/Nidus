import { Component, ChangeDetectionStrategy, ChangeDetectorRef } from '@angular/core';
import { RouterModule, Router } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [CommonModule, RouterModule, MatButtonModule],
  templateUrl: './header.component.html',
  styleUrl: './header.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class HeaderComponent {
  constructor(
    private authService: AuthService,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) {}

  get usuario() {
    return this.authService.getUser();
  }

  get esAdmin() {
    return this.authService.isAdmin();
  }

  get inicialUsuario() {
    return this.usuario?.nombre.charAt(0).toUpperCase() || '';
  }

  salir(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
