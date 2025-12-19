import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RegistroService, RegistroDTO } from '../../core/registro.service';
import { RouterLink } from "@angular/router";
import { FormularioRegistro } from '../formulario/formulario';
import { AuthService } from '../../core/auth.service';
import { FormsModule } from '@angular/forms';

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
    private readonly authService: AuthService
  ) {
    this.rol = this.authService.getRol();
  }

  ngOnInit(): void {
    this.cargarRegistros();
  }

  cargarRegistros(): void {
    this.registroService.obtenerRegistros().subscribe({
      next: (data) => this.registros = data,
      error: () => console.error('Error al obtener registros')
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
  
  get registrosFiltrados(): RegistroDTO[] {
    if (!this.busqueda) return this.registros;
    const filtro = this.busqueda.trim().toLowerCase();
    return this.registros.filter(r =>
      r.numeroSolicitud.toString().toLowerCase().includes(filtro)
    );
  }
}
