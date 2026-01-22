# Banco de Imágenes FinComún

Este proyecto es una aplicación completa para la gestión de archivos e imágenes, compuesta por un backend robusto en Spring Boot y un frontend moderno en Angular.

## Tecnologías Utilizadas

### Backend (`apibanco`)
*   **Java**: 17
*   **Framework**: Spring Boot 3.5.9
*   **Base de Datos**: MySQL (Producción), H2 (Comprobación/Tests)
*   **Seguridad**: Spring Security + JWT (JSON Web Tokens)
*   **Almacenamiento**: AWS S3 SDK (para gestión de archivos en la nube)
*   **Utilidades**: Lombok, MapStruct, Apache Commons IO

### Frontend (`frontend-apibanco`)
*   **Framework**: Angular 20
*   **Estilos**: TailwindCSS 4 + Flowbite 4
*   **Iconos**: FontAwesome

## Prerrequisitos

Asegúrate de tener instalado lo siguiente en tu entorno local:
*   [Java JDK 17](https://www.oracle.com/java/technologies/downloads/#java17)
*   [Node.js](https://nodejs.org/) (versión LTS recomendada)
*   [Maven](https://maven.apache.org/)
*   [MySQL Server](https://dev.mysql.com/downloads/mysql/)

## Instalación y Configuración

### 1. Backend (API)

1.  Navega al directorio del backend:
    ```bash
    cd apibanco
    ```
2.  Configura tus variables de entorno o propiedades en `src/main/resources/application.properties` (asegúrate de configurar la conexión a MySQL y credenciales de AWS S3 si aplica).
3.  Compila y ejecuta el proyecto:
    ```bash
    mvn spring-boot:run
    ```

### 2. Frontend (Cliente Web)

1.  Navega al directorio del frontend:
    ```bash
    cd frontend-apibanco
    ```
2.  Instala las dependencias:
    ```bash
    npm install
    ```
3.  Inicia el servidor de desarrollo:
    ```bash
    npm start
    ```
4.  Abre tu navegador en `http://localhost:4200/`.

## Características Principales

*   **Autenticación Segura**: Login y gestión de usuarios mediante tokens JWT.
*   **Gestión de Archivos**: Subida, descarga y administración de imágenes/archivos (integración con AWS S3).

## Contribución

1.  Haz un Fork del proyecto.
2.  Crea tu rama de funcionalidad (`git checkout -b feature/NuevaFuncionalidad`).
3.  Haz Commit de tus cambios (`git commit -m 'Agrega nueva funcionalidad'`).
4.  Haz Push a la rama (`git push origin feature/NuevaFuncionalidad`).
5.  Abre un Pull Request.

---
© 2026 FinComún. Todos los derechos reservados.
