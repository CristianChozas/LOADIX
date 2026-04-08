# AGENTS.md

## Proposito
Kernel de comportamiento para reconstruir LOADIX en un repo nuevo, alcanzando el mismo estado funcional que el repo original pero con historial limpio, commits profesionales y workflow de proyecto real.

## Contexto del proyecto
- Este repo no parte de cero en ideas: parte del `LOADIX` original como referencia.
- El `LOADIX` original no se toca. Es fuente de verdad para comparar, verificar y portar bloques.
- El objetivo es llegar al mismo punto que el repo original, no reinventar el producto ni redisenarlo por capricho.

## Objetivo de trabajo
- Reconstruir el proyecto por bloques coherentes.
- Hacer commits pequenos, limpios y defendibles.
- Mantener una historia Git profesional y comprensible.
- Aprender stack, arquitectura y workflow real de empresa durante la reconstruccion.

## Reglas globales
- Hablar en espanol por defecto en este repositorio.
- Priorizar comprension, razonamiento y validacion antes que velocidad.
- No inventar trabajo que no exista en el repo original ni en el backlog aprobado.
- No copiar todo el repo original de golpe.
- No hacer commits artificiales por archivo o por clase.
- No dejar CI, Docker u OpenAPI para el final: deben aparecer temprano si ya existen en el sistema objetivo.
- No dar por cerrado un bloque sin evidencia suficiente.

## Tickets
- Los tickets deben parecer tickets de trabajo real, no prompts de implementacion.
- Un ticket describe capacidad, alcance y criterio de aceptacion; no receta tecnica detallada.
- Evitar tickets hiper especificos por capa, VO, excepcion o mapper salvo que haya una necesidad clara.
- Cuando el backlog original sea demasiado academico o inconsistente, reinterpretarlo en formato profesional antes de usarlo para reconstruir.

## Commits
- El mensaje de commit debe seguir el titulo del ticket activo.
- Cada commit debe quedar acotado a un bloque real y validable.
- No mezclar infraestructura, frontend, backend y ruido no relacionado en un mismo commit salvo que formen un bloque logico real.
- Antes de hacer commit, comprobar siempre `git status`, diff y alcance.

## Modo de trabajo
- Trabajar por bloques 1:1 respecto al repo original.
- Cada bloque debe seguir este orden:
  1. comparar con el repo original
  2. definir el alcance minimo del bloque
  3. reconstruirlo en este repo
  4. validarlo
  5. hacer commit
- El siguiente bloque solo empieza cuando el actual esta claro y validado.

## Implementacion
- No escribir codigo salvo peticion explicita del usuario.
- Antes de implementar: verificar en el repo original que ese bloque existe y entender su alcance real.
- Preferir cambios minimos correctos.
- No introducir arquitectura nueva no justificada.

## Infraestructura y calidad
- CI debe existir desde etapas tempranas.
- Docker y entorno local reproducible deben existir desde etapas tempranas.
- OpenAPI/Swagger debe crecer junto con la API, no dejarse para el final.
- README y `.env.example` deben aparecer pronto cuando ayuden al arranque real del proyecto.

## Carga de contexto
- Cargar siempre este archivo y `agents/workflow/rebuild-phases.md`.
- Cargar `documents/TICKETS_LOADIX.md` cuando el trabajo dependa del backlog.
- Consultar el repo original solo para verificar realidad, no para copiar sin criterio.

## Regla de cierre
- Cada cierre de bloque debe dejar claro:
  - que se reconstruyo
  - como se valido
  - que ticket queda asociado
  - que sigue despues
