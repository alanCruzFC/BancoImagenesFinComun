import { RenderMode, ServerRoute } from '@angular/ssr';

export const serverRoutes: ServerRoute[] = [
  // raíz → prerender (redirige a login en cliente)
  { path: '', renderMode: RenderMode.Prerender },

  // estáticas
  { path: 'login', renderMode: RenderMode.Prerender },
  { path: 'dashboard', renderMode: RenderMode.Prerender },

  // dinámicas → Server (no prerender)
  { path: 'visualizar/:numeroSolicitud', renderMode: RenderMode.Server },
  { path: 'dashboard/visualizar/:numeroSolicitud', renderMode: RenderMode.Server },

  // catch‑all → Server
  { path: '**', renderMode: RenderMode.Server }
];
