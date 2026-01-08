import { Component, OnInit } from '@angular/core';
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
export class ImagenesRegistro implements OnInit {
  numeroSolicitud: string = '';
  imagenes: ArchivoDTO[] = [];
  usuarioActual: string = '';
  usuarioRol: string = '';
  registro?: RegistroDTO;
  mostrarModal: boolean = false;
  imagenSeleccionada: ArchivoDTO | null = null;

  constructor(
    private readonly route: ActivatedRoute,
    private readonly authService: AuthService,
    private readonly registroService: RegistroService
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
    return this.usuarioRol === 'SUPERADMIN' || this.registro?.creador === this.usuarioActual;
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
    const nombre = this.getNombreArchivo(img.url).toLowerCase();

    if (nombre.endsWith('.jpg') || nombre.endsWith('.jpeg') ||
        nombre.endsWith('.png') || nombre.endsWith('.gif')) {
      this.imagenSeleccionada = img;
      return;
    }

    if (nombre.endsWith('.pdf')) {
      window.open(img.url + '?inline=true', '_blank');
      return;
    }

    const link = document.createElement('a');
    link.href = img.url;
    link.download = img.nombreArchivo;
    link.click();
  }

  cerrarVisualizacion(): void {
    this.imagenSeleccionada = null;
  }

  getNombreArchivo(url: string): string {
    if (!url) return '';
    return url.split('/').pop() || url;
  }

  getThumbnail(img: ArchivoDTO): string {
    const nombre = this.getNombreArchivo(img.url).toLowerCase();

    if (nombre.endsWith('.jpg') || nombre.endsWith('.jpeg') ||
        nombre.endsWith('.png') || nombre.endsWith('.gif')) {
      return img.url;
    }
    if (nombre.endsWith('.pdf')) return 'assets/icons/pdf.png';
    if (nombre.endsWith('.doc') || nombre.endsWith('.docx')) return 'assets/icons/word.png';
    if (nombre.endsWith('.xls') || nombre.endsWith('.xlsx')) return 'assets/icons/excel.png';
    return 'assets/icons/file.png';
  }

  esImagen(img: ArchivoDTO): boolean {
    const nombre = this.getNombreArchivo(img.url).toLowerCase();
    return nombre.endsWith('.jpg') || nombre.endsWith('.jpeg') ||
           nombre.endsWith('.png') || nombre.endsWith('.gif');
  }

  formatDocumento(value: string): string {
    if (!value) return '';

    let texto = value.replaceAll('_', ' DE ');

    texto = texto.replaceAll(/([a-zA-Z])([0-9]+)/g, '$1 $2');

    texto = texto.replaceAll(/([a-z])([A-Z])/g, '$1 $2');

    return texto.trim().toUpperCase();
  }
}
