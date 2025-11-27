import { Routes } from '@angular/router';
import { AuthGuard } from './core/auth.guard';
import { ImagenesRegistro } from './registros/imagenes/imagenes';

export const routes: Routes = [
  // raíz → redirige a login
  { path: '', redirectTo: 'login', pathMatch: 'full' },

  // dinámicas
  { path: 'visualizar/:numeroSolicitud', component: ImagenesRegistro },

  // estáticas
  {
    path: 'login',
    loadComponent: () => import('./auth/login/login').then(m => m.LoginComponent)
  },
  {
    path: 'dashboard',
    canActivate: [AuthGuard],
    loadChildren: () => import('./dashboard/dashboard.routes').then(m => m.dashboardRoutes)
  },

  // catch‑all
  { path: '**', redirectTo: 'dashboard', pathMatch: 'full' }
];
