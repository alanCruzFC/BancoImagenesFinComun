import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../core/auth.service';
import { UsuarioService, Usuario } from '../../core/usuario.service';
import { FormularioUsuario } from "../formulario/formulario";
import { ToastService } from '../../shared/toast/toast.service';

@Component({
  selector: 'app-usuarios-listado',
  standalone: true,
  imports: [CommonModule, FormularioUsuario, FormsModule],
  templateUrl: './listado.html'
})
export class ListadoComponent implements OnInit {
  usuarios: Usuario[] = [];

  modalVisible2: boolean = false;
  usuarioSeleccionado: Usuario | null = null;
  busqueda: string = '';
  rolActual: string = '';

  showConfirmDeleteUser = false;
  idUsuarioPendiente: number | null = null;

  constructor(
    private readonly authService: AuthService,
    private readonly usuarioService: UsuarioService,
    private readonly toastService: ToastService
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
      error: () => this.toastService.error('Error al cargar usuarios')
    });
  }


  editarUsuario(usuario: Usuario): void {
    this.usuarioSeleccionado = usuario;
    this.abrirModal2();
  }

  abrirModal2(): void {
    this.modalVisible2 = true;
  }

  cerrarModal(): void {
    this.usuarioSeleccionado = null;
  }

  cerrarModal2(): void {
    this.modalVisible2 = false;
    this.usuarioSeleccionado = null;
  }


  borrarUsuario(id: number): void {
    this.usuarioService.borrarUsuario(id).subscribe({
      next: () => {
        this.toastService.success('Usuario desactivado correctamente');
        this.cargarUsuarios();
      },
      error: () => this.toastService.error('Error al desactivar usuario')
    });
  }

  columnaOrdenada: string = '';
  ordenAscendente: boolean = true;

  get usuariosFiltrados(): Usuario[] {
    const term = this.busqueda.toLowerCase();
    let filtrados = this.usuarios.filter(usuario =>
      usuario.username?.toLowerCase().includes(term) ||
      usuario.firstName?.toLowerCase().includes(term) ||
      usuario.lastName?.toLowerCase().includes(term) ||
      usuario.email?.toLowerCase().includes(term) ||
      usuario.department?.toLowerCase().includes(term)
    );

    if (this.columnaOrdenada) {
      filtrados.sort((a, b) => {
        const valorA = a[this.columnaOrdenada as keyof Usuario] ?? '';
        const valorB = b[this.columnaOrdenada as keyof Usuario] ?? '';

        if (valorA < valorB) {
          return this.ordenAscendente ? -1 : 1;
        }
        if (valorA > valorB) {
          return this.ordenAscendente ? 1 : -1;
        }
        return 0;
      });
    }

    return filtrados;
  }

  ordenar(columna: string): void {
    if (this.columnaOrdenada === columna) {
      this.ordenAscendente = !this.ordenAscendente;
    } else {
      this.columnaOrdenada = columna;
      this.ordenAscendente = true;
    }
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

  pedirConfirmacionEliminarUsuario(id: number): void {
    this.idUsuarioPendiente = id;
    this.showConfirmDeleteUser = true;
  }

  cancelarEliminarUsuario(): void {
    this.showConfirmDeleteUser = false;
    this.idUsuarioPendiente = null;
  }

  confirmarEliminarUsuario(): void {
    if (this.idUsuarioPendiente != null) {
      this.borrarUsuario(this.idUsuarioPendiente);
    }
    this.showConfirmDeleteUser = false;
    this.idUsuarioPendiente = null;
  }

}

