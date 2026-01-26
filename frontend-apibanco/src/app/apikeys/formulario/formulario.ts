import { Component, Output, EventEmitter, Input, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { ApiKey } from '../listado/listado'; 
import { ToastService } from '../../shared/toast/toast.service';

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

  constructor(private readonly http: HttpClient, private readonly toastService: ToastService) {}

  ngOnInit(): void {
    if (this.apiKey) {
      this.formData = { ...this.apiKey };
    }
  }

  hasError(field: string): boolean {
    return this.validationErrors.includes(field);
  }

  refactorizarClave(): void {
    if (!this.formData.id) {
      this.toastService.error('Solo puedes refactorizar una API Key existente', 'Error');
      return;
    }

    this.http.put<{ mensaje: string; nuevaClave: string }>(
      `http://localhost:8080/api/apikeys/${this.formData.id}/refactorizar`,
      {},
      { responseType: 'json' }
    ).subscribe({
      next: (res) => {
        this.toastService.success('Clave refactorizada correctamente', 'Éxito');
        this.formData.clave = res.nuevaClave;
        this.claveVisible = true;
      },
      error: (err: HttpErrorResponse) => {
        this.toastService.error('❌ Error al refactorizar la API Key', 'Error');
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
        this.toastService.success(isEdit ? 'API Key actualizada correctamente' : 'API Key creada correctamente', 'Éxito');
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
