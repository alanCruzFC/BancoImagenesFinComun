# Backend - API Banco de Archivos

Backend REST API construido con Spring Boot para la gestión segura de archivos e imágenes.

## Tecnologías

*   **Java**: 17
*   **Spring Boot**: 3.5.9
*   **Spring Security**: Autenticación basada en JWT
*   **Spring Data JPA**: Acceso a datos con Hibernate
*   **MySQL**: Base de datos en producción
*   **H2**: Base de datos en memoria para tests
*   **AWS S3**: Almacenamiento de archivos en la nube
*   **Maven**: Gestión de dependencias

## Estructura del Proyecto

```
apibanco/
├── src/main/java/com/fc/apibanco/
│   ├── ApibancoApplication.java       # Punto de entrada de la aplicación
│   ├── config/                        # Configuraciones de Spring
│   │   ├── SecurityConfig.java        # Configuración de seguridad
│   │   └── WebConfig.java             # Configuración CORS y web
│   ├── controller/                    # Controladores REST
│   │   ├── ApiKeyController.java      # CRUD de API Keys
│   │   ├── ArchivoController.java     # Gestión de archivos/imágenes
│   │   ├── AuthController.java        # Login y autenticación
│   │   ├── RegistroController.java    # Gestión de registros
│   │   └── UsuarioController.java     # CRUD de usuarios
│   ├── dto/                           # Data Transfer Objects
│   │   ├── ApiKeyRequest.java
│   │   ├── LoginRequest.java
│   │   ├── RegistroDTO.java
│   │   ├── UsuarioDTO.java
│   │   └── UsuarioRequest.java
│   ├── exception/                     # Manejo de excepciones
│   │   └── GlobalExceptionHandler.java
│   ├── model/                         # Entidades JPA
│   │   ├── ApiKey.java                # Claves API para autenticación externa
│   │   ├── Archivo.java               # Archivos subidos al sistema
│   │   ├── Metadata.java              # Metadatos de archivos
│   │   ├── PasswordEncriptada.java    # Contraseñas encriptadas AES
│   │   ├── Registro.java              # Registros de actividad
│   │   └── Usuario.java               # Usuarios del sistema
│   ├── repository/                    # Repositorios JPA
│   │   ├── ApiKeyRepository.java
│   │   ├── ArchivoRepository.java
│   │   ├── MetadataRepository.java
│   │   ├── RegistroRepository.java
│   │   └── UsuarioRepository.java
│   ├── security/                      # Componentes de seguridad
│   │   ├── JwtUtil.java               # Utilidad para generar/validar JWT
│   │   ├── JwtRequestFilter.java      # Filtro para interceptar requests
│   │   └── CustomUserDetailsService.java
│   ├── service/                       # Lógica de negocio
│   │   ├── ArchivoService.java
│   │   ├── FileStorageService.java    # Abstracción para almacenamiento
│   │   ├── LocalFileStorageService.java # Almacenamiento local
│   │   ├── S3FileStorageService.java  # Almacenamiento en AWS S3
│   │   ├── RegistroService.java
│   │   └── UsuarioService.java
│   └── util/                          # Utilidades
│       ├── AESUtil.java               # Encriptación AES
│       └── Constantes.java            # Constantes globales
└── src/main/resources/
    └── application.properties         # Configuración de la aplicación
```

## Arquitectura y Capas

### 1. **Controllers** (Capa de Presentación)
Exponen endpoints REST para la comunicación con el frontend:
*   `AuthController`: Autenticación (`/api/login`, `/api/validate`, `/api/apikey`)
*   `UsuarioController`: Gestión de usuarios (`/api/usuarios`, `/api/supervisores`, etc.)
*   `ArchivoController`: Subida/descarga de archivos (`/api/archivos`)
*   `RegistroController`: Consulta de registros de actividad (`/api/registros`)
*   `ApiKeyController`: Administración de claves API (`/api/apikeys`)

### 2. **Services** (Capa de Lógica de Negocio)
Contienen la lógica del negocio y operaciones complejas:
*   `FileStorageService`: Interfaz para almacenamiento (local o S3)
*   `UsuarioService`: Lógica de usuarios (activación, validaciones)
*   `RegistroService`: Generación de logs de auditoría

### 3. **Repositories** (Capa de Acceso a Datos)
Interfaces que extienden `JpaRepository` para operaciones CRUD con la base de datos.

### 4. **Security**
*   **JWT**: Tokens para autenticación stateless
*   **Roles**: `USER`, `SUPERVISOR`, `ADMIN`, `SUPERADMIN`
*   **ApiKey**: Sistema adicional de autenticación por clave

### 5. **Models** (Entidades)
Clases JPA que mapean las tablas de la base de datos con relaciones:
*   `Usuario` → `Supervisor` (auto-referencia)
*   `Archivo` → `Metadata` (1:1)
*   `Registro` → `Archivo` (N:1)

## Configuración

### Variables de Entorno

Crea un archivo `.env` o configura las siguientes variables en `application.properties`:

```properties
# Base de datos
spring.datasource.url=jdbc:mysql://localhost:3306/banco_archivos
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}

# JWT Secret
jwt.secret=${JWT_SECRET}

# AWS S3 (si se usa almacenamiento en la nube)
file.storage.type=s3
aws.s3.bucket=${AWS_S3_BUCKET}
aws.s3.region=${AWS_REGION}
aws.access.key.id=${AWS_ACCESS_KEY_ID}
aws.secret.access.key=${AWS_SECRET_ACCESS_KEY}

# Almacenamiento local (alternativa a S3)
# file.storage.type=local
# file.upload.dir=/path/to/uploads
```

### Base de Datos

1.  Crea la base de datos:
    ```sql
    CREATE DATABASE banco_archivos CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
    ```
2.  Spring Boot creará las tablas automáticamente gracias a `spring.jpa.hibernate.ddl-auto=update`.

## Ejecución

### Desarrollo
```bash
mvn spring-boot:run
```

### Construcción
```bash
mvn clean package
java -jar target/apibanco-0.0.1-SNAPSHOT.jar
```

### Tests
```bash
mvn test
```

## Endpoints Principales

### Autenticación
*   `POST /api/login` - Login con username/password
*   `POST /api/validate` - Validar token JWT
*   `POST /api/apikey` - Login con API Key

### Usuarios
*   `GET /api/usuarios/sin-password` - Listar usuarios sin password configurado
*   `POST /api/usuarios` - Crear usuario (requiere SUPERADMIN)
*   `PUT /api/usuarios/{id}` - Actualizar usuario
*   `DELETE /api/usuarios/{id}` - Borrado lógico (desactivar)

### Archivos
*   `POST /api/archivos/subir` - Subir archivo
*   `GET /api/archivos/descargar/{id}` - Descargar archivo
*   `GET /api/archivos` - Listar archivos (con filtros)

### Registros
*   `GET /api/registros` - Listar registros de actividad

## Seguridad

*   **BCrypt**: Para hash de contraseñas
*   **AES**: Para encriptación reversible de contraseñas (almacenamiento legacy)
*   **JWT**: Tokens con expiración de 10 horas
*   **CORS**: Configurado para permitir origen `http://localhost:4200`

## Notas Importantes

*   Los archivos se pueden almacenar localmente o en AWS S3 según `file.storage.type`
*   El sistema mantiene un registro de auditoría de todas las operaciones con archivos
*   Las contraseñas se hashean con BCrypt y también se guardan encriptadas con AES en tabla separada
*   Los supervisores pueden gestionar a sus subordinados (jerarquía de usuarios)
