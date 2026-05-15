# LOADIX


## Estado actual

El repositorio contiene una version funcional del producto con:

- `frontend/`: aplicacion Next.js 16 con landing, autenticacion y dashboards por rol.
- `backend/`: API Spring Boot 3.4 con autenticacion JWT por cookie, perfiles, cargas y documentacion OpenAPI.
- `docker-compose.yml`: PostgreSQL 16 y Redis 7 para entorno local.
- `.github/workflows/ci.yml`: comprobaciones basicas de estructura del repo y validacion de Docker Compose.

## Stack actual

- Frontend: Next.js 16, React 19, TypeScript, Tailwind CSS 4, React Hook Form, Zod
- Backend: Java 17, Spring Boot 3.4, Spring Web, Spring Security, Spring Data JPA, Redis, Flyway, OpenAPI
- Base de datos e infraestructura: PostgreSQL, Redis, Docker Compose
- Testing: JUnit 5, Spring Boot Test, Spring Security Test, Testcontainers
- Pago: integracion backend con Stripe para la reserva de cargas

## Funcionalidad implementada

### Frontend

- Landing publica en `/`
- Registro en `/register`
- Login en `/login`
- Dashboard de almacen en `/dashboard/warehouse`
- Perfil de almacen en `/dashboard/warehouse/profile`
- Publicacion de cargas en `/dashboard/warehouse/new-load`
- Listado y edicion de cargas propias en `/dashboard/warehouse/loads`
- Dashboard de transportista en `/dashboard/carrier`
- Perfil de transportista en `/dashboard/carrier/profile`
- Marketplace de cargas en `/dashboard/carrier/marketplace`
- Listado de cargas reservadas o en curso en `/dashboard/carrier/trips`

### Backend

- Health check en `/api/v1/health`
- Auth:
  - `POST /api/v1/auth/register`
  - `POST /api/v1/auth/login`
  - `GET /api/v1/auth/me`
  - `PATCH /api/v1/auth/email`
  - `POST /api/v1/auth/logout`
- Profiles:
  - `POST|GET|PUT /api/v1/profiles/warehouse`
  - `POST|GET|PUT /api/v1/profiles/carrier`
- Loads:
  - `POST /api/v1/loads`
  - `GET /api/v1/loads/mine`
  - `GET /api/v1/loads/dashboard/warehouse`
  - `GET /api/v1/loads/dashboard/carrier`
  - `GET /api/v1/loads/carrier/mine`
  - `GET /api/v1/loads/available`
  - `PUT /api/v1/loads/{loadId}`
  - `PATCH /api/v1/loads/{loadId}/status`
  - `POST /api/v1/loads/{loadId}/reserve`
- OpenAPI disponible en `/v3/api-docs` y Swagger UI en `/swagger-ui/index.html`

## Limitaciones actuales

- La subida de documentos del perfil de transportista todavia no esta conectada al backend.
- La tarjeta de facturacion del dashboard de transportista muestra datos provisionales, no datos reales de backend.
- El frontend incluye dependencias y scripts de Prisma, pero en el estado actual la aplicacion web consume la API de Spring Boot; Prisma no forma parte del flujo principal documentado aqui.

## Estructura del repo

```text
.
├─ backend/
├─ frontend/
├─ documents/
├─ .github/workflows/
├─ docker-compose.yml
└─ README.md
```

## Arranque en local

### 1. Infraestructura

```bash
docker compose up -d
```

Servicios locales por defecto:

- PostgreSQL: `localhost:5434`
- Redis: `localhost:6379`

### 2. Backend

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

Ejecucion:

```bash
cd backend
./mvnw spring-boot:run
```

El backend arranca por defecto en `http://localhost:8082`.

### 3. Frontend

Instalacion y arranque:

```bash
cd frontend
npm install
npm run dev
```

La aplicacion arranca por defecto en `http://localhost:3000`.

El frontend consume la API en `http://localhost:8082/api/v1` por defecto. Si necesitas otra URL, el codigo soporta `NEXT_PUBLIC_API_URL`.

Variables incluidas en `frontend/.env.example`:

- `DATABASE_URL`
- `SUPABASE_URL`
- `SUPABASE_ANON_KEY`
- `SUPABASE_SERVICE_ROLE_KEY`
- `UPSTASH_REDIS_REST_URL`
- `UPSTASH_REDIS_REST_TOKEN`
- `JWT_SECRET`
- `NODE_ENV`

## Validacion ejecutada

Se ha comprobado el estado actual del repo con:

- `docker compose config`
- `frontend`: `npm run lint` y `npm run build`
- `backend`: `./mvnw compile` y `./mvnw test`

Todos esos comandos han pasado en el estado actual documentado en este README.
