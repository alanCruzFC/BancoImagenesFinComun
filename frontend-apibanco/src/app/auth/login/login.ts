import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule, Router } from '@angular/router';
import { AuthService } from '../../core/auth.service';
import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './login.html',
})
export class LoginComponent implements OnInit {
  username = '';
  password = '';
  error = '';

  constructor(private readonly authService: AuthService, private readonly router: Router) {}

  login() {
    this.authService.login(this.username, this.password).subscribe({
      next: (response) => {
        // Guardamos token y rol en localStorage
        localStorage.setItem('token', response.token);
        localStorage.setItem('rol', response.rol);

        // Redirigimos al dashboard
        this.router.navigate(['/dashboard']);
      },
      error: () => {
        this.error = 'Credenciales inválidas';
      }
    });
  }

  ngOnInit() {
    if (this.authService.isAuthenticated() && !this.authService.isTokenExpired()) {
      this.router.navigate(['/dashboard']);
    }
  }
}
