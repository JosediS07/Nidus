import { Component, OnInit, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { MatIconModule } from '@angular/material/icon';
import { MatSnackBarModule, MatSnackBar } from '@angular/material/snack-bar';
import { HttpErrorResponse } from '@angular/common/http';
import { ReservaService } from '../../../core/services/reserva.service';
import { RecursoService } from '../../../core/services/recurso.service';
import { RecursoResponse } from '../../../core/models/recurso.models';

@Component({
  selector: 'app-reserva-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, MatButtonModule, MatInputModule, MatSelectModule,
            MatDatepickerModule, MatNativeDateModule, MatIconModule, MatSnackBarModule],
  templateUrl: './reserva-form.component.html',
  styleUrl: './reserva-form.component.css'
})
export class ReservaFormComponent implements OnInit {
  private fb = inject(FormBuilder);
  form = this.fb.group({
    recursoId: [0, Validators.required],
    fechaInicioDate: [null as Date | null, Validators.required],
    fechaInicioHora: [9, Validators.required],
    fechaInicioMinuto: [0, Validators.required],
    fechaFinDate: [null as Date | null, Validators.required],
    fechaFinHora: [11, Validators.required],
    fechaFinMinuto: [0, Validators.required],
  });

  horas = Array.from({ length: 24 }, (_, i) => i);
  minutos = [0, 5, 10, 15, 20, 25, 30, 35, 40, 45, 50, 55];

  editando = false;
  reservaId?: number;
  recursos: RecursoResponse[] = [];
  guardando = false;
  error = '';

  get tituloPagina() {
    return this.editando ? 'Editar reserva' : 'Nueva reserva';
  }

  get textoBoton() {
    return this.guardando ? 'Guardando...' : 'Guardar';
  }

  constructor(
    private reservaService: ReservaService,
    private recursoService: RecursoService,
    private snackBar: MatSnackBar,
    private router: Router,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.recursoService.listar(0, 100).subscribe({
      next: (respuesta) => (this.recursos = respuesta.content),
      error: (err) => { this.error = err.error?.message || 'Error al cargar recursos'; }
    });

    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.editando = true;
      this.reservaId = Number(id);
      this.reservaService.obtener(this.reservaId).subscribe({
        next: (reserva) => {
          const inicio = new Date(reserva.fechaInicio);
          const fin = new Date(reserva.fechaFin);
          this.form.patchValue({
            fechaInicioDate: inicio,
            fechaInicioHora: inicio.getHours(),
            fechaInicioMinuto: inicio.getMinutes(),
            fechaFinDate: fin,
            fechaFinHora: fin.getHours(),
            fechaFinMinuto: fin.getMinutes(),
          });
        },
        error: (error) => { this.error = error.error?.message || 'Error al cargar reserva'; }
      });
    }
  }

  guardar(): void {
    if (this.form.invalid) return;
    this.guardando = true;
    this.error = '';

    const v = this.form.value;
    const fechaInicio = new Date(v.fechaInicioDate!);
    fechaInicio.setHours(v.fechaInicioHora!, v.fechaInicioMinuto!, 0, 0);
    const fechaFin = new Date(v.fechaFinDate!);
    fechaFin.setHours(v.fechaFinHora!, v.fechaFinMinuto!, 0, 0);

    const req = {
      recursoId: v.recursoId!,
      fechaInicio: fechaInicio.toISOString(),
      fechaFin: fechaFin.toISOString(),
    };

    const action = this.editando
      ? this.reservaService.modificar(this.reservaId!, { fechaInicio: req.fechaInicio, fechaFin: req.fechaFin })
      : this.reservaService.crear(req);

    action.subscribe(this.handleGuardarResponse());
  }

  private handleGuardarResponse() {
    return {
      next: () => {
        this.snackBar.open('Reserva guardada', 'Cerrar', { duration: 3000 });
        this.router.navigate(['/reservas']);
      },
      error: (err: HttpErrorResponse) => {
        this.error = err.error?.message || 'Error al guardar';
        this.guardando = false;
      }
    };
  }
}
