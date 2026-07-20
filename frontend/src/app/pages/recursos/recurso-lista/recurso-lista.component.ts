import { Component, OnInit, ChangeDetectionStrategy, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { PaginationComponent } from '../../../shared/pagination/pagination.component';
import { RecursoService } from '../../../core/services/recurso.service';
import { RecursoResponse } from '../../../core/models/recurso.models';

@Component({
  selector: 'app-recurso-lista',
  standalone: true,
  imports: [CommonModule, RouterModule, MatButtonModule, PaginationComponent],
  templateUrl: './recurso-lista.component.html',
  styleUrl: './recurso-lista.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class RecursoListaComponent implements OnInit {
  recursos: RecursoResponse[] = [];
  total = 0;
  pagina = 0;
  cargando = false;
  error = '';

  constructor(private recursoService: RecursoService, private cdr: ChangeDetectorRef) {}

  ngOnInit(): void {
    this.cargar();
  }

  cargar(page = 0): void {
    this.pagina = page;
    this.cargando = true;
    this.error = '';
    this.recursoService.listar(page).subscribe({
      next: (respuesta) => {
        this.recursos = respuesta.content;
        this.total = respuesta.totalElements;
        this.cargando = false;
        this.cdr.markForCheck();
      },
      error: (err) => {
        this.error = err.error?.message || 'Error al cargar recursos';
        this.cargando = false;
      }
    });
  }

}
