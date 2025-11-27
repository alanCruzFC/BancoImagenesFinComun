import { RenderMode, ServerRoute } from '@angular/ssr';

export const serverRoutes: ServerRoute[] = [
  // Rutas estáticas que sí se pueden prerender
  { path: 'login', renderMode: RenderMode.Prerender },
  { path: 'dashboard', renderMode: RenderMode.Prerender },

  // Rutas dinámicas → Server (no prerender)
  { path: 'visualizar/:numeroSolicitud', renderMode: RenderMode.Server },
  { path: 'dashboard/visualizar/:numeroSolicitud', renderMode: RenderMode.Server },

  // Catch‑all → Server (para cualquier otra ruta no listada)
  { path: '**', renderMode: RenderMode.Server }
];
