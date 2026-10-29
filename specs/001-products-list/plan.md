# Plan — GET /api/products (listado de productos)

## 0. Premisas

- Patrón de implementación: clon estructural del listado `GET /api/brands` (mismos paquetes, mismos roles, mismos tags de test).
- Regla de dependencias entre fases: **OpenAPI primero → codegen → dominio/ports → adapters → liquibase wiring → tests**. Las subtareas independientes pueden ejecutarse en paralelo (ver `tasks.md`).
- Fuera de alcance: ver §5 de `requirements.md` (no tocar brands, prices ni `GET /api/prices/active`).

## 1. Orden de ejecución y dependencias

### Fase A — Contrato y regeneración (bloqueante para los adapters REST)
1. **A1.** Editar `src/main/resources/rest/openapi.yaml`: path `/api/products` (tag `Products`, query param `q`) + schema `ProductResponse` (ver design §2). **NO tocar** `target/generated/openapi/**`.
2. **A2.** Verificar regeneración: `./mvnw clean compile` → deben existir en `target/generated/openapi/`: `ProductsApi` (`...adapter.input.rest.api`), `ProductResponse` (DTO con setters), tag/operation `getAllProducts`. Criterio: compilación limpia sin errores.

### Fase B — Dominio y application (paralelizable con A y C)
- **B1.** `domain/model/Product.java` (POJO puro, sin Spring/JPA).
- **B2.** `application/port/input/GetProductsUseCase.java`, `GetProductsQuery.java` (Builder, campo `q`).
- **B3.** `application/port/output/ProductRepository.java` (solo `findAll()`).
- **B4.** `application/service/GetProductsService.java` (filtrado en memoria sobre `name`/`description`, `q` blank = sin filtro, null-safe).
- Criterio de la fase: `./mvnw clean compile` + `./mvnw test -Dtest=GetProductsServiceTest` (tras crear el test de la Fase D).

### Fase C — Persistencia y Liquibase (paralelizable con A y B parcialmente)
- **C1.** `006-create-products-table.xml` (tabla + seed) + include en `db.changelog-master.xml` (independiente de todo el código Java → puede ir en paralelo desde el minuto cero).
- **C2.** `infrastructure/adapter/output/persistence/`: `ProductEntity`, `SpringDataProductRepository`, `ProductPersistenceMapper`, `ProductRepositoryAdapter` (el adapter depende del port `ProductRepository` de B3).
- Criterio: `./mvnw clean test -Dgroups=e2e` arranca Liquibase sin errores y la tabla existe (verificado indirectamente por el e2e).

### Fase D — Adapter REST, wiring y tests
- **D1.** `infrastructure/adapter/input/rest/ProductController.java` + `ProductRestMapper.java` (dependen de A1/A2 y B2/B4).
- **D2.** Wiring en `BeanConfiguration.java`: bean `getProductsUseCase` (depende de B4).
- **D3.** Test `unit`: `GetProductsServiceTest` (depende de B3/B4; puede escribirse antes de C/D).
- **D4.** Test `e2e`: `ProductControllerE2ETest` (depende de A1/A2, C1, D1, D2; data el seed de C1).

### Fase E — Documentación y verificación final
- **E1.** `CHANGELOG.md`: entrada `Added` bajo `[Unreleased]`.
- **E2.** Verificación global: full suite `./mvnw clean test` (regresión de brands/prices incluida).
- (Opcional, posterior a esta feature: añadir `GET /api/products` a la colección Postman `src/test/resources/postman/pricing-service.postman_collection.json` — no estrictamente necesario para el CA).

## 2. Verificación por fase (comandos)

| Fase | Comando | Resultado esperado |
|---|---|---|
| A | `./mvnw clean compile` | Codegen produce `ProductsApi`/`ProductResponse`; build OK. |
| B | `./mvnw clean compile` | Compila; puertos y service sin referencias a infraestructura. |
| C | `./mvnw test -Dgroups=e2e` (tras D) | App arranca, Liquibase aplica 006, endpoint responde. |
| D | `./mvnw test -Dtest=GetProductsServiceTest` / `-Dtest=ProductControllerE2ETest` | Verdes. |
| E | `./mvnw clean test` | Suite completa verde (sin regresiones). |

## 3. Impactos cruzados (checklist de reviewer)

- [ ] `openapi.yaml`: solo añade path/schema; no modifica `/api/brands` ni `/api/prices/active`.
- [ ] `target/generated/openapi/**`: únicamente regenerado, nunca editado a mano.
- [ ] `domain/`: `Product` puro, sin imports de Spring/JPA.
- [ ] `application/`: puertos sin detalle tecnológico; service sin anotaciones Spring.
- [ ] `infrastructure/`: controllers/mappers/entities/adapters/config; dependencias solo hacia application/domain.
- [ ] Liquibase: único changeset nuevo `006-*`; include en master; `ddl-auto` sigue `none`.
- [ ] Tests: tags correctos (`unit` sin Spring, `e2e` con RestAssured); aserciones e2e orden-agnósticas (sin `[0].id` hard-codeado a un orden).
- [ ] `CHANGELOG.md` actualizado bajo `[Unreleased]`.

## 4. Riesgos y mitigaciones

| Riesgo | Mitigación |
|---|---|
| Codegen cambia firma del método generado (p. ej. `@Nullable`, tipos) | Contrastar la firma generada de `getAllProducts` con la del patrón antes de escribir el controller; ajustar el hand-written a la interfaz, nunca a la inversa. |
| Seed diverge entre changeset y aserciones e2e | Única fuente del contenido del seed: changeset `006` (S2 en `requirements.md`); las aserciones e2e derivan de ella. |
| Orden de la respuesta asumido en aserciones (mal hábito heredado de `BrandControllerE2ETest`) | Aserciones con matchers orden-agnósticos (`hasSize`/`hasItem`). |
| Colisión de ids de changeset si otro agente añade `006` a la vez | El id `006-create-products-table` es consecutivo al último (`005-*`); asígnalo en exclusiva al implementer de la subtarea T5/C1 (una sola persona). |
| H2 sin datos si Liquibase falla silenciosamente | El e2e verifica tamaño 3 del catálogo; un fallo de seed rompe el test (fail-fast). |
