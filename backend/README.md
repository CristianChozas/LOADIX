# LOADIX Backend

API REST de LOADIX desarrollada con Spring Boot.

## Stack

- Java 17
- Spring Boot 3.4
- Spring Security
- Spring Data JPA
- PostgreSQL
- Redis
- Flyway
- OpenAPI / Swagger
- Testcontainers

## Arranque local

### 1. Levantar infraestructura

Desde la raiz del proyecto:

```bash
docker compose up -d
```

Servicios locales por defecto:

- PostgreSQL: `localhost:5434`
- Redis: `localhost:6379`

### 2. Configurar variables

El backend soporta carga de variables desde `backend/.env`.

Variables disponibles en `backend/.env.example`:

- `DATABASE_URL`
- `DATABASE_USERNAME`
- `DATABASE_PASSWORD`
- `SUPABASE_URL`
- `SUPABASE_ANON_KEY`
- `SUPABASE_SERVICE_ROLE_KEY`
- `UPSTASH_REDIS_REST_URL`
- `UPSTASH_REDIS_REST_TOKEN`
- `JWT_SECRET`
- `SUPABASE_CARRIER_DOCS_BUCKET`
- `STRIPE_SECRET_KEY`

### 3. Ejecutar aplicacion

```bash
./mvnw spring-boot:run
```

La API arranca por defecto en `http://localhost:8082`.

## Endpoints principales

- `GET /api/v1/health`
- `POST /api/v1/auth/register`
- `POST /api/v1/auth/login`
- `GET /api/v1/auth/me`
- `PATCH /api/v1/auth/email`
- `POST /api/v1/auth/logout`
- `POST|GET|PUT /api/v1/profiles/warehouse`
- `POST|GET|PUT /api/v1/profiles/carrier`
- `POST /api/v1/loads`
- `GET /api/v1/loads/mine`
- `GET /api/v1/loads/dashboard/warehouse`
- `GET /api/v1/loads/dashboard/carrier`
- `GET /api/v1/loads/carrier/mine`
- `GET /api/v1/loads/available`
- `PUT /api/v1/loads/{loadId}`
- `PATCH /api/v1/loads/{loadId}/status`
- `POST /api/v1/loads/{loadId}/reserve`

## Documentacion de API

- OpenAPI JSON: `http://localhost:8082/v3/api-docs`
- Swagger UI: `http://localhost:8082/swagger-ui/index.html`

## Validacion

Comandos verificados en el estado actual:

```bash
./mvnw compile
./mvnw test
```
