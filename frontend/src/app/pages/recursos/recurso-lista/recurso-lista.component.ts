import { Component, OnInit, ChangeDetectionStrategy, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { RecursoService } from '../../../core/services/recurso.service';
import { RecursoResponse } from '../../../core/models/recurso.models';

@Component({
  selector: 'app-recurso-lista',
  standalone: true,
  imports: [CommonModule, RouterModule, MatButtonModule],
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

  private readonly TAMANIO_PAGINA = 20;

  get hayPaginaSiguiente(): boolean {
    return (this.pagina + 1) * this.TAMANIO_PAGINA < this.total;
  }

  get hayPaginacion(): boolean {
    return this.total > this.TAMANIO_PAGINA;
  }

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

  cambiarPagina(evento: any): void {
    this.cargar(evento.pageIndex);
  }
}
