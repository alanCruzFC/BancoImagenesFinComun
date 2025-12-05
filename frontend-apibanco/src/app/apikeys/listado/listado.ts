import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { FormularioApiKey } from "../formulario/formulario";
import { FormsModule } from '@angular/forms';

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
}

@Component({
  selector: 'app-apikeys-listado',
  standalone: true,
  imports: [CommonModule, FormularioApiKey, FormsModule],
  templateUrl: './listado.html',
  styleUrls: ['./listado.css']
})
export class ListadoApiComponent {
  apikeys: ApiKey[] = [];
  apikeysFiltradas: ApiKey[] = [];
  modalVisibleApikey: boolean = false;
  searchTerm: string = '';

  claveVisible: { [id: number]: boolean } = {};
  apikeySeleccionada: ApiKey | null = null;

  constructor(private http: HttpClient) {}

  ngOnInit(): void {
    this.http.get<ApiKey[]>('http://localhost:8080/api/apikeys').subscribe({
      next: (data) => {
        // Mapear y convertir fechaCreacion a Date
        this.apikeys = data.map(item => ({
          ...item,
          fechaCreacion: item.fechaCreacion ? new Date(item.fechaCreacion) : null
        }));

        // Inicializar visibilidad de claves
        this.apikeys.forEach(key => this.claveVisible[key.id] = false);

        this.apikeysFiltradas = [...this.apikeys];
      },
      error: () => console.error('Error al obtener API Keys')
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
    const term = this.searchTerm.toLowerCase();
    this.apikeysFiltradas = this.apikeys.filter(key =>
      key.consumidor.toLowerCase().includes(term) ||
      key.clave.toLowerCase().includes(term) ||
      key.id.toString().includes(term)
    );
  }

  toggleClave(id: number): void {
    this.claveVisible[id] = !this.claveVisible[id];
  }
}

