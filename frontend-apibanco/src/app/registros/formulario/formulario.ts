import { Component, EventEmitter, Output } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { debounceTime, switchMap } from 'rxjs/operators';
import { RegistroDTO, RegistroService } from '../../core/registro.service';
import { UsuarioService } from '../../core/usuario.service';

export interface RegistroRequest {
  numeroSolicitud: string;
  correosAutorizados?: string[];
}

@Component({
  selector: 'app-formulario-registro',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './formulario.html'
})
export class FormularioRegistro {
  @Output() registroCreado = new EventEmitter<RegistroDTO>();
  @Output() guardado = new EventEmitter<void>();

  form: FormGroup;
  correosSeleccionados: string[] = [];
  sugerencias: string[] = [];

  constructor(
    private readonly fb: FormBuilder,
    private readonly registroService: RegistroService,
    private readonly usuarioService: UsuarioService
  ) {
    this.form = this.fb.group({
      // ✅ Validación: requerido y no solo espacios
      numeroSolicitud: ['', [Validators.required, Validators.pattern(/\S+/)]],
      correoInput: ['']
    });

    this.form.get('correoInput')!.valueChanges
      .pipe(
        debounceTime(300),
        switchMap(valor => this.usuarioService.obtenerCorreos(valor))
      )
      .subscribe(correos => this.sugerencias = correos);
  }

  agregarCorreo(correo: string): void {
    if (correo && !this.correosSeleccionados.includes(correo)) {
      this.correosSeleccionados.push(correo);
    }
    this.form.patchValue({ correoInput: '' });
    this.sugerencias = [];
  }

  eliminarCorreo(correo: string): void {
    this.correosSeleccionados = this.correosSeleccionados.filter(c => c !== correo);
  }

  guardar(): void {
    if (this.form.invalid) {
      // ✅ Ya no usamos alert, el template mostrará los mensajes debajo del input
      this.form.markAllAsTouched();
      return;
    }

    const dto: RegistroRequest = {
      numeroSolicitud: this.form.value.numeroSolicitud!,
      correosAutorizados: this.correosSeleccionados
    };

    this.registroService.crearRegistro(dto).subscribe({
      next: (nuevo: RegistroDTO) => {
        alert('✅ Registro creado correctamente');
        this.registroCreado.emit(nuevo);
        this.guardado.emit();
      },
      error: (err) => {
        console.error('❌ Error al crear registro', err);
        alert('❌ Error al crear registro');
      }
    });
  }
}
