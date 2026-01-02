import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";

@Injectable({ providedIn: 'root' })
export class ApiKeyService {
  private readonly apiUrl = 'http://localhost:8080/api/apikeys';

  constructor(private readonly http: HttpClient) {}

  getApiKeys(): Observable<ApiKey[]> {
    return this.http.get<ApiKey[]>(this.apiUrl);
  }

  borrarApiKey(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}

export interface ApiKey {
  id: number;
  keyValue: string;
  activo: boolean;
  fechaCreacion: string;
  fechaActualizacion?: string;
  fechaEliminacion?: string;
}
