import { Component, Output, EventEmitter, Input, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { ApiKey } from '../listado/listado'; 

@Component({
  selector: 'app-formulario-apikey',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './formulario.html',
})
export class FormularioApiKey implements OnInit {
    validationErrors: string[] = [];

  @Output() creado = new EventEmitter<void>();
  @Input() apiKey: ApiKey | null = null; 

  formData: ApiKey = {
    id: 0,
    consumidor: '',
    clave: '',
    activo: true,
    lectura: false,
    escritura: false,
    actualizacion: false,
    eliminacion: false,
    fechaCreacion: new Date()
  };

  claveVisible = false;

  constructor(private readonly http: HttpClient) {}

  ngOnInit(): void {
    if (this.apiKey) {
      this.formData = { ...this.apiKey };
    }
  }

  toggleClave(): void {
    this.claveVisible = !this.claveVisible;
  }

  hasError(field: string): boolean {
    return this.validationErrors.includes(field);
  }

  refactorizarClave(): void {
    if (!this.formData.id) {
      alert('❌ Solo puedes refactorizar una API Key existente');
      return;
    }

    this.http.put<{ mensaje: string; nuevaClave: string }>(
      `http://localhost:8080/api/apikeys/${this.formData.id}/refactorizar`,
      {},
      { responseType: 'json' }
    ).subscribe({
      next: (res) => {
        alert(res.mensaje || '✅ Clave refactorizada correctamente');
        this.formData.clave = res.nuevaClave;
        this.claveVisible = true;
      },
      error: (err: HttpErrorResponse) => {
        alert('❌ Error al refactorizar la API Key');
        console.error('Error refactorizando API Key:', err);
      }
    });
  }

  submit(): void {
    this.validationErrors = [];

    // ✅ Validación de consumidor: no vacío y no solo espacios
    if (!this.formData.consumidor || this.formData.consumidor.trim() === '') {
      this.validationErrors.push('Consumidor');
    }

    // Si hay errores, no continuar
    if (this.validationErrors.length > 0) {
      return;
    }

    const payload = { ...this.formData };
    const isEdit = !!this.formData.id;
    const url = isEdit
      ? `http://localhost:8080/api/apikeys/${this.formData.id}`
      : 'http://localhost:8080/api/apikeys';
    const method = isEdit ? 'put' : 'post';

    this.http.request(method, url, { body: payload, responseType: 'text' }).subscribe({
      next: () => {
        alert(isEdit ? '✅ API Key actualizada correctamente' : '✅ API Key creada correctamente');
        this.creado.emit();
      },
      error: (err: HttpErrorResponse) => {
        let errorMessage = isEdit ? '❌ Error al actualizar API Key.' : '❌ Error al crear API Key.';
        if (err.status === 403) {
          errorMessage = '❌ Acceso denegado. No tienes permisos para esta acción.';
        } else if (err.statusText) {
          errorMessage = `❌ Error (${err.status}): ${err.statusText}`;
        }
        alert(errorMessage);
        console.error('Error en submit API Key:', err);
      }
    });
  }

}
