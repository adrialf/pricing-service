# AGENTS.md

## Descripción del proyecto
**Pricing Service**: microservicio RESTful (Spring Boot 4, Java 25) para consultar el precio aplicable de un producto según fecha de aplicación, ID de producto e ID de marca (`GET /api/prices/active`). Stack: H2 en memoria + Liquibase, OpenAPI Generator, Springdoc (Swagger), RestAssured. Dockerizado (`pricing:<versión>`).

## SDD (Desarrollo Dirigido por Specs)
- Este proyecto sigue SDD. Antes de planificar o implementar cualquier feature/cambio, lee `docs/constitution.md` (constitución del proyecto, principios innegociables) y aplícala.
- Las specs viven en `specs/`: crea un subdirectorio por feature con el nombre precedido de un número incremental de 3 dígitos (p. ej. `specs/<NNN>-<feature>/` → `specs/001-products-list/`) y escribe allí las specs (requirements, design, plan, tasks) antes de tocar código. El número es consecutivo al último existente. Mantén las specs actualizadas a medida que evoluciona la implementación.

## Arquitectura
- Hexagonal: `domain/` (puro, sin Spring/JPA), `application/` (ports/casos de uso/servicios), `infrastructure/` (adapters/config).
- API-first: `src/main/resources/rest/openapi.yaml` es la fuente de verdad. `openapi-generator-maven-plugin` (Spring, `interfaceOnly`, `useSpringBoot4`, `useJakartaEe`, `useTags`) genera en `target/generated/openapi/` (`...infrastructure.adapter.input.rest.api`, `...dto`, `...invoker`, `ApiUtil.java`). Regenera con `./mvnw clean compile`.
- BD: H2 en memoria (`jdbc:h2:mem:pricingdb`), changelog de Liquibase `src/main/resources/db/changelog/db.changelog-master.xml`. `spring.jpa.open-in-view: false`, `show-sql: true`.

## Ejecución / Dev
- Local: `./mvnw spring-boot:run` (8080). Endpoint: `GET /api/prices/active?applicationDate,productId,brandId` (normalizado a UTC). Swagger `/swagger-ui.html`, OpenAPI `/api-docs`. Consola H2 `/h2-console`.
- Docker: `./mvnw spring-boot:build-image` → `pricing:<versión>`; `docker compose up -d` / `down`.

## Reglas
- Fechas: almacenadas como `TIMESTAMP WITH TIME ZONE`, procesadas en UTC; entradas de la API normalizadas a UTC.
- Empate de prioridad: si varias fechas activas comparten la prioridad máxima para la consulta → devolver el primer registro de la colección recuperada.
- Código generado: tratar `target/generated/openapi/**` como generado (no editar a mano). Cambia la spec OpenAPI + regenera.
- Commits: nunca automáticos. Ningún commit por agentes; el commit es siempre un paso manual del propietario humano. Solo commitear si el usuario lo pide explícitamente.

## Tests
- Tags: `unit` (puro), `repository` (`@DataJpaTest`), `e2e` (`@SpringBootTest` + RestAssured).
- Todos: `./mvnw clean test`
- Por tag: `./mvnw test -Dgroups=unit|repository|e2e`
- Individual: `./mvnw test -Dtest=NombreClase` o `./mvnw test -Dtest=NombreClase#metodo`
- Postman: `src/test/resources/postman/pricing-service.postman_collection.json` (contra la app en vivo)
