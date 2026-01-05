import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { RegistroRequest } from '../registros/formulario/formulario';

export interface ArchivoDTO {
  nombreArchivo: string;
  url: string;
  tipoDocumento: string;
}

export interface RegistroDTO {
  numeroSolicitud: string;
  carpetaRuta: string;
  creador: string;
  fechaCreacion: string;
  correosAutorizados?: string[];
  imagenes?: ArchivoDTO[];   
  esDueño?: boolean;         
}

@Injectable({ providedIn: 'root' })
export class RegistroService {
  private readonly baseUrl = 'http://localhost:8080/api/registros';
  private readonly base2Url = 'http://localhost:8080/api/registro';

  constructor(private readonly http: HttpClient) {}

  obtenerRegistros(): Observable<RegistroDTO[]> {
    return this.http.get<RegistroDTO[]>(this.baseUrl, {
      headers: this.getAuthHeaders()
    });
  }

  obtenerRegistro(numeroSolicitud: string): Observable<RegistroDTO> {
    return this.http.get<RegistroDTO>(`${this.baseUrl}/${numeroSolicitud}`, {
      headers: this.getAuthHeaders()
    });
  }

  private getAuthHeaders(): HttpHeaders {
  const token = localStorage.getItem('token');
  return new HttpHeaders({
    Authorization: `Bearer ${token}`
  });
}
  subirImagen(numeroSolicitud: string, formData: FormData): Observable<any> {
    return this.http.post(`${this.baseUrl}/${numeroSolicitud}/imagenes`, formData, {
      headers: this.getAuthHeaders()
    });
  }
  crearRegistro(dto: RegistroRequest): Observable<RegistroDTO> {
  return this.http.post<RegistroDTO>(this.base2Url, dto, {
    headers: this.getAuthHeaders()
  });
}



}

