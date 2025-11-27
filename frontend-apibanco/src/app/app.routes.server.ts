import { ServerRoute, RenderMode } from '@angular/ssr';

export const serverRoutes: ServerRoute[] = [
  { path: '', renderMode: RenderMode.Prerender },
  { path: 'login', renderMode: RenderMode.Prerender },
  { path: 'dashboard', renderMode: RenderMode.Prerender },

  // dinámicas → Server
  { path: 'visualizar/:numeroSolicitud', renderMode: RenderMode.Server },
  { path: 'dashboard/visualizar/:numeroSolicitud', renderMode: RenderMode.Server }
];

