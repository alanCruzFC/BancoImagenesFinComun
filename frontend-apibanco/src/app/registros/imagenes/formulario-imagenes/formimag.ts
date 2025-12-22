import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { forkJoin, Observable } from 'rxjs';

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

  constructor(private readonly http: HttpClient) {}

  ngOnInit(): void {
    // Inicializamos los documentos fijos
    this.documentosFijos = this.tiposFijos.map(t => ({ tipo: t, archivo: null }));
  }

  onFileSelected(event: any, doc: { tipo: string; archivo: File | null }): void {
    const file = event.target.files[0];
    if (file) {
      doc.archivo = file;
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
      console.warn('No se seleccionó archivo extra');
      return;
    }
    doc.archivo = file;
  }

  getNombreArchivo(doc: { tipo: string; archivo: File | null }): string {
    return doc.archivo?.name || 'Ningún archivo seleccionado';
  }

  eliminarArchivo(doc: { tipo: string; archivo: File | null }): void {
    doc.archivo = null;
  }

  eliminarExtra(index: number): void {
    this.documentosExtra.splice(index, 1);
  }

  guardarImagenes(): void {
    if (!this.numeroSolicitud) {
      alert('⚠️ No se encontró el número de solicitud');
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
      alert('⚠️ No hay documentos para subir');
      return;
    }

    forkJoin(peticiones).subscribe({
      next: () => {
        alert('✅ Imágenes guardadas correctamente');
        this.guardado.emit();
      },
      error: (err) => {
        console.error('❌ Error al guardar imágenes', err);
        alert('❌ Error al guardar imágenes');
      }
    });
  }
}
