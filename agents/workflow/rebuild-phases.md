# Rebuild Phases

## Proposito
Definir el flujo especifico para reconstruir LOADIX 1:1 a partir del repo original, manteniendo un historial Git limpio y profesional.

## Regla principal
No reconstruir por archivos sueltos ni por capas aisladas si eso rompe la narrativa del proyecto.
Reconstruir por bloques con sentido de producto, infraestructura o arquitectura.

## Fase 1 - Comparar
Objetivo: verificar que existe en el repo original y entender su alcance real.

Salida minima:
- bloque identificado
- archivos o zonas afectadas en el original
- criterio de que ese bloque esta realmente presente

## Fase 2 - Delimitar
Objetivo: convertir ese bloque en una unidad profesional reconstruible.

Salida minima:
- titulo del ticket
- alcance del bloque
- fuera de alcance
- validacion esperada

## Fase 3 - Reconstruir
Objetivo: implementar el bloque minimo correcto en este repo nuevo.

Reglas:
- no arrastrar ruido del repo original
- no meter mejoras no aprobadas si rompen el 1:1
- si hace falta adaptar algo por limpieza tecnica, explicarlo y validarlo

## Fase 4 - Validar
Objetivo: comprobar con evidencia que el bloque reconstruido queda correcto.

Evidencia valida:
- arranque local
- tests
- build
- pipeline
- comparacion funcional con el repo original

## Fase 5 - Commit
Objetivo: cerrar el bloque con un commit limpio.

Reglas:
- el mensaje sigue el titulo del ticket
- solo entra lo que pertenece al bloque
- no mezclar trabajo de bloques siguientes

## Regla de avance
No empezar el siguiente bloque hasta que el actual:
- este validado
- tenga commit
- y quede reflejado en el backlog del repo nuevo si aplica
