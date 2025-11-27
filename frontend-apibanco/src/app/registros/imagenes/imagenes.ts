import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { AuthService } from '../../core/auth.service';
import { ArchivoDTO, RegistroDTO, RegistroService } from '../../core/registro.service';
import { FormularioImagenes } from "./formulario-imagenes/formimag";

@Component({
  selector: 'app-imagenes-registro',
  standalone: true,
  imports: [CommonModule, RouterModule, FormularioImagenes],
  templateUrl: './imagenes.html'
})
export class ImagenesRegistro {
  numeroSolicitud: string = '';
  imagenes: ArchivoDTO[] = [];
  usuarioActual: string = '';
  usuarioRol: string = '';
  registro?: RegistroDTO;
  mostrarModal: boolean = false;
  imagenSeleccionada: ArchivoDTO | null = null;  // 👈 ahora guardamos el objeto completo

  constructor(
    private route: ActivatedRoute,
    private authService: AuthService,
    private registroService: RegistroService
  ) {}

  ngOnInit(): void {
    this.numeroSolicitud = this.route.snapshot.paramMap.get('numeroSolicitud') ?? '';
    this.usuarioActual = this.authService.getNombre();
    this.usuarioRol = this.authService.getRol();
    this.cargarRegistro();
  }

  cargarRegistro(): void {
    this.registroService.obtenerRegistro(this.numeroSolicitud).subscribe({
      next: (data) => {
        this.registro = data;
        this.imagenes = (data.imagenes || []).map(img => ({
          ...img,
          url: `http://localhost:8080${img.url}`
        }));
      },
      error: (err) => {
        console.error('Error al cargar registro:', err);
        this.imagenes = [];
      }
    });
  }

  puedeSubir(): boolean {
    return this.usuarioRol === 'ADMIN' || this.registro?.creador === this.usuarioActual;
  }

  abrirModal(): void {
    this.mostrarModal = true;
  }

  cerrarModal(): void {
    this.mostrarModal = false;
  }

  onImagenesGuardadas(): void {
    this.cargarRegistro();
    this.cerrarModal(); 
  }

  abrirVisualizacion(img: ArchivoDTO): void {
    this.imagenSeleccionada = img;
  }

  cerrarVisualizacion(): void {
    this.imagenSeleccionada = null;
  }

  getNombreArchivo(url: string): string {
    if (!url) return '';
    return url.split('/').pop() || url;
  }

}
