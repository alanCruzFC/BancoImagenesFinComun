import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RegistroService, RegistroDTO } from '../../core/registro.service';
import { RouterLink } from "@angular/router";
import { FormularioRegistro } from '../formulario/formulario';
import { AuthService } from '../../core/auth.service';
import { FormsModule } from '@angular/forms';
import { ToastService } from '../../shared/toast/toast.service';

@Component({
  selector: 'app-listado',
  standalone: true,
  imports: [CommonModule, RouterLink, FormularioRegistro, FormsModule],
  templateUrl: './listado.html',
})
export class ListadoRegComponent implements OnInit {
  registros: RegistroDTO[] = [];
  mostrarModal: boolean = false;
  rol: string;
  busqueda: string = '';

  constructor(
    private readonly registroService: RegistroService,
    private readonly authService: AuthService,
    private readonly toastService: ToastService
  ) {
    this.rol = this.authService.getRol();
  }

  ngOnInit(): void {
    this.cargarRegistros();
  }

  cargarRegistros(): void {
    this.registroService.obtenerRegistros().subscribe({
      next: (data) => this.registros = data,
      error: () => this.toastService.error('Error al cargar registros', 'Error')
    });
  }

  abrirModal(): void {
    this.mostrarModal = true;
  }

  cerrarModal(): void {
    this.mostrarModal = false;
  }

  onRegistroCreado(nuevo: RegistroDTO): void {
    this.registros.push(nuevo);
  }

  onRegistroGuardado(): void {
    this.cargarRegistros();
    this.cerrarModal();
  }

  columnaOrdenada: string = '';
  ordenAscendente: boolean = true;

  get registrosFiltrados(): RegistroDTO[] {
    let filtrados = this.registros;
    if (this.busqueda) {
      const filtro = this.busqueda.trim().toLowerCase();
      filtrados = this.registros.filter(r =>
        r.numeroSolicitud.toString().toLowerCase().includes(filtro) ||
        r.creador.toLowerCase().includes(filtro)
      );
    }

    if (this.columnaOrdenada) {
      filtrados.sort((a, b) => {
        const valorA = a[this.columnaOrdenada as keyof RegistroDTO] ?? '';
        const valorB = b[this.columnaOrdenada as keyof RegistroDTO] ?? '';

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
}
