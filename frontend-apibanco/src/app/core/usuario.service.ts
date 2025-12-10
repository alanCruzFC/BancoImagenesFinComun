import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class UsuarioService {
  private readonly apiUrl = 'http://localhost:8080/api/usuarios';

  constructor(private readonly http: HttpClient) {}

  // Obtener todos los correos (con filtro opcional)
  obtenerCorreos(filtro: string = ''): Observable<string[]> {
    return this.http.get<string[]>(`${this.apiUrl}/correos?filtro=${filtro}`);
  }
}
