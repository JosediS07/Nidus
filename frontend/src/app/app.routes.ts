import { Routes } from '@angular/router';
import { AuthGuard, AdminGuard } from './core/guards/auth.guard';

export const routes: Routes = [
  { path: 'login', loadComponent: () => import('./pages/login/login.component').then((c) => c.LoginComponent) },
  { path: 'register', loadComponent: () => import('./pages/register/register.component').then((c) => c.RegisterComponent) },
  { path: 'dashboard', loadComponent: () => import('./pages/dashboard/dashboard.component').then((c) => c.DashboardComponent), canActivate: [AuthGuard, AdminGuard] },
  { path: 'recursos', loadComponent: () => import('./pages/recursos/recurso-lista/recurso-lista.component').then((c) => c.RecursoListaComponent), canActivate: [AuthGuard] },
  { path: 'recursos/:id', loadComponent: () => import('./pages/recursos/recurso-detalle/recurso-detalle.component').then((c) => c.RecursoDetalleComponent), canActivate: [AuthGuard] },
  { path: 'reservas', loadComponent: () => import('./pages/reservas/reserva-lista/reserva-lista.component').then((c) => c.ReservaListaComponent), canActivate: [AuthGuard] },
  { path: 'reservas/nueva', loadComponent: () => import('./pages/reservas/reserva-form/reserva-form.component').then((c) => c.ReservaFormComponent), canActivate: [AuthGuard] },
  { path: 'reservas/:id/editar', loadComponent: () => import('./pages/reservas/reserva-form/reserva-form.component').then((c) => c.ReservaFormComponent), canActivate: [AuthGuard] },
  { path: 'admin/usuarios', loadComponent: () => import('./pages/admin/usuarios/admin-usuarios.component').then((c) => c.AdminUsuariosComponent), canActivate: [AuthGuard, AdminGuard] },
  { path: 'admin/reservas', loadComponent: () => import('./pages/admin/reservas/admin-reservas.component').then((c) => c.AdminReservasComponent), canActivate: [AuthGuard, AdminGuard] },
  { path: 'admin/recursos', loadComponent: () => import('./pages/admin/recursos/admin-recursos.component').then((c) => c.AdminRecursosComponent), canActivate: [AuthGuard, AdminGuard] },
  { path: 'admin/cola', loadComponent: () => import('./pages/admin/cola/admin-cola.component').then((c) => c.AdminColaComponent), canActivate: [AuthGuard, AdminGuard] },
  { path: 'perfil', loadComponent: () => import('./pages/perfil/perfil.component').then((c) => c.PerfilComponent), canActivate: [AuthGuard] },
  { path: '', redirectTo: '/recursos', pathMatch: 'full' },
  { path: '**', redirectTo: '/recursos' },
];
