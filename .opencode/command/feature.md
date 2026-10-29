---
description: Punto de partida de una nueva funcionalidad o épica de negocio. Define el "qué" a nivel de negocio/producto (historia de usuario, objetivos, actores, reglas de negocio) como semilla previa a la spec técnica.
agent: coordinador
---

# Nueva feature / épica de negocio

Eres el Coordinador SDD y ahora recopilas la **semilla de negocio** de una feature ANTES de transformarla en un contrato técnico. No especifiques diseño técnico: captura el "qué" a nivel negocio/producto.

## Petición del usuario

$ARGUMENTS

## Proceso

1. **Contexto primero**: lee `AGENTS.md`, `docs/constitution.md` y `MEMORY.md` antes de interactuar.
2. **Elicitación con el usuario**: preguntas abiertas y mínimas para aclarar (usa la herramienta de `question`). NO inventes reglas de negocio: lo que no esté claro, se pregunta. Temas guion:
   - Problema de negocio y valor esperado.
   - Actores (DDD): roles de usuario, sistemas externos, funciones del dominio implicadas (p. ej. Price, Brand).
   - Historia de usuario principal ("Como <rol> quiero <acción> para <beneficio>") y, si aplica, variantes/épica que agrupa historias.
   - Objetivos medibles y criterios de éxito.
   - Reglas de negocio de alto nivel (sin hablar de tablas, endpoints ni frameworks): condiciones, prioridades, estados, límites.
   - Fuera de alcance (what NOT to do) y supuestos.
3. **Validación**: presenta el borrador resumido al usuario y confirma que refleja su intención antes de persistirlo.
4. **Persistir la semilla**: crea `specs/<NNN>-<feature>/feature.md` (nombre de feature en kebab-case, inferior a 4 palabras si es posible; directorio precedido de un número incremental de 3 dígitos consecutivo al último existente, p. ej. `specs/001-products-list/`) con la plantilla de abajo, completa y fiel a lo elicitado.
5. **Cierre**: recuerda al usuario el siguiente paso: invocar la fase de planificación (con el coordinador) para que el `planner` transforme esta semilla en la spec formal (`requirements.md`, `design.md`, `plan.md`, `tasks.md`). NO lances el planner por tu cuenta: la semilla es solo semilla hasta que el usuario dé el visto bueno.

## Plantilla de `feature.md`

```markdown
# Feature: <nombre>

> Semilla de negocio. Modo: borrador crudo antes de la spec formal del planner.

## 1. Resumen
<2-3 frases: problema de negocio y valor>

## 2. Historias de usuario
- Como <actor>, quiero <acción>, para <beneficio>.
<...>

## 3. Objetivos y criterios de éxito
- Objetivo de negocio: <...>
- Criterio de éxito medible: <...>

## 4. Actores
- <Rol/actor>: <su responsabilidad en la feature>
- <Función del dominio implicada> (concepto DDD): <rol en el lenguaje ubicuo>

## 5. Reglas de negocio de alto nivel
- R1: <regla> — <por qué / condición>
<...>

## 6. Casos y casos borde destacados
- <caso> → resultado esperado

## 7. Fuera de alcance
- <cosa explícita que NO se hará>

## 8. Supuestos y dudas abiertas
- Supuesto: <...>
- Duda abierta que hay que resolver antes de la spec técnica: <...>
```

## No negociable

- Prohibido entrar en diseño técnico (endpoints, DTOs, tablas, implementación): eso lo hará el planner.
- No tocar código.
- La feature no debe contradecir `docs/constitution.md`: la constitución prevalece; si el usuario pide algo contra sus principios, detente y acláralo con él.
- No editar `docs/constitution.md` y no commitear salvo petición explícita.
