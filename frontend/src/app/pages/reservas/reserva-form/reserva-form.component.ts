import { Component, OnInit, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatSnackBarModule, MatSnackBar } from '@angular/material/snack-bar';
import { ReservaService } from '../../../core/services/reserva.service';
import { RecursoService } from '../../../core/services/recurso.service';
import { RecursoResponse } from '../../../core/models/recurso.models';

@Component({
  selector: 'app-reserva-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, MatButtonModule, MatInputModule, MatSelectModule, MatSnackBarModule],
  templateUrl: './reserva-form.component.html',
  styleUrl: './reserva-form.component.css'
})
export class ReservaFormComponent implements OnInit {
  private fb = inject(FormBuilder);
  form = this.fb.group({
    recursoId: [0, Validators.required],
    fechaInicio: ['', Validators.required],
    fechaFin: ['', Validators.required],
  });

  editando = false;
  reservaId?: number;
  recursos: RecursoResponse[] = [];
  guardando = false;
  error = '';

  constructor(
    private reservaService: ReservaService,
    private recursoService: RecursoService,
    private snackBar: MatSnackBar,
    private router: Router,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.recursoService.listar(0, 100).subscribe({
      next: (res) => (this.recursos = res.content),
      error: (err) => { this.error = err.error?.message || 'Error al cargar recursos'; }
    });

    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.editando = true;
      this.reservaId = Number(id);
      this.reservaService.obtener(this.reservaId).subscribe((r) => {
        this.form.patchValue({
          fechaInicio: this.toLocalDatetime(r.fechaInicio),
          fechaFin: this.toLocalDatetime(r.fechaFin),
        });
      });
    }
  }

  guardar(): void {
    if (this.form.invalid) return;
    this.guardando = true;
    this.error = '';

    const req = {
      recursoId: this.form.value.recursoId!,
      fechaInicio: new Date(this.form.value.fechaInicio!).toISOString(),
      fechaFin: new Date(this.form.value.fechaFin!).toISOString(),
    };

    const action = this.editando
      ? this.reservaService.modificar(this.reservaId!, { fechaInicio: req.fechaInicio, fechaFin: req.fechaFin })
      : this.reservaService.crear(req);

    action.subscribe({
      next: () => {
        this.snackBar.open('Reserva guardada', 'Cerrar', { duration: 3000 });
        this.router.navigate(['/reservas']);
      },
      error: (err) => {
        this.error = err.error?.message || 'Error al guardar';
        this.guardando = false;
      }
    });
  }

  private toLocalDatetime(iso: string): string {
    return iso?.substring(0, 16) || '';
  }
}
