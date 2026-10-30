---
description: Commit interactivo siguiendo Conventional Commits v1.0.0. Inspecciona el repo, pregunta qué stagedear y la info del mensaje, muestra el mensaje final para confirmación y solo commitea tras el visto bueno explícito del usuario.
---

# Commit (Conventional Commits v1.0.0)

## Petición del usuario

$ARGUMENTS

## Proceso

1. **Inspección (contexto primero)**: ejecuta `git status --porcelain=v1`, `git diff` (sin stagedear), `git diff --staged` y `git log --oneline -10` (para entender el estilo del historial). Muestra al usuario un resumen claro del estado: archivos modificados / nuevos / eliminados, qué hay ya stagedeado y los últimos commits.
2. **Staging**: pregunta con la herramienta `question` qué stagedear:
   - Todo lo actual (`git add -A`).
   - Una selección explícita de rutas (los archivos listados, accionables por número o nombre).
   - Nada más (usar solo lo ya stagedeado).

   Revalida con `git status` tras stagedear y muestra `git diff --staged --stat`.
3. **Elicitación del mensaje** (con `question`, una a una; deduce y propone siempre una recomendación a partir del diff real, p. ej. scope por los módulos tocados):
   - **Tipo**: `feat`, `fix`, `docs`, `style`, `refactor`, `perf`, `test`, `build`, `ci`, `chore`, `revert` (especificación v1.0.0).
   - **Scope (opcional)**: tipo `feat(api)`, `fix(db)`, etc. Propón uno según las rutas afectadas (domain, application, infrastructure, specs, db...); permite omitirlo.
   - **Subject**: en inglés, imperativo ("add", no "added"), minúscula inicial, sin punto final, idealmente ≤ 50 caracteres (nunca > 72). Si la elicitación no lo aclara, pídelo abiertamente.
   - **Body (opcional)**: explica el *qué* y el *por qué* (no el *cómo*), margen a 100 columnas. Propón uno si el diff es complejo.
   - **Breaking change (sí/no)**: si hay cambio rompiente, añade `!` tras type/scope y un footer `BREAKING CHANGE: <descripción e impacto/migración>`.
   - **Footers (opcional)**: referencias a issues (`Closes #123`, `Refs #456`), `Co-authored-by:` u otros tokens. Confirma si es un `revert` para el formato `revert: <subject del revertido>` + footer `This reverts commit <hash>.`.
4. **Composición**: construye el mensaje conforme a la spec oficial:

   ```
   <type>[optional scope][!]: <description>

   [optional body]

   [optional footer(s)]
   ```

5. **Confirmación (obligatoria, no negociable)**: muestra el mensaje EXACTO en un bloque de código junto a `git diff --staged --stat` y pregunta con `question`:
   - Commitear tal cual.
   - Editar algo (indica qué parte: tipo, scope, subject, body, footers) → vuelve al paso 3 solo de esa parte y repite la confirmación.
   - Cancelar (no se commitea nada).
6. **Ejecución** (solo si confirma):
   - Escribe el mensaje EXACTO (sin alterar saltos de línea ni formato) en `/tmp/opencode/commit-msg.txt` y ejecuta `git commit -F /tmp/opencode/commit-msg.txt` para evitar problemas de quoting.
   - Nunca uses `--no-verify`. Si los hooks rechazan el commit, corrige el problema reportado y haz un commit nuevo (no amend de commits fallidos).
   - Reporta el resultado: `git log -1 --format="%h %s"` y el estado de `git status` restante. No hagas push salvo que el usuario lo pida después.

## Reglas

- El commit SIEMPRE pasa por la confirmación del paso 5: es la formalización del paso manual del propietario que exige `AGENTS.md`. Nada de commits automáticos.
- Nunca stagedear ni commitear secretos ni archivos sensibles (`.env`, claves, `*.p12`...): si detectas alguno en el diff, detente y avísalo.
- No editar archivos del proyecto como parte de este comando (salvo `git add`, que es staging, no modificación).
- Respeta el estilo del historial si es coherente con Conventional Commits.
