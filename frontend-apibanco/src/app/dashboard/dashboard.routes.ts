import { Routes } from '@angular/router';
import { DashboardComponent } from './dashboard';
import { RoleGuard } from '../core/role-guard';

export const dashboardRoutes: Routes = [
  {
    path: '',
    component: DashboardComponent,
    children: [
      {
        path: 'registros',
        canActivate: [RoleGuard],
        data: { roles: ['USER', 'SUPERVISOR', 'ADMIN','SUPERADMIN'] },
        loadComponent: () => import('../registros/listado/listado').then(m => m.ListadoRegComponent)
      },
      {
        path: 'usuarios',
        canActivate: [RoleGuard],
        data: { roles: ['ADMIN','SUPERADMIN'] },
        loadComponent: () => import('../usuarios/listado/listado').then(m => m.ListadoComponent)
      },
      {
        path: 'apikeys',
        canActivate: [RoleGuard],
        data: { roles: ['ADMIN','SUPERADMIN'] },
        loadComponent: () => import('../apikeys/listado/listado').then(m => m.ListadoApiComponent)
      },
      {
        path: 'visualizar/:numeroSolicitud',
        canActivate: [RoleGuard],
        data: { roles: ['USER', 'SUPERVISOR', 'ADMIN','SUPERADMIN'] },
        loadComponent: () => import('../registros/imagenes/imagenes').then(m => m.ImagenesRegistro)
      },
      {
        path: '',
        redirectTo: 'registros',
        pathMatch: 'full'
      }
    ]
  }
];

