import { Component, OnInit, ChangeDetectionStrategy, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AdminService } from '../../core/services/admin.service';
import { DashboardResponse } from '../../core/models/admin.models';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class DashboardComponent implements OnInit {
  data?: DashboardResponse;
  cargando = false;
  error = '';

  constructor(private adminService: AdminService, private cdr: ChangeDetectorRef) {}

  ngOnInit(): void {
    this.cargar();
  }

  cargar(): void {
    this.cargando = true;
    this.error = '';
    this.adminService.dashboard().subscribe({
      next: (datos) => {
        this.data = datos;
        this.cargando = false;
        this.cdr.markForCheck();
      },
      error: (err) => {
        this.error = err.error?.message || 'Error al cargar dashboard';
        this.cargando = false;
      }
    });
  }

  get reservasPorEstadoArr() {
    return Object.entries(this.data?.reservasPorEstado || {});
  }
}
