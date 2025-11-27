import { ServerRoute, RenderMode } from '@angular/ssr';

export const serverRoutes: ServerRoute[] = [
  // Rutas estáticas que sí se pueden prerender
  {
    path: '',
    renderMode: RenderMode.Prerender
  },
  {
    path: 'login',
    renderMode: RenderMode.Prerender
  },
  {
    path: 'dashboard',
    renderMode: RenderMode.Prerender
  },

  // Rutas dinámicas: usar Server en lugar de Prerender
  {
    path: 'visualizar/:numeroSolicitud',
    renderMode: RenderMode.Server
  },
  {
    path: 'dashboard/visualizar/:numeroSolicitud',
    renderMode: RenderMode.Server
  },

  // Otras rutas que quieras prerender (ejemplo)
  {
    path: 'usuarios',
    renderMode: RenderMode.Prerender
  },
  {
    path: 'registros',
    renderMode: RenderMode.Prerender
  },
  {
    path: 'apikeys',
    renderMode: RenderMode.Prerender
  }
];
