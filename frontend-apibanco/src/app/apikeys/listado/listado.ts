import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { FormularioApiKey } from "../formulario/formulario";
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../core/auth.service';
import { ToastService } from '../../shared/toast/toast.service';

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
  fechaEliminacion?: Date | null;
}

@Component({
  selector: 'app-apikeys-listado',
  standalone: true,
  imports: [CommonModule, FormularioApiKey, FormsModule],
  templateUrl: './listado.html'
})
export class ListadoApiComponent implements OnInit {
  apikeys: ApiKey[] = [];
  // apikeysFiltradas: ApiKey[] = [];
  modalVisibleApikey = false;
  searchTerm = '';

  claveVisible: Record<number, boolean> = {};
  apikeySeleccionada: ApiKey | null = null;
  rolActual: string = '';

  showConfirmDelete = false;
  idPendienteBorrar: number | null = null;

  private readonly apiUrl = 'http://localhost:8080/api/apikeys';

  constructor(
    private readonly http: HttpClient,
    private readonly authService: AuthService,
    private readonly toast: ToastService
  ) { }

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
          .filter(item => item.activo);

        this.apikeys.forEach(key => this.claveVisible[key.id] = false);
      },
      error: (err: any) => this.toast.error('❌ Error al cargar las API Keys', 'Error')
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

  columnaOrdenada: string = '';
  ordenAscendente: boolean = true;

  get apikeysFiltradas(): ApiKey[] {
    const term = this.searchTerm.trim().toLowerCase();
    let filtrados = this.apikeys.filter(key =>
      key.consumidor.toLowerCase().includes(term) ||
      key.clave.toLowerCase().includes(term) ||
      key.id.toString().includes(term)
    );

    if (this.columnaOrdenada) {
      filtrados.sort((a, b) => {
        const valorA = a[this.columnaOrdenada as keyof ApiKey] ?? '';
        const valorB = b[this.columnaOrdenada as keyof ApiKey] ?? '';

        if (valorA < valorB) {
          return this.ordenAscendente ? -1 : 1;
        }
        if (valorA > valorB) {
          return this.ordenAscendente ? 1 : -1;
        }
        return 0;
      });
    }
    return filtrados;
  }

  ordenar(columna: string): void {
    if (this.columnaOrdenada === columna) {
      this.ordenAscendente = !this.ordenAscendente;
    } else {
      this.columnaOrdenada = columna;
      this.ordenAscendente = true;
    }
  }

  filtrarApiKeys(): void {
    // Ya se maneja en el getter apikeysFiltradas
  }

  toggleClave(id: number): void {
    this.claveVisible[id] = !this.claveVisible[id];
  }

  borrarApiKey(id: number): void {
    this.http.delete<void>(`${this.apiUrl}/${id}`).subscribe({
      next: () => {
        this.toast.success('API Key desactivada correctamente', 'Éxito');
        this.cargarApiKeys();
      },
      error: () => this.toast.error('❌ Error al desactivar la API Key', 'Error')
    });
  }

  puedeSubir(): boolean {
    return this.rolActual === 'SUPERADMIN';
  }

  copiarClave(clave: string): void {
    navigator.clipboard.writeText(clave)
      .then(() => {
        this.toast.success('Clave copiada al portapapeles', 'Éxito');
      })
      .catch(err => {
        this.toast.error('❌ Error al copiar la clave', 'Error');
      });
  }

  pedirConfirmacionBorrar(id: number): void {
    this.idPendienteBorrar = id;
    this.showConfirmDelete = true;
  }

  cancelarBorrado(): void {
    this.showConfirmDelete = false;
    this.idPendienteBorrar = null;
  }

  confirmarBorrado(): void {
    if (this.idPendienteBorrar != null) {
      this.borrarApiKey(this.idPendienteBorrar);
    }
    this.showConfirmDelete = false;
    this.idPendienteBorrar = null;
  }




}

