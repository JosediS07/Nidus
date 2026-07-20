import { Component, Input, Output, EventEmitter, ChangeDetectionStrategy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';

@Component({
  selector: 'app-paginacion',
  standalone: true,
  imports: [CommonModule, MatButtonModule],
  template: `
    @if (hayPaginacion) {
      <div class="flex items-center justify-between px-6 py-4 border-t border-subtle">
        <span class="text-sm text-secondary">{{ total }} {{ etiqueta }}</span>
        <div class="flex gap-2">
          <button mat-stroked-button (click)="cambiar(pagina - 1)" [disabled]="pagina === 0" class="btn-pagina">Anterior</button>
          <button mat-stroked-button (click)="cambiar(pagina + 1)" [disabled]="!hayPaginaSiguiente" class="btn-pagina">Siguiente</button>
        </div>
      </div>
    }
  `,
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class PaginationComponent {
  @Input({ required: true }) total!: number;
  @Input({ required: true }) pagina!: number;
  @Input() tamanioPagina = 20;
  @Input() etiqueta = 'registros';
  @Output() cambioPagina = new EventEmitter<number>();

  get hayPaginaSiguiente(): boolean {
    return (this.pagina + 1) * this.tamanioPagina < this.total;
  }

  get hayPaginacion(): boolean {
    return this.total > this.tamanioPagina;
  }

  cambiar(page: number): void {
    this.cambioPagina.emit(page);
  }
}
