import { Injectable } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class UserService {
  private getPayload(): any {
    const token = localStorage.getItem('token');
    if (!token) return null;
    try {
      return JSON.parse(atob(token.split('.')[1]));
    } catch {
      return null;
    }
  }

  getNombre(): string {
    return this.getPayload()?.nombre || '';
  }

  getEmail(): string {
    return this.getPayload()?.email || '';
  }

  getRol(): string {
    return this.getPayload()?.rol || '';
  }

  getId(): string {
    return this.getPayload()?.id || '';
  }

  isExpired(): boolean {
    const exp = this.getPayload()?.exp;
    const now = Math.floor(Date.now() / 1000);
    return exp < now;
  }
}
