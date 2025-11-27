import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
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
  styleUrls: ['./sidebar.css']
})
export class SidebarComponent {
  navItems: NavItem[] = [
    { label: 'Registros', route: 'registros', roles: ['USER', 'SUPERVISOR', 'ADMIN'] },
    { label: 'Usuarios', route: 'usuarios', roles: ['ADMIN'] },
    { label: 'API Keys', route: 'apikeys', roles: ['ADMIN'] }
  ];

  constructor(private authService: AuthService) {}

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
}
