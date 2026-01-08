import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { FormularioApiKey } from "../formulario/formulario";
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../core/auth.service';

export interface ApiKey {
  id: number;
  consumidor: string;
  clave: string;
  activo: boolean;
  lectura: boolean;
  escritura: boolean;
  actualizacion: boolean;
  eliminacion: boolean;
  fechaCreacion: Date | null;
  fechaEliminacion?: Date | null; // opcional para auditoría
}

@Component({
  selector: 'app-apikeys-listado',
  standalone: true,
  imports: [CommonModule, FormularioApiKey, FormsModule],
  templateUrl: './listado.html'
})
export class ListadoApiComponent implements OnInit {
  apikeys: ApiKey[] = [];
  apikeysFiltradas: ApiKey[] = [];
  modalVisibleApikey = false;
  searchTerm = '';

  claveVisible: Record<number, boolean> = {};
  apikeySeleccionada: ApiKey | null = null;
  rolActual: string = '';

  private readonly apiUrl = 'http://localhost:8080/api/apikeys';

  constructor(
    private readonly http: HttpClient,
    private readonly authService: AuthService
  ) {}

  ngOnInit(): void {
    this.cargarApiKeys();
    this.rolActual = this.authService.getRol();
  }

  private cargarApiKeys(): void {
    this.http.get<ApiKey[]>(this.apiUrl).subscribe({
      next: (data) => {
        this.apikeys = data
          .map(item => ({
            ...item,
            fechaCreacion: item.fechaCreacion ? new Date(item.fechaCreacion) : null,
            fechaEliminacion: item.fechaEliminacion ? new Date(item.fechaEliminacion) : null
          }))
          .filter(item => item.activo); // solo activas

        this.apikeys.forEach(key => this.claveVisible[key.id] = false);
        this.apikeysFiltradas = [...this.apikeys];
      },
      error: (err) => console.error('❌ Error al obtener API Keys', err)
    });
  }

  abrirModalApiKey(apiKey?: ApiKey): void {
    this.apikeySeleccionada = apiKey || null;
    this.modalVisibleApikey = true;
  }

  cerrarModalApiKey(): void {
    this.modalVisibleApikey = false;
    this.apikeySeleccionada = null;
  }

  filtrarApiKeys(): void {
    const term = this.searchTerm.trim().toLowerCase();
    this.apikeysFiltradas = this.apikeys.filter(key =>
      key.consumidor.toLowerCase().includes(term) ||
      key.clave.toLowerCase().includes(term) ||
      key.id.toString().includes(term)
    );
  }

  toggleClave(id: number): void {
    this.claveVisible[id] = !this.claveVisible[id];
  }

  borrarApiKey(id: number): void {
    this.http.delete<void>(`${this.apiUrl}/${id}`).subscribe({
      next: () => {
        alert('🗑️ API Key desactivada correctamente');
        this.cargarApiKeys();
      },
      error: () => alert('❌ Error al desactivar API Key')
    });
  }

  puedeSubir(): boolean {
    return this.rolActual === 'SUPERADMIN';
  }

  copiarClave(clave: string): void {
    navigator.clipboard.writeText(clave)
      .then(() => {
        alert("Clave copiada al portapapeles");
      })
      .catch(err => {
        console.error("Error al copiar la clave: ", err);
      });
  }

}

