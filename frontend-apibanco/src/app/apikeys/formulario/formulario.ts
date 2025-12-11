import { Component, Output, EventEmitter, Input } from '@angular/core';
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
export class FormularioApiKey {
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

  claveVisible: boolean = false;

  constructor(private readonly http: HttpClient) {}

  ngOnInit(): void {
    if (this.apiKey) {
      this.formData = { ...this.apiKey };
    }
  }

  toggleClave(): void {
    this.claveVisible = !this.claveVisible;
  }

  refactorizarClave(): void {
    if (!this.formData.id) {
      alert('❌ Solo puedes refactorizar una API Key existente');
      return;
    }

    this.http.put<{mensaje: string, nuevaClave: string}>(
      `http://localhost:8080/api/apikeys/${this.formData.id}/refactorizar`, 
      {}, 
      { responseType: 'json' }
    ).subscribe({
      next: (res) => {
        alert(res.mensaje);
        this.formData.clave = res.nuevaClave;
        this.claveVisible = true;
      },
      error: (err: HttpErrorResponse) => {
        alert('❌ Error al refactorizar la API Key');
        console.error(err);
      }
    });
  }

  submit(): void {
    const payload = { ...this.formData };

    const isEdit = !!this.formData.id;
    const url = isEdit
      ? `http://localhost:8080/api/apikeys/${this.formData.id}`
      : 'http://localhost:8080/api/apikeys';
    const method = isEdit ? 'put' : 'post';

    this.http.request(method, url, { body: payload, responseType: 'text' }).subscribe({
      next: (res: string) => {
        alert((isEdit ? 'API Key actualizada correctamente' : 'API Key creada correctamente'));
        this.creado.emit();
      },
      error: (err: HttpErrorResponse) => {
        let errorMessage = isEdit ? 'Error al actualizar API Key.' : 'Error al crear API Key.';
        if (err.status === 403) {
          errorMessage = 'Acceso denegado. No tienes permisos para esta acción.';
        }
        alert(errorMessage);
        console.error(err);
      }
    });
  }

}
