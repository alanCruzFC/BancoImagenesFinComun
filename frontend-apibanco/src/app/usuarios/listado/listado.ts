import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../core/auth.service';
import { UsuarioService, Usuario } from '../../core/usuario.service';
import { FormularioUsuario } from "../formulario/formulario";

@Component({
  selector: 'app-usuarios-listado',
  standalone: true,
  imports: [CommonModule, FormularioUsuario, FormsModule],
  templateUrl: './listado.html'
})
export class ListadoComponent implements OnInit {
  usuarios: Usuario[] = [];
  modalVisible: boolean = false;
  modalVisible2: boolean = false;
  usuarioSeleccionado: Usuario | null = null;
  busqueda: string = '';
  rolActual: string = '';

  constructor(
    private readonly authService: AuthService,
    private readonly usuarioService: UsuarioService
  ) {
    this.rolActual = this.authService.getRol();
  }

  ngOnInit(): void {
    this.cargarUsuarios();
  }

  cargarUsuarios(): void {
    this.usuarioService.getUsuarios().subscribe({
      next: (data) => {
        let activos = data.filter(u => u.activo);

        if (this.rolActual === 'SUPERADMIN') {
          this.usuarios = activos;
        } else if (this.rolActual === 'ADMIN') {
          this.usuarios = activos.filter(u => u.rol !== 'SUPERADMIN');
        } else {
          this.usuarios = [];
        }
      },
      error: () => console.error('❌ Error al obtener usuarios')
    });
  }


  mostrarPassword(usuario: Usuario): void {
    if (this.rolActual === 'ADMIN' || this.rolActual === 'SUPERADMIN') {
      this.usuarioSeleccionado = usuario;
      this.modalVisible = true;
      setTimeout(() => this.cerrarModal(), 10000);
    } else {
      alert('⚠️ No tienes permisos para ver contraseñas');
    }
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
        .then(() => {
          alert('✅ Contraseña copiada al portapapeles')
          this.cerrarModal();
        })
        .catch(() => alert('❌ Error al copiar la contraseña'));
    }
  }

  borrarUsuario(id: number): void {
    this.usuarioService.borrarUsuario(id).subscribe({
      next: () => {
        alert('🗑️ Usuario desactivado correctamente');
        this.cargarUsuarios();
      },
      error: () => alert('❌ Error al desactivar usuario')
    });
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
      SUPERADMIN: 'bg-red-600 text-white',
      SUPERVISOR: 'bg-yellow-500 text-white',
      MANAGER: 'bg-blue-700 text-white',
      USER: 'bg-blue-400 text-white'
    };
    return colors[rol] || 'bg-gray-500 text-white';
  }

  puedeSubir(): boolean {
    return this.rolActual === 'SUPERADMIN';
  }
}
