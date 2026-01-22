import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { AuthService } from '../../core/auth.service';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './navbar.html',
})
export class NavbarComponent {
  constructor(
    private readonly authService: AuthService,
    private readonly router: Router
  ) { }

  get nombre(): string {
    return this.authService.getNombre();
  }

  get rol(): string {
    return this.authService.getRol();
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}


