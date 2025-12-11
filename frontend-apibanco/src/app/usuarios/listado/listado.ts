import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { FormularioUsuario } from "../formulario/formulario";
import { FormsModule } from '@angular/forms';

interface Usuario {
  id: number;
  username: string;
  firstName: string;
  lastName: string;
  email: string;
  rol: string;
  activo: boolean;
  team: string;
  department: string;
  supervisorId?: number;
  supervisorName?: string;
  passwordDesencriptada?: string; 
}

@Component({
  selector: 'app-usuarios-listado',
  standalone: true,
  imports: [CommonModule, FormularioUsuario, FormsModule],
  templateUrl: './listado.html',
})
export class ListadoComponent {
  usuarios: Usuario[] = [];
  modalVisible: boolean = false;   // modal para ver contraseña
  modalVisible2: boolean = false;  // modal para crear/editar usuario
  usuarioSeleccionado: Usuario | null = null;
  busqueda: string = '';

  constructor(private readonly http: HttpClient) {}

  OnInit(): void {
    this.http.get<Usuario[]>('http://localhost:8080/api/usuarios').subscribe({
      next: (data) => this.usuarios = data,
      error: () => console.error('❌ Error al obtener usuarios')
    });
  }
  
  mostrarPassword(usuario: Usuario): void {
    this.usuarioSeleccionado = usuario;
    this.modalVisible = true;
    setTimeout(() => this.cerrarModal(), 10000);
  }

  editarUsuario(usuario: Usuario): void {
    this.usuarioSeleccionado = usuario;
    this.abrirModal2();
  }

  abrirModal2(): void {
    this.modalVisible2 = true;
  }

  cerrarModal(): void {
    this.modalVisible = false;
    this.usuarioSeleccionado = null;
  }

  cerrarModal2(): void {
    this.modalVisible2 = false;
    this.usuarioSeleccionado = null;
  }

  copiarPassword(): void {
    if (this.usuarioSeleccionado?.passwordDesencriptada) {
      navigator.clipboard.writeText(this.usuarioSeleccionado.passwordDesencriptada)
        .then(() => alert('✅ Contraseña copiada al portapapeles'))
        .catch(() => alert('❌ Error al copiar la contraseña'));
    }
  }

  get usuariosFiltrados(): Usuario[] {
    const term = this.busqueda.toLowerCase();
    return this.usuarios.filter(usuario =>
      usuario.username?.toLowerCase().includes(term) ||
      usuario.firstName?.toLowerCase().includes(term) ||
      usuario.lastName?.toLowerCase().includes(term) ||
      usuario.email?.toLowerCase().includes(term) ||
      usuario.department?.toLowerCase().includes(term)
    );
  }

  getRolColor(rol: string): string {
    const colors: any = {
      ADMIN: 'bg-pink-500 text-white',
      SUPERVISOR: 'bg-yellow-500 text-white',
      MANAGER: 'bg-blue-700 text-white',
      USER: 'bg-blue-400 text-white'
    };
    return colors[rol] || 'bg-gray-500 text-white';
  }
}
