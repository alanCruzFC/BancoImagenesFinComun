import { Component, Output, EventEmitter, Input, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, NgForm } from '@angular/forms';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';

interface SupervisorDTO {
  id: number;
  firstName: string;
  lastName: string;
  email: string;
}

interface UsuarioForm {
  id?: number;
  username: string;
  firstName: string;
  lastName: string;
  email: string;
  rol: string;
  activo: boolean;
  team?: string;
  department?: string;
  supervisorId?: number | null;
  password?: string;
  passwordDesencriptada?: string;
}

@Component({
  selector: 'app-formulario-usuario',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './formulario.html'
})
export class FormularioUsuario implements OnInit {
  @Input() usuario: UsuarioForm | null = null;
  @Output() creado = new EventEmitter<void>();

  formData: UsuarioForm = {
    username: '',
    firstName: '',
    lastName: '',
    email: '',
    rol: '',
    activo: true,
    team: '',
    department: '',
    supervisorId: null,
    password: ''
  };

  validationErrors: string[] = [];

  // ✅ Incluimos SUPERADMIN
  roles: string[] = ['SUPERADMIN', 'ADMIN', 'SUPERVISOR', 'USER'];
  teams: string[] = [];
  departments: string[] = [];
  supervisores: SupervisorDTO[] = [];

  usarInputTeam = false;
  usarInputDepartment = false;

  constructor(private readonly http: HttpClient) {}

  ngOnInit(): void {
    if (this.usuario) {
      this.formData = {
        id: this.usuario.id,
        username: this.usuario.username || '',
        firstName: this.usuario.firstName || '',
        lastName: this.usuario.lastName || '',
        email: this.usuario.email || '',
        rol: this.usuario.rol || '',
        activo: this.usuario.activo ?? true,
        team: this.usuario.team || '',
        department: this.usuario.department || '',
        supervisorId: this.usuario.supervisorId ?? null,
        password: ''
      };
    }

    this.http.get<any[]>('http://localhost:8080/api/usuarios').subscribe({
      next: (usuarios) => {
        this.teams = [...new Set(usuarios.map(u => u.team).filter(t => !!t))];
        this.departments = [...new Set(usuarios.map(u => u.department).filter(d => !!d))];
        this.supervisores = usuarios.filter(u => u.rol === 'SUPERVISOR');
      },
      error: (err) => console.error('❌ Error al obtener usuarios', err)
    });
  }

  // Generador de contraseña con reglas concretas:
  generarPassword(): void {
    const length = 16;
    const lowers = 'abcdefghijkmnopqrstuvwxyz'; 
    const uppers = 'ABCDEFGHJKLMNPQRSTUVWXYZ';  
    const digits = '23456789';                 
    const specials = '!@#$%^&*()-_=+[]{}?';
    const all = lowers + uppers + digits + specials;

    const pick = (set: string) => {
      const array = new Uint32Array(1);
      crypto.getRandomValues(array);
      return set.charAt(array[0] % set.length);
    };

    const obligatorios = "FinComun".split("");

    let pwd = [
      pick(lowers),
      pick(uppers),
      pick(digits),
      pick(specials)
    ];
    const longitudExtras = length - (pwd.length + obligatorios.length);
    for (let i = 0; i < longitudExtras; i++) {
      pwd.push(pick(all));
    }

    pwd = pwd.concat(obligatorios);
    for (let i = pwd.length - 1; i > 0; i--) {
      const array = new Uint32Array(1);
      crypto.getRandomValues(array);
      const j = array[0] % (i + 1);
      [pwd[i], pwd[j]] = [pwd[j], pwd[i]];
    }
    this.formData.password = pwd.join('');
  }

  hasError(field: string): boolean {
    return this.validationErrors.includes(field);
  }

  cambiarAInputTeam(): void {
    this.usarInputTeam = true;
    this.formData.team = '';
  }

  cambiarAInputDepartment(): void {
    this.usarInputDepartment = true;
    this.formData.department = '';
  }

  submit(form: NgForm): void {
    this.validationErrors = [];

    const validations = [
      { condition: !this.formData.email, message: 'Email' },
      { condition: !this.formData.firstName, message: 'Nombre' },
      { condition: !this.formData.lastName, message: 'Apellido' },
      { condition: !this.formData.username, message: 'Usuario' },
      { condition: !this.formData.rol, message: 'Rol' },
      { condition: this.formData.rol === 'USER' && !this.formData.supervisorId, message: 'Supervisor' },
      { condition: this.formData.supervisorId && this.formData.id && this.formData.supervisorId === this.formData.id,
        message: 'Supervisor invalido (no puede ser él mismo)' },
      { condition: !this.formData.team || this.formData.team.trim() === '', message: 'Equipo' },
      { condition: !this.formData.department || this.formData.department.trim() === '', message: 'Departamento' },
      { condition: !this.usuario?.passwordDesencriptada && !this.formData.password, message: 'Contraseña' }
    ];

    validations.forEach(v => {
      if (v.condition) this.validationErrors.push(v.message);
    });

    if (this.validationErrors.length > 0) return;

    const payload = { ...this.formData };
    const isEdit = !!this.formData.id;
    const url = isEdit
      ? `http://localhost:8080/api/usuarios/${this.formData.id}`
      : 'http://localhost:8080/api/usuarios';
    const method = isEdit ? 'put' : 'post';

    this.http.request(method, url, { body: payload, responseType: 'text' }).subscribe({
      next: (response: string) => {
        alert(response || '✅ Operación completada con éxito');
        this.creado.emit();
      },
      error: (error: HttpErrorResponse) => {
        let errorMessage = '❌ Error de comunicación con el servidor.';
        if (error.error && (error.error.reason || error.error.message)) {
          errorMessage = `❌ Error (${error.status}): ${error.error.reason || error.error.message}`;
        } else if (error.status === 403) {
          errorMessage = '❌ Acceso denegado. No tienes permisos para esta acción.';
        } else if (error.statusText) {
          errorMessage = `❌ Error (${error.status}): ${error.statusText}`;
        }
        alert(errorMessage);
        console.error(error);
      }
    });
  }
}
