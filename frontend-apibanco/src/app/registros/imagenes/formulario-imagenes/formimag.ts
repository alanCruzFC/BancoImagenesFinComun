
import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { forkJoin, Observable } from 'rxjs';
import { ToastService } from '../../../shared/toast/toast.service';

@Component({
  selector: 'app-formulario-imagenes',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './formimag.html'
})
export class FormularioImagenes implements OnInit {
  @Input() numeroSolicitud!: string;
  @Output() guardado = new EventEmitter<void>();

  tiposFijos: string[] = [
    'INE',
    'COMPROBANTE_DOMICILIO',
    'ESTADO_CUENTA',
    'FOTONEGOCIO1',
    'FOTONEGOCIO2',
    'SELFIE'
  ];
  documentosFijos: { tipo: string; archivo: File | null }[] = [];
  documentosExtra: { tipo: string; archivo: File | null }[] = [];

  constructor(
    private readonly http: HttpClient,
    private readonly toastService: ToastService
  ) {}

  ngOnInit(): void {
    this.documentosFijos = this.tiposFijos.map(t => ({ tipo: t, archivo: null }));
  }

  onFileSelected(event: any, doc: { tipo: string; archivo: File | null }): void {
    const file = event.target.files[0];
    if (file) {
      doc.archivo = file;
      this.revokePreviewURL(doc);
    }
  }

  agregarDocumentoExtra(): void {
    if (this.documentosExtra.length === 0 || this.documentosExtra.at(-1)?.archivo) {
      this.documentosExtra.push({ tipo: '', archivo: null });
    }
  }

  onFileSelectedExtra(event: any, doc: { tipo: string; archivo: File | null }): void {
    const input = event.target as HTMLInputElement;
    const file = input.files?.item(0);

    if (!file) {
      this.toastService.error('No se seleccionó archivo extra', 'Error');
      return;
    }
    doc.archivo = file;
    this.revokePreviewURL(doc);
  }

  getNombreArchivo(doc: { tipo: string; archivo: File | null }): string {
    return doc.archivo?.name || 'Ningún archivo seleccionado';
  }

  eliminarArchivo(doc: { tipo: string; archivo: File | null }): void {
    this.revokePreviewURL(doc);
    doc.archivo = null;
  }

  eliminarExtra(index: number): void {
    const doc = this.documentosExtra[index];
    if (doc) this.revokePreviewURL(doc);
    this.documentosExtra.splice(index, 1);
  }

  guardarImagenes(): void {
    if (!this.numeroSolicitud) {
      this.toastService.error('No se encontró el número de solicitud', 'Error');
      return;
    }

    const peticiones: Observable<any>[] = [];

    // Documentos fijos
    this.documentosFijos.forEach(doc => {
      if (doc.archivo) {
        const formData = new FormData();
        formData.append('tipo', doc.tipo);
        formData.append('archivo', doc.archivo);

        peticiones.push(
          this.http.post(`http://localhost:8080/api/subir/${this.numeroSolicitud}`, formData)
        );
      }
    });

    // Documentos extra
    this.documentosExtra.forEach(doc => {
      if (doc.archivo) {
        const formData = new FormData();
        formData.append('tipo', doc.tipo);
        formData.append('archivo', doc.archivo);

        peticiones.push(
          this.http.post(`http://localhost:8080/api/subir/${this.numeroSolicitud}`, formData)
        );
      }
    });

    if (peticiones.length === 0) {
      this.toastService.warning('No hay documentos para subir', 'Advertencia');
      return;
    }

    forkJoin(peticiones).subscribe({
      next: () => {
        this.toastService.success('Imágenes guardadas correctamente', 'Éxito');
        this.guardado.emit();
      },
      error: () => {
        this.toastService.error('Error al guardar imágenes', 'Error');
      }
    });
  }

  formatDocumento(value: string): string {
    if (!value) return '';
    let texto = value;

    // '_' -> ' DE '
    texto = texto.replaceAll('_', ' DE ');

    texto = texto.replaceAll(/([a-zA-Z])(\d+)/g, '$1 $2');

    texto = texto.replaceAll(/([a-z])([A-Z])/g, '$1 $2');

    return texto.trim().toUpperCase();
  }

  private readonly previewUrls = new WeakMap<{ tipo: string; archivo: File | null }, string>();

  private esImagenNombre(nombre: string): boolean {
    const lower = nombre.toLowerCase();
    return lower.endsWith('.jpg') || lower.endsWith('.jpeg') ||
           lower.endsWith('.png') || lower.endsWith('.gif');
  }

  getThumbnailLocal(doc: { tipo: string; archivo: File | null }): string {
    const nombre = doc.archivo?.name?.toLowerCase() || '';

    if (!nombre) return 'assets/icons/file.png';

    if (this.esImagenNombre(nombre) && doc.archivo) {
      const existente = this.previewUrls.get(doc);
      if (existente) return existente;

      const url = URL.createObjectURL(doc.archivo);
      this.previewUrls.set(doc, url);
      return url;
    }

    if (nombre.endsWith('.pdf')) return 'assets/icons/pdf.png';
    if (nombre.endsWith('.doc') || nombre.endsWith('.docx')) return 'assets/icons/word.png';
    if (nombre.endsWith('.xls') || nombre.endsWith('.xlsx')) return 'assets/icons/excel.png';
    return 'assets/icons/file.png';
  }

  private revokePreviewURL(doc: { tipo: string; archivo: File | null }): void {
    const existente = this.previewUrls.get(doc);
    if (existente) {
      URL.revokeObjectURL(existente);
      this.previewUrls.delete(doc);
    }
  }
}
