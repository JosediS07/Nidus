import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AdminService } from '../../core/services/admin.service';
import { DashboardResponse } from '../../core/models/admin.models';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.css'
})
export class DashboardComponent implements OnInit {
  data?: DashboardResponse;

  constructor(private adminService: AdminService) {}

  ngOnInit(): void {
    this.adminService.dashboard().subscribe((d) => (this.data = d));
  }

  get reservasPorEstadoArr() {
    return Object.entries(this.data?.reservasPorEstado || {});
  }
}
