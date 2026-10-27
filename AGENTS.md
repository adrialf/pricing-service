# AGENTS.md

## Architecture
- Hexagonal: `domain/` (pure, no Spring/JPA), `application/` (ports/use cases/services), `infrastructure/` (adapters/config).
- API-first: `src/main/resources/rest/openapi.yaml` is the source of truth. `openapi-generator-maven-plugin` (Spring, `interfaceOnly`, `useSpringBoot4`, `useJakartaEe`, `useTags`) generates to `target/generated/openapi/` (`...infrastructure.adapter.input.rest.api`, `...dto`, `...invoker`, `ApiUtil.java`). Regenerate with `./mvnw clean compile`.
- DB: H2 in-mem (`jdbc:h2:mem:pricingdb`), Liquibase changelog `src/main/resources/db/changelog/db.changelog-master.xml`. `spring.jpa.open-in-view: false`, `show-sql: true`.

## Run / Dev
- Local: `./mvnw spring-boot:run` (8080). Endpoint: `GET /api/prices/active?applicationDate,productId,brandId` (UTC normalized). Swagger `/swagger-ui.html`, OpenAPI `/api-docs`. H2 console `/h2-console` (README notes manual `spring-boot-h2console` dep if missing).
- Docker: `./mvnw spring-boot:build-image` → `pricing:<version>`; `docker compose up -d` / `down`.

## Rules
- Dates: stored `TIMESTAMP WITH TIME ZONE`, processed as UTC; API inputs normalized to UTC.
- Priority tie: if multiple active prices share max priority for query → return first record in retrieved collection.
- Generated code: treat `target/generated/openapi/**` as generated (do not hand-edit). Change OpenAPI spec + regenerate.

## Tests
- Tags: `unit` (pure), `repository` (`@DataJpaTest`), `e2e` (`@SpringBootTest` + RestAssured).
- All: `./mvnw clean test`
- By tag: `./mvnw test -Dgroups=unit|repository|e2e`
- Single: `./mvnw test -Dtest=ClassName` or `./mvnw test -Dtest=ClassName#method`
- Postman: `src/test/resources/postman/pricing-service.postman_collection.json` (against live app)
