# Guía de Flyway - Migraciones de Base de Datos

## ¿Qué es Flyway?

Flyway es una herramienta de migración de bases de datos que gestiona y versiona los cambios del esquema de tu base de datos de forma controlada y auditable.

## Estructura de Archivos

```
src/main/resources/db/migration/
├── V1__initial_schema.sql          # Esquema inicial
├── V2__add_new_column.sql          # Ejemplo de migración futura
└── V3__add_new_table.sql           # Ejemplo de migración futura
```

## Convenciones de Nomenclatura

Los archivos de migración deben seguir este formato:

```
V{versión}__{descripción}.sql
```

- **V**: Prefijo obligatorio (mayúscula)
- **{versión}**: Número de versión (ej: 1, 2, 3, 1.1, 2.5)
- **__**: Dos guiones bajos
- **{descripción}**: Descripción en snake_case (o palabras separadas por guiones bajos)
- **.sql**: Extensión del archivo

### Ejemplos Válidos
- ✅ `V1__initial_schema.sql`
- ✅ `V2__add_user_profile_table.sql`
- ✅ `V3__add_email_verification_column.sql`
- ✅ `V1.1__hotfix_user_constraints.sql`

### Ejemplos Inválidos
- ❌ `v1_initial.sql` (v minúscula)
- ❌ `V1_initial.sql` (un solo guión bajo)
- ❌ `V1__Initial Schema.sql` (espacios en la descripción)

## Cómo Funciona

### Primera Ejecución

1.  Cuando arrancas la aplicación por primera vez, Flyway:
    - Crea una tabla llamada `flyway_schema_history` en tu base de datos
    - Ejecuta todos los scripts de migración en orden
    - Registra cada migración ejecutada con un checksum

2.  La tabla `flyway_schema_history` contiene:
    - Versión de la migración
    - Descripción
    - Tipo (SQL)
    - Fecha de ejecución
    - Checksum del archivo
    - Estado (SUCCESS/FAILED)

### Ejecuciones Posteriores

En cada arranque de la aplicación, Flyway:
- Compara los archivos de migración contra `flyway_schema_history`
- Ejecuta **solo las migraciones nuevas** que aún no se han aplicado
- Valida que las migraciones antiguas no hayan cambiado (mediante checksum)

## Crear una Nueva Migración

### Paso 1: Crear el archivo SQL

```bash
# Ejemplo: Agregar columna de teléfono a la tabla usuario
touch src/main/resources/db/migration/V2__add_phone_to_usuario.sql
```

### Paso 2: Escribir el SQL

```sql
-- V2__add_phone_to_usuario.sql
ALTER TABLE usuario ADD COLUMN telefono VARCHAR(20);
CREATE INDEX idx_usuario_telefono ON usuario(telefono);
```

### Paso 3: Ejecutar la aplicación

```bash
mvn spring-boot:run
```

Flyway detectará automáticamente el nuevo archivo y lo ejecutará.

## Configuración Actual

En `application.properties`:

```properties
# Flyway habilitado
spring.flyway.enabled=true

# Permite migrar una base de datos existente
spring.flyway.baseline-on-migrate=true

# Ubicación de los scripts
spring.flyway.locations=classpath:db/migration

# Versión base para bases de datos existentes
spring.flyway.baseline-version=0

# Valida migraciones al arrancar
spring.flyway.validate-on-migrate=true
```

## Escenarios Comunes

### 1. Base de Datos Nueva (Primera vez)

```bash
# 1. Crear la base de datos
mysql -u root -p
CREATE DATABASE apibanco CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
EXIT;

# 2. Ejecutar la aplicación
mvn spring-boot:run
```

Flyway ejecutará `V1__initial_schema.sql` y creará todas las tablas.

### 2. Base de Datos Existente (Migración desde Hibernate Auto)

Si ya tienes una base de datos creada con `spring.jpa.hibernate.ddl-auto=update`:

```bash
# Flyway ignorará el esquema existente gracias a baseline-on-migrate=true
mvn spring-boot:run
```

Flyway marcará `V1__initial_schema.sql` como ejecutado (baseline) y solo aplicará migraciones futuras (V2, V3, etc.).

### 3. Agregar una Nueva Columna

```sql
-- V2__add_avatar_url_to_usuario.sql
ALTER TABLE usuario ADD COLUMN avatar_url VARCHAR(500);
```

### 4. Crear una Nueva Tabla

```sql
-- V3__create_notification_table.sql
CREATE TABLE notificacion (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    mensaje TEXT NOT NULL,
    leida BOOLEAN DEFAULT FALSE,
    fecha_creacion DATETIME,
    usuario_id BIGINT,
    CONSTRAINT fk_notificacion_usuario FOREIGN KEY (usuario_id) REFERENCES usuario(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_notificacion_usuario ON notificacion(usuario_id);
```

### 5. Modificar Datos (Data Migration)

```sql
-- V4__set_default_roles.sql
UPDATE usuario SET rol = 'USER' WHERE rol IS NULL;
```

## Rollback

⚠️ **Flyway Community Edition NO soporta rollback automático.**

Para revertir cambios:

### Opción 1: Crear una nueva migración de reversión

```sql
-- V5__remove_avatar_url_from_usuario.sql
ALTER TABLE usuario DROP COLUMN avatar_url;
```

### Opción 2: Rollback manual

1.  Restaurar un backup de la base de datos
2.  Eliminar las entradas correspondientes de `flyway_schema_history`

## Mejores Prácticas

### ✅ DO (Hacer)

- **Nunca modificar migraciones ya aplicadas** - Crear una nueva migración en su lugar
- **Usar transacciones** - Flyway las maneja automáticamente para PostgreSQL/MySQL
- **Hacer migraciones pequeñas** - Una migración = un cambio lógico
- **Probar en local primero** - Antes de aplicar en producción
- **Incluir índices** - Para columnas que se usarán en WHERE/JOIN
- **Usar nombres descriptivos** - `V2__add_email_verification` no `V2__update`
- **Hacer backup** - Antes de aplicar en producción

### ❌ DON'T (No Hacer)

- ❌ Modificar un archivo `V1__` después de ejecutarlo
- ❌ Eliminar archivos de migración después de aplicarlos
- ❌ Usar `DROP TABLE` sin precaución
- ❌ Olvidar foreign keys y constraints
- ❌ Hacer migraciones gigantes con 100+ cambios

## Comandos Maven de Flyway (Opcional)

Puedes usar el plugin de Flyway en Maven:

```xml
<plugin>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-maven-plugin</artifactId>
</plugin>
```

Comandos útiles:

```bash
# Ver información de migraciones
mvn flyway:info

# Validar migraciones
mvn flyway:validate

# Limpiar base de datos (⚠️ CUIDADO - Borra todo)
mvn flyway:clean

# Reparar metadatos (si hay errores)
mvn flyway:repair
```

## Troubleshooting

### Error: "Validate failed: Migration checksum mismatch"

**Causa**: Modificaste un archivo de migración que ya fue ejecutado.

**Solución**:
```bash
# Opción 1: Revertir el cambio en el archivo
git checkout V1__initial_schema.sql

# Opción 2: Reparar (solo si sabes lo que haces)
mvn flyway:repair
```

### Error: "Found non-empty schema(s) but no schema history table"

**Causa**: Base de datos existente sin Flyway.

**Solución**: La configuración `baseline-on-migrate=true` ya lo maneja automáticamente.

### Error: "Migration failed"

**Causa**: Hay un error SQL en tu migración.

**Solución**:
1.  Flyway marca la migración como `FAILED` en `flyway_schema_history`
2.  Corrige el archivo SQL
3.  Ejecuta `mvn flyway:repair` para limpiar el estado fallido
4.  Vuelve a ejecutar la aplicación

## Workflow de Desarrollo

### Desarrollo Local
```properties
# development.properties (opcional)
spring.flyway.enabled=true
spring.jpa.hibernate.ddl-auto=validate
```

### Staging/QA
```properties
spring.flyway.enabled=true
spring.jpa.hibernate.ddl-auto=validate
```

### Producción AWS
```properties
spring.flyway.enabled=true
spring.jpa.hibernate.ddl-auto=validate
spring.flyway.validate-on-migrate=true
```

## Conclusión

Con Flyway implementado:

✅ **Control total** de cambios del esquema
✅ **Versionado** en Git junto con el código
✅ **Auditoría** completa de quién cambió qué
✅ **Seguridad** contra cambios accidentales
✅ **CI/CD** friendly
✅ **Listo para producción en AWS**

Para más información: https://flywaydb.org/documentation/
