import { Routes } from '@angular/router';
import { AuthGuard } from './core/auth.guard';
import { ImagenesRegistro } from './registros/imagenes/imagenes';

export const routes: Routes = [
  { path: '', redirectTo: 'login', pathMatch: 'full' },

  { path: 'visualizar/:numeroSolicitud', component: ImagenesRegistro },

  {
    path: 'login',
    loadComponent: () => import('./auth/login/login').then(m => m.LoginComponent)
  },
  {
    path: 'dashboard',
    canActivate: [AuthGuard],
    loadChildren: () => import('./dashboard/dashboard.routes').then(m => m.dashboardRoutes)
  },
  { path: '**', redirectTo: 'dashboard', pathMatch: 'full' }
];


