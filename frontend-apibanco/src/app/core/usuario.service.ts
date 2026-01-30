import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Usuario {
  id: number;
  username: string;
  firstName: string;
  lastName: string;
  email: string;
  rol: string;
  activo: boolean;
  team: string;
  department: string;
  supervisorId?: number;
  supervisorName?: string;
}

@Injectable({
  providedIn: 'root'
})
export class UsuarioService {
  private readonly apiUrl = 'http://localhost:8080/api/usuarios';

  constructor(private readonly http: HttpClient) { }

  obtenerCorreos(filtro: string = ''): Observable<string[]> {
    return this.http.get<string[]>(`${this.apiUrl}/correos?filtro=${filtro}`);
  }

  getUsuarios(): Observable<Usuario[]> {
    return this.http.get<Usuario[]>(this.apiUrl);
  }

  borrarUsuario(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
