import { Component, OnInit, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { MatInputModule } from '@angular/material/input';
import { MatSnackBarModule, MatSnackBar } from '@angular/material/snack-bar';
import { RecursoService } from '../../../core/services/recurso.service';
import { ReservaService } from '../../../core/services/reserva.service';
import { ColaService } from '../../../core/services/cola.service';
import { AuthService } from '../../../core/services/auth.service';
import { RecursoResponse } from '../../../core/models/recurso.models';

@Component({
  selector: 'app-recurso-detalle',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, MatButtonModule, MatInputModule, MatSnackBarModule],
  templateUrl: './recurso-detalle.component.html',
  styleUrl: './recurso-detalle.component.css'
})
export class RecursoDetalleComponent implements OnInit {
  private fb = inject(FormBuilder);
  formReserva = this.fb.group({
    fechaInicio: ['', Validators.required],
    fechaFin: ['', Validators.required],
  });

  recursoId!: number;
  recurso?: RecursoResponse;
  miCola: any = null;
  cargando = false;
  error = '';
  reservando = false;
  reservaError = '';
  colaError = '';

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
      next: (r) => {
        this.recurso = r;
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

    const req = {
      recursoId: this.recursoId,
      fechaInicio: new Date(this.formReserva.value.fechaInicio!).toISOString(),
      fechaFin: new Date(this.formReserva.value.fechaFin!).toISOString(),
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
      next: (respuesta: any) => {
        const userId = this.authService.getUser()?.id;
        this.miCola = respuesta.content?.find((solicitud: any) => solicitud.usuarioId === userId) || null;
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
