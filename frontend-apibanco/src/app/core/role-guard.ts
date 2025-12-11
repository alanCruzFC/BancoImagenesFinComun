import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from './auth.service';

export const RoleGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  const rol = authService.getRol();
  const allowedRoles: string[] = route.data?.['roles'] || [];

  if (!authService.isAuthenticated()) {
    router.navigate(['/login']);
    return false;
  }

  if (!allowedRoles.includes(rol)) {
    router.navigate(['/dashboard']);
    return false;
  }

  return true;
};

