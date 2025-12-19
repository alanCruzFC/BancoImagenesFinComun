import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { AuthService } from '../../core/auth.service';

interface NavItem {
  label: string;
  route: string;
  roles: string[];
}

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './sidebar.html',
})
export class SidebarComponent {
  navItems: NavItem[] = [
    { label: 'Registros', route: 'registros', roles: ['USER', 'SUPERVISOR', 'ADMIN', 'SUPERADMIN'] },
    { label: 'Usuarios', route: 'usuarios', roles: ['ADMIN', 'SUPERADMIN'] },
    { label: 'API Keys', route: 'apikeys', roles: ['ADMIN', 'SUPERADMIN'] }
  ];
  logoAnimating = false;

  constructor(private readonly authService: AuthService, private readonly router: Router) {}

  get visibleItems(): NavItem[] {
    const rol = this.authService.getRol();
    return this.navItems.filter(item => item.roles.includes(rol));
  }

  get nombre(): string {
    return this.authService.getNombre();
  }

  get rol(): string {
    return this.authService.getRol();
  }

  goToDashboard(event: Event) {
    event.preventDefault();
    // Animación visual breve antes de navegar
    this.logoAnimating = true;
    // Duración de la animación: 300ms (ajusta si quieres más/menos)
    setTimeout(() => {
      this.logoAnimating = false;
      this.router.navigate(['/dashboard']);
    }, 300);
  }
}

