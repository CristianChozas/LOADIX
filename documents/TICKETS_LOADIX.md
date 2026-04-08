# LOADIX — Backlog operativo del repo reconstruido

**Proyecto:** Loadix - Plataforma B2B de transporte de mercancias  
**Objetivo del repo:** reconstruir el estado actual del proyecto original con un historial Git limpio, profesional y defendible  
**Metodologia:** tickets por capacidad o bloque real de sistema, no por receta tecnica  
**Referencia funcional:** repo original `LOADIX`  
**Idioma de trabajo:** espanol

---

## Principios del backlog

- El backlog debe parecerse al trabajo real de empresa.
- Los tickets describen capacidad, bloque tecnico o integracion real, no pasos academicos hiper detallados.
- CI, Docker y OpenAPI aparecen temprano porque en un proyecto real no se dejan para el final.
- Cada ticket debe poder reconstruirse, validarse y commitearse como una unidad coherente.
- El resultado final debe quedar 1:1 con el repo original en alcance real, aunque el historial nuevo sea mejor.
- Los titulos de tickets y commits se escriben en ingles.
- Convencion de commits: `#<ticket> - <short title>`.

---

## Estados

- TODO
- DOING
- DONE

---

## Fase 0 — Base del repositorio y entorno profesional

### Ticket 001 - Bootstrap del repo reconstruido
**Estado:** TODO  
**Objetivo:** crear la base minima del nuevo repo y documentar las reglas de reconstruccion.

**Acceptance criteria:**
- existe `README.md` inicial
- existe `AGENTS.md`
- existe `agents/workflow/rebuild-phases.md`
- existe `documents/TICKETS_LOADIX.md`

### Ticket 002 - Estructura inicial del monorepo
**Estado:** TODO  
**Objetivo:** crear la estructura base del repo nuevo para backend, frontend y documentacion sin portar aun funcionalidad.

**Acceptance criteria:**
- existe la estructura minima del repo
- la estructura es coherente con el estado objetivo del proyecto original
- el repo arranca limpio y comprensible

### Ticket 003 - Docker y entorno local reproducible
**Estado:** TODO  
**Objetivo:** reconstruir el entorno local base con Compose, variables ejemplo y servicios necesarios.

**Acceptance criteria:**
- existe `docker-compose.yml`
- existen variables ejemplo
- el entorno local puede levantarse sin depender del repo original

### Ticket 004 - CI inicial del repositorio
**Estado:** TODO  
**Objetivo:** añadir pipeline minima para validar build y tests desde etapas tempranas.

**Acceptance criteria:**
- existe workflow de CI
- el pipeline corre sobre el stack reconstruido actual

### Ticket 005 - OpenAPI y documentacion base de la API
**Estado:** TODO  
**Objetivo:** dejar preparada la base de OpenAPI/Swagger desde fases tempranas del backend.

**Acceptance criteria:**
- existe estrategia clara para exponer la documentacion de la API
- la documentacion puede crecer junto a los endpoints desde el inicio

---

## Fase 1 — Shell del frontend y base del backend

### Ticket 006 - Shell inicial del frontend
**Estado:** TODO  
**Objetivo:** reconstruir la base visual y estructural del frontend que ya existe en el repo original.

**Acceptance criteria:**
- el frontend compila
- existe shell base y estructura principal
- el resultado es reconocible respecto al repo original

### Ticket 007 - Bootstrap del backend Java Spring
**Estado:** TODO  
**Objetivo:** reconstruir el backend base con su estructura y arranque minimo.

**Acceptance criteria:**
- el backend compila y arranca
- existe estructura por capas coherente con la arquitectura objetivo

### Ticket 008 - Health, configuracion base y observabilidad inicial
**Estado:** TODO  
**Objetivo:** reconstruir health endpoint, configuracion base y logs iniciales.

**Acceptance criteria:**
- existe endpoint de health
- la configuracion base esta externalizada donde corresponda
- los logs basicos estan disponibles

---

## Fase 2 — Seguridad, validacion y contratos

### Ticket 009 - Validacion de entrada y contratos base
**Estado:** TODO  
**Objetivo:** reconstruir DTOs, validacion y convenciones de contrato de entrada/salida.

### Ticket 010 - Manejo global de errores
**Estado:** TODO  
**Objetivo:** reconstruir el modelo uniforme de errores y su manejo global.

### Ticket 011 - Seguridad base y contexto de autenticacion
**Estado:** TODO  
**Objetivo:** reconstruir la base de seguridad, JWT y resolucion de usuario autenticado.

### Ticket 012 - Rate limiting y hardening inicial
**Estado:** TODO  
**Objetivo:** reconstruir protecciones basicas de entrada y hardening inicial del backend.

---

## Fase 3 — Identidad y acceso

### Ticket 013 - Dominio de usuarios y roles
**Estado:** TODO  
**Objetivo:** reconstruir el bloque de identidad y roles existente en el sistema objetivo.

### Ticket 014 - Registro de usuario
**Estado:** TODO  
**Objetivo:** reconstruir el flujo de registro backend y su integracion minima de frontend si ya existe en el original.

### Ticket 015 - Login y emision de JWT
**Estado:** TODO  
**Objetivo:** reconstruir el flujo de autenticacion y entrega de token.

### Ticket 016 - Estado de autenticacion en frontend
**Estado:** TODO  
**Objetivo:** reconstruir gestion de sesion, guards y consumo de contexto autenticado.

### Ticket 017 - Dashboards iniciales por rol
**Estado:** TODO  
**Objetivo:** reconstruir las vistas iniciales diferenciadas para warehouse y carrier.

---

## Fase 4 — Perfiles y cargas

### Ticket 018 - Perfiles de warehouse y carrier
**Estado:** TODO  
**Objetivo:** reconstruir el bloque de perfiles y sus contratos principales.

### Ticket 019 - Gestion de perfiles en backend
**Estado:** TODO  
**Objetivo:** reconstruir la logica de consulta y actualizacion de perfiles.

### Ticket 020 - Flujos de perfil en frontend
**Estado:** TODO  
**Objetivo:** reconstruir formularios y UX de perfiles para ambos roles.

### Ticket 021 - Dominio y modelo base de cargas
**Estado:** TODO  
**Objetivo:** reconstruir el bloque de cargas, estados iniciales y contratos nucleares.

### Ticket 022 - Publicacion de cargas en backend
**Estado:** TODO  
**Objetivo:** reconstruir create/list/get de cargas.

### Ticket 023 - Publicacion y listado de cargas en frontend
**Estado:** TODO  
**Objetivo:** reconstruir la experiencia de publicar y explorar cargas ya presente en el original.

### Ticket 024 - Dashboard de cargas
**Estado:** TODO  
**Objetivo:** reconstruir la entrada principal al flujo operativo de cargas.

---

## Fase 5 — Flujo core de operacion

### Ticket 025 - Estados y transiciones de carga
**Estado:** TODO  
**Objetivo:** reconstruir reglas y casos de uso de estado operativo de las cargas.

### Ticket 026 - Detalle de carga
**Estado:** TODO  
**Objetivo:** reconstruir la vista y el contrato completo de detalle de carga.

### Ticket 027 - Cargas visibles para carrier
**Estado:** TODO  
**Objetivo:** reconstruir el marketplace inicial para el carrier.

### Ticket 028 - Cargas propias para warehouse
**Estado:** TODO  
**Objetivo:** reconstruir la gestion operativa de cargas del warehouse.

### Ticket 029 - Notificaciones basicas
**Estado:** TODO  
**Objetivo:** reconstruir el bloque inicial de notificaciones y eventos simples.

### Ticket 030 - Busqueda y filtros de cargas
**Estado:** TODO  
**Objetivo:** reconstruir query API, cache y experiencia de filtrado presentes en el original.

### Ticket 031 - Historial y timeline de actividad
**Estado:** TODO  
**Objetivo:** reconstruir el seguimiento historico y visual de la actividad de una carga.

### Ticket 032 - Cancelaciones y politicas operativas
**Estado:** TODO  
**Objetivo:** reconstruir reglas y flujo UX de cancelacion.

### Ticket 033 - Diseno de matching fase 2
**Estado:** TODO  
**Objetivo:** reconstruir la base analitica y contractual del siguiente salto del core.

---

## Fase 6 — Tracking y tiempo real

### Ticket 034 - Infraestructura realtime
**Estado:** TODO

### Ticket 035 - Modelo de tracking
**Estado:** TODO

### Ticket 036 - API de tracking
**Estado:** TODO

### Ticket 037 - Cliente realtime en frontend
**Estado:** TODO

### Ticket 038 - Mapa operativo en tiempo real
**Estado:** TODO

### Ticket 039 - Panel de seguimiento warehouse
**Estado:** TODO

### Ticket 040 - PWA operativa del carrier
**Estado:** TODO

### Ticket 041 - Persistencia de ultima ubicacion
**Estado:** TODO

---

## Fase 7 — Documentacion operativa y POD

### Ticket 042 - Dominio documental
**Estado:** TODO

### Ticket 043 - Upload y gestion documental
**Estado:** TODO

### Ticket 044 - Flujo ePOD del carrier
**Estado:** TODO

### Ticket 045 - Visualizacion documental
**Estado:** TODO

### Ticket 046 - Generacion de albaranes y CMR
**Estado:** TODO

### Ticket 047 - Centro documental frontend
**Estado:** TODO

---

## Fase 8 — Pagos y facturacion

### Ticket 048 - Setup de pagos marketplace
**Estado:** TODO

### Ticket 049 - Dominio de pagos
**Estado:** TODO

### Ticket 050 - Cobro de servicio y payment intent
**Estado:** TODO

### Ticket 051 - Payout rapido 24h
**Estado:** TODO

### Ticket 052 - Onboarding de Stripe para carrier
**Estado:** TODO

### Ticket 053 - Checkout frontend para warehouse
**Estado:** TODO

### Ticket 054 - Dominio de facturacion
**Estado:** TODO

### Ticket 055 - Generacion automatica de facturas
**Estado:** TODO

### Ticket 056 - Dashboards financieros
**Estado:** TODO

---

## Fase 9 — Features avanzadas

### Ticket 057 - Valoraciones y reputacion
**Estado:** TODO

### Ticket 058 - Centro de notificaciones y push
**Estado:** TODO

### Ticket 059 - Matching inteligente de rutas
**Estado:** TODO

### Ticket 060 - Analytics operativos
**Estado:** TODO

### Ticket 061 - Disputas y reclamaciones
**Estado:** TODO

### Ticket 062 - API publica e integraciones
**Estado:** TODO

### Ticket 063 - Automatizacion administrativa
**Estado:** TODO

### Ticket 064 - Chat IA FAQ clientes
**Estado:** TODO

### Ticket 065 - Documentacion telematica integral
**Estado:** TODO

---

## Fase 10 — Escalado y portfolio

### Ticket 066 - Roles avanzados y multi tenancy base
**Estado:** TODO

### Ticket 067 - Integraciones enterprise y compliance
**Estado:** TODO

### Ticket 068 - Sostenibilidad y huella de CO2
**Estado:** TODO

### Ticket 069 - Optimizacion avanzada y telematica
**Estado:** TODO

### Ticket 070 - Admin panel y auditoria
**Estado:** TODO

### Ticket 071 - PWA completa y offline
**Estado:** TODO

### Ticket 072 - Testing E2E completo
**Estado:** TODO

### Ticket 073 - Dockerizacion completa y despliegue
**Estado:** TODO

### Ticket 074 - Mapa operativo avanzado
**Estado:** TODO

### Ticket 075 - Diferenciales de negocio y release portfolio
**Estado:** TODO

---

## Notas de uso

- Este backlog no pretende describir linea por linea la implementacion.
- Cada ticket debera reinterpretarse contra la realidad del repo original antes de reconstruirse.
- Los estados se iran actualizando segun el nuevo repo alcance 1:1 el sistema de referencia.
