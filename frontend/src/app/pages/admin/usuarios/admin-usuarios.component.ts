import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { AdminService } from '../../../core/services/admin.service';
import { UsuarioAdminResponse } from '../../../core/models/admin.models';

@Component({
  selector: 'app-admin-usuarios',
  standalone: true,
  imports: [CommonModule, MatButtonModule],
  templateUrl: './admin-usuarios.component.html',
  styleUrl: './admin-usuarios.component.css'
})
export class AdminUsuariosComponent implements OnInit {
  usuarios: UsuarioAdminResponse[] = [];
  total = 0;
  pagina = 0;

  constructor(private adminService: AdminService) {}

  ngOnInit(): void {
    this.cargar();
  }

  cargar(page = 0): void {
    this.pagina = page;
    this.adminService.listarUsuarios(page).subscribe((res: any) => {
      this.usuarios = res.content;
      this.total = res.totalElements;
    });
  }

  cambiarPagina(e: any): void {
    this.cargar(e.pageIndex);
  }
}
