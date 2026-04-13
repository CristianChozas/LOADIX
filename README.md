# LOADIX

Plataforma B2B para la gestión y contratación de cargas entre almacenes y transportistas.

## Stack técnico

- Java 17
- Spring Boot
- Maven
- PostgreSQL
- Redis
- Flyway
- Testcontainers
- Docker

## Entorno

El backend puede trabajar de dos formas:

- local con `docker compose` usando PostgreSQL y Redis locales
- entorno real usando `backend/.env` con servicios gestionados

Variables recomendadas para entorno real:

- `DATABASE_URL`: URL de PostgreSQL de Supabase. Se aceptan formatos `postgresql://...`, `postgres://...` o `jdbc:postgresql://...`
- `DATABASE_USERNAME` y `DATABASE_PASSWORD`: opcionales si la URL no incluye credenciales
- `REDIS_URL`: URL estándar de Redis (`redis://` o `rediss://`)
- `REDIS_USERNAME` y `REDIS_PASSWORD`: opcionales según el proveedor
- `REDIS_SSL=true`: activar si el proveedor exige TLS

Nota: el backend usa cliente Redis TCP de Spring. Si tu proveedor solo expone credenciales REST y no una URL estándar de Redis, esa integración no servirá tal cual.

## Arquitectura

Se aplica una arquitectura orientada a Clean Architecture, con una base preparada para crecer por módulos y mantener el backend desacoplado.

## Estructura del backend

```text
backend/
├─ .mvn/
│ └─ wrapper/
├─ src/
│ ├─ main/
│ │ ├─ java/
│ │ │ └─ com/
│ │ │ └─ loadix/
│ │ │ ├─ application/
│ │ │ │ └─ exception/
│ │ │ ├─ domain/
│ │ │ └─ infrastructure/
│ │ │ ├─ config/
│ │ │ ├─ http/
│ │ │ │ └─ filter/
│ │ │ ├─ persistence/
│ │ │ │ ├─ adapter/
│ │ │ │ ├─ entity/
│ │ │ │ └─ repository/
│ │ │ ├─ security/
│ │ │ └─ startup/
│ │ └─ resources/
│ │ └─ db/
│ │ └─ migration/
│ └─ test/
│ └─ java/
│ └─ com/
│ └─ loadix/
│ ├─ infrastructure/
│ │ ├─ config/
│ │ ├─ http/
│ │ │ └─ filter/
│ │ └─ security/
│ └─ support/
└─ target/
├─ classes/
├─ test-classes/
├─ surefire-reports/
└─ maven-status/
```

## Aviso legal

El frontend del proyecto no se publica porque forma parte de un activo comercial no abierto al público.
Todo el código de este repositorio es propiedad intelectual y no está autorizado para su uso, copia, redistribución ni explotación comercial sin permiso expreso.
