# Frontend - Banco de Archivos

Aplicación web moderna construida con Angular para la gestión de archivos e imágenes con interfaz intuitiva y responsiva.

## Tecnologías

*   **Angular**: 20.3
*   **TypeScript**: 5.9.2
*   **TailwindCSS**: 4.1.18
*   **Flowbite**: 4.0.1 (Componentes UI)
*   **FontAwesome**: 7.1.0 (Iconos)
*   **RxJS**: 7.8 (Programación reactiva)
*   **Angular SSR**: Server-Side Rendering

## Estructura del Proyecto

```
frontend-apibanco/
├── src/
│   ├── app/
│   │   ├── app.component.ts           # Componente raíz
│   │   ├── app.routes.ts              # Configuración de rutas
│   │   ├── app.config.ts              # Configuración de la app
│   │   ├── auth/                      # Módulo de autenticación
│   │   │   ├── login.component.ts     # Página de login
│   │   │   └── login.css              # Estilos del login
│   │   ├── core/                      # Núcleo de la aplicación
│   │   │   ├── guards/
│   │   │   │   ├── auth.guard.ts      # Protección de rutas
│   │   │   │   └── role.guard.ts      # Protección por roles
│   │   │   ├── interceptors/
│   │   │   │   └── auth.interceptor.ts # Inyección de token JWT
│   │   │   ├── models/
│   │   │   │   ├── archivo.model.ts
│   │   │   │   ├── registro.model.ts
│   │   │   │   └── usuario.model.ts
│   │   │   └── services/
│   │   │       ├── api.service.ts     # Cliente HTTP base
│   │   │       ├── auth.service.ts    # Autenticación
│   │   │       ├── archivo.service.ts # Gestión de archivos
│   │   │       ├── registro.service.ts
│   │   │       └── usuario.service.ts
│   │   ├── dashboard/                 # Panel principal
│   │   │   ├── dashboard.component.ts
│   │   │   ├── navbar.component.ts    # Barra de navegación superior
│   │   │   └── sidebar.component.ts   # Menú lateral
│   │   ├── usuarios/                  # Gestión de usuarios
│   │   │   ├── lista-usuarios.component.ts
│   │   │   └── crear-usuario.component.ts
│   │   ├── registros/                 # Visualización de registros
│   │   │   ├── lista-registros.component.ts
│   │   │   └── detalle-registro.component.ts
│   │   ├── apikeys/                   # Gestión de API Keys
│   │   │   ├── lista-apikeys.component.ts
│   │   │   └── crear-apikey.component.ts
│   │   └── shared/                    # Componentes compartidos
│   │       ├── toast/                 # Sistema de notificaciones
│   │       │   ├── toast.service.ts
│   │       │   ├── toast-container.component.ts
│   │       │   └── toast-container.component.html
│   │       └── modal/                 # Modales reutilizables
│   ├── styles.css                     # Estilos globales
│   ├── index.html                     # HTML principal
│   └── main.ts                        # Punto de entrada
├── tailwind.config.js                 # Configuración de TailwindCSS
└── package.json                       # Dependencias npm
```

## Arquitectura y Capas

### 1. **Core** (Núcleo)
Contiene la lógica fundamental de la aplicación que se usa en toda la app:

#### **Services**
*   `AuthService`: Maneja login, logout, validación de tokens y estado de autenticación
*   `ApiService`: Cliente HTTP base con configuración de headers y manejo de errores
*   `ArchivoService`: CRUD de archivos (subir, descargar, listar, eliminar)
*   `UsuarioService`: Gestión de usuarios
*   `RegistroService`: Consulta de logs de actividad

#### **Guards**
*   `AuthGuard`: Protege rutas que requieren autenticación
*   `RoleGuard`: Protege rutas según roles de usuario (`data: { roles: ['ADMIN', 'SUPERADMIN'] }`)

#### **Interceptors**
*   `AuthInterceptor`: Inyecta automáticamente el token JWT en headers de cada request

#### **Models**
Interfaces TypeScript que definen la estructura de datos:
*   `Usuario`, `Archivo`, `Registro`, `ApiKey`, etc.

### 2. **Feature Modules** (Módulos de Funcionalidad)

#### **Auth**
*   Página de login con validación de formularios
*   Redirección automática al dashboard tras autenticación exitosa

#### **Dashboard**
*   Layout principal con navbar y sidebar
*   Sidebar responsivo con menú hamburguesa en móviles
*   Navegación dinámica según roles del usuario

#### **Usuarios**
*   Listado de usuarios con filtros y paginación
*   Formulario para crear/editar usuarios
*   Asignación de supervisores y roles

#### **Registros**
*   Tabla de registros de actividad
*   Filtrado por fecha, usuario, tipo de operación
*   Visualización de detalles de cada registro

#### **ApiKeys**
*   Generación de nuevas claves API
*   Listado y revocación de claves existentes
*   Visualización segura de claves (solo al crearlas)

### 3. **Shared** (Componentes Compartidos)

#### **Toast Service**
Sistema de notificaciones tipo "toast" con:
*   4 tipos: `success`, `info`, `warning`, `error`
*   Animaciones suaves de entrada/salida
*   Auto-cierre configurable
*   Stack múltiple (varias notificaciones simultáneas)

#### **Modals**
Componentes modales reutilizables para confirmaciones y formularios.

## Rutas Principales

```typescript
/login              → LoginComponent (pública)
/dashboard          → DashboardComponent (requiere auth)
  ├── /usuarios     → ListaUsuariosComponent (roles: ADMIN, SUPERADMIN)
  ├── /registros    → ListaRegistrosComponent (requiere auth)
  └── /apikeys      → ListaApikeysComponent (roles: ADMIN, SUPERADMIN)
```

## Estilos

### TailwindCSS
Utiliza Tailwind v4 con configuración personalizada en `tailwind.config.js`:
*   Tema personalizado con paleta de colores
*   Clases de utilidad para layouts responsivos
*   Componentes de Flowbite integrados

### Archivo Global `styles.css`
Define:
*   Fuente: **Inter** (Google Fonts)
*   Animaciones personalizadas (toasts, modales)
*   Clases de utilidad custom (`@layer utilities`)
*   Clases de componentes custom (`@layer components`)

```css
@layer utilities {
  @keyframes toastSlideDown {
    from { transform: translateY(-200px) scale(0.80); opacity: 0; }
    to   { transform: translateY(0) scale(1); opacity: 1; }
  }
  
  .toast-enter {
    animation: toastSlideDown 1000ms cubic-bezier(0.16, 1, 0.3, 1);
  }
}
```

## Configuración del Entorno

### Configurar la URL del Backend

Edita `src/app/core/services/api.service.ts`:

```typescript
private readonly API_URL = 'http://localhost:8080/api';
```

### Variables de Entorno (Opcional)

Para configuraciones avanzadas, puedes usar archivos `environment.ts`:

```typescript
export const environment = {
  production: false,
  apiUrl: 'http://localhost:8080/api'
};
```

## Ejecución

### Desarrollo
```bash
npm start
# o
ng serve
```
La app estará disponible en `http://localhost:4200/`

### Build de Producción
```bash
npm run build
```
Los archivos se generarán en `dist/frontend-apibanco/browser/`

### Server-Side Rendering (SSR)
```bash
npm run serve:ssr:frontend-apibanco
```

### Tests
```bash
npm test
```

## Características Clave

### Autenticación Persistente
*   Token JWT almacenado en `localStorage`
*   Auto-login si el token es válido
*   Logout automático al expirar el token

### Diseño Responsivo
*   Sidebar colapsable en móviles
*   Tablas con scroll horizontal en pantallas pequeñas
*   Navbar adaptativa

### Sistema de Notificaciones
*   Toasts elegantes con animaciones suaves
*   Feedback visual para operaciones exitosas/fallidas
*   Auto-cierre con temporizador

### Guards y Roles
*   Rutas protegidas según autenticación
*   Restricción de acceso por roles (`USER`, `SUPERVISOR`, `ADMIN`, `SUPERADMIN`)
*   Redirección automática si no hay permisos

## Flujo de Autenticación

1.  Usuario ingresa credenciales en `/login`
2.  `AuthService.login()` envía request a `/api/login`
3.  Backend responde con token JWT
4.  Token se guarda en `localStorage` y se establece observable `currentUser$`
5.  `AuthInterceptor` inyecta token en headers de futuras requests
6.  `AuthGuard` permite navegación a rutas protegidas
7.  Al cerrar sesión, se limpia `localStorage` y se redirige a `/login`

## Mejores Prácticas Implementadas

*   **Lazy Loading**: Módulos cargados bajo demanda
*   **OnPush Change Detection**: Mejora de rendimiento
*   **Reactive Forms**: Validación robusta de formularios
*   **RxJS Operators**: Uso de `pipe()`, `map()`, `filter()`, `switchMap()`, etc.
*   **TypeScript Strict Mode**: Tipado estricto
*   **Standalone Components**: Arquitectura moderna de Angular 20

## Notas de Desarrollo

*   Los servicios están provistos en `root` para singleton global
*   Se usa `HttpClient` para comunicación con el backend
*   Las tablas usan `trackBy` para optimización de renderizado
*   Los formularios reactivos incluyen validaciones síncronas y asíncronas
*   El sistema de toasts es global y accesible desde cualquier componente
