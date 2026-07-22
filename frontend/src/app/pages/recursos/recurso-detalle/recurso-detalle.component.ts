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
import { RecursoService } from '../../../core/services/recurso.service';
import { ReservaService } from '../../../core/services/reserva.service';
import { ColaService } from '../../../core/services/cola.service';
import { AuthService } from '../../../core/services/auth.service';
import { RecursoResponse } from '../../../core/models/recurso.models';
import { SolicitudColaResponse } from '../../../core/models/admin.models';

@Component({
  selector: 'app-recurso-detalle',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, MatButtonModule, MatInputModule, MatSelectModule,
            MatDatepickerModule, MatNativeDateModule, MatIconModule, MatSnackBarModule],
  templateUrl: './recurso-detalle.component.html',
  styleUrl: './recurso-detalle.component.css'
})
export class RecursoDetalleComponent implements OnInit {
  private fb = inject(FormBuilder);
  formReserva = this.fb.group({
    fechaInicioDate: [null as Date | null, Validators.required],
    fechaInicioHora: [9, Validators.required],
    fechaInicioMinuto: [0, Validators.required],
    fechaFinDate: [null as Date | null, Validators.required],
    fechaFinHora: [11, Validators.required],
    fechaFinMinuto: [0, Validators.required],
  });

  horas = Array.from({ length: 24 }, (_, i) => i);
  minutos = [0, 5, 10, 15, 20, 25, 30, 35, 40, 45, 50, 55];

  recursoId!: number;
  recurso?: RecursoResponse;
  miCola: SolicitudColaResponse | null = null;
  cargando = false;
  error = '';
  reservando = false;
  reservaError = '';
  colaError = '';

  get textoBoton() {
    return this.reservando ? 'Reservando...' : 'Reservar';
  }

  constructor(
    private route: ActivatedRoute,
    private recursoService: RecursoService,
    private reservaService: ReservaService,
    private colaService: ColaService,
    private authService: AuthService,
    private snackBar: MatSnackBar,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.recursoId = Number(this.route.snapshot.paramMap.get('id'));
    this.cargando = true;
    this.recursoService.obtener(this.recursoId).subscribe({
      next: (recurso) => {
        this.recurso = recurso;
        this.cargando = false;
      },
      error: (err) => {
        this.error = err.error?.message || 'Error al cargar recurso';
        this.cargando = false;
      }
    });
    this.cargarCola();
  }

  reservar(): void {
    if (this.formReserva.invalid) return;
    this.reservando = true;
    this.reservaError = '';

    const v = this.formReserva.value;
    const fechaInicio = new Date(v.fechaInicioDate!);
    fechaInicio.setHours(v.fechaInicioHora!, v.fechaInicioMinuto!, 0, 0);
    const fechaFin = new Date(v.fechaFinDate!);
    fechaFin.setHours(v.fechaFinHora!, v.fechaFinMinuto!, 0, 0);

    const req = {
      recursoId: this.recursoId,
      fechaInicio: fechaInicio.toISOString(),
      fechaFin: fechaFin.toISOString(),
    };

    this.reservaService.crear(req).subscribe({
      next: () => {
        this.snackBar.open('Reserva creada con éxito', 'Cerrar', { duration: 3000 });
        this.router.navigate(['/reservas']);
      },
      error: (err) => {
        this.reservaError = err.error?.message || 'Error al crear reserva';
        this.reservando = false;
      }
    });
  }

  cargarCola(): void {
    this.colaService.listar(this.recursoId).subscribe({
      next: (respuesta) => {
        const userId = this.authService.getUser()?.id;
        this.miCola = respuesta.content?.find((solicitud: SolicitudColaResponse) => solicitud.usuarioId === userId) || null;
      },
      error: () => {}
    });
  }

  apuntarseCola(): void {
    this.colaService.apuntarse(this.recursoId).subscribe({
      next: () => {
        this.snackBar.open('Te has apuntado a la cola', 'Cerrar', { duration: 3000 });
        this.cargarCola();
      },
      error: (err) => {
        this.colaError = err.error?.message || 'Error al apuntarse';
      }
    });
  }

  salirCola(): void {
    if (!this.miCola) return;
    this.colaService.salir(this.miCola.id).subscribe({
      next: () => {
        this.miCola = null;
        this.snackBar.open('Has salido de la cola', 'Cerrar', { duration: 3000 });
      },
      error: (err) => {
        this.colaError = err.error?.message || 'Error al salir';
      }
    });
  }
}
