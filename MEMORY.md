# MEMORY.md

Memoria a largo plazo y guía de contexto para agentes de IA que trabajen en este repositorio.
Mantén este fichero actualizado cuando cambien el stack, las convenciones o el estado del proyecto.

---

## 1. Stack tecnológico y versiones

**Lenguaje y runtime**
- Java 25 (`java.version` en `pom.xml`). Build con Maven wrapper (`./mvnw`).

**Framework y librerías**
- Spring Boot 4.1.0 (`spring-boot-starter-parent`): Web, Data JPA, Liquibase, Test, Data JPA Test.
- Springdoc OpenAPI `3.0.3` (Swagger UI + `/api-docs`).
- OpenAPI Generator `7.23.0` (plugin Maven, generador Spring: `interfaceOnly`, `useSpringBoot4`, `useJakartaEe`, `useTags`, `skipDefaultInterface`). Salida: `target/generated/openapi/`.
- Jackson `jackson-databind-nullable` `0.2.10`.
- H2 en memoria (`jdbc:h2:mem:pricingdb;DB_CLOSE_DELAY=-1`), Liquibase para el esquema (`src/main/resources/db/changelog/db.changelog-master.xml`), `hibernate.ddl-auto=none`.
- Tests: JUnit 5, Mockito (vía starter), RestAssured `6.0.0`.

**Infraestructura / tooling**
- Docker (`./mvnw spring-boot:build-image` → imagen `pricing:<versión>`) + `docker-compose.yml` (mapea el 8080).
- Colección Postman: `src/test/resources/postman/pricing-service.postman_collection.json`.
- Sin OpenShift, Kafka ni Redis todavía — no hay nada de eso en el stack actual.

**Datos de la app**
- Artifact: `com.altiacompany:pricing`, versión `1.0.1-SNAPSHOT`.
- Puerto 8080. Endpoints: `GET /api/prices/active` y el de brands; Swagger `/swagger-ui.html`; consola H2 `/h2-console`.

---

## 2. Convenciones de arquitectura (estrictas)

- **Arquitectura Hexagonal (obligatoria)**, fuente de verdad: `docs/constitution.md` (suprema, inmutable — no modificar salvo instrucción explícita del propietario humano).
- Organización de paquetes: `com.altiacompany.pricing`
  - `domain/` — modelo de negocio puro (`model/`, `exception/`, `service/`). **Ni anotaciones de Spring/JPA ni dependencias de frameworks. Nunca.** Los servicios de dominio (p. ej. `PriceSelector`) contienen las reglas de negocio.
  - `application/` — `port/input` (casos de uso + query records), `port/output` (contratos de repositorios), `service/` (implementaciones de los casos de uso). Los puertos son interfaces; sin detalles tecnológicos.
  - `infrastructure/` — `adapter/input/rest` (controllers, mappers REST, `exception/GlobalExceptionHandler`), `adapter/output/persistence` (entidades JPA, mappers, repositorios Spring Data, adapters de repositorio que implementan los puertos de salida), `config/` (wiring de beans).
- **Regla de dependencias:** infrastructure → application → domain. El dominio no depende de nada externo.
- **API-first:** `src/main/resources/rest/openapi.yaml` es la única fuente de verdad de los contratos REST. El código generado en `target/generated/openapi/**` nunca se edita a mano; se cambia la spec y se regenera con `./mvnw clean compile`.
- **Spec Driven Development (SDD):** leer `docs/constitution.md` antes de planificar/implementar; las specs viven en `specs/<feature>/` (requirements, design, plan, tasks) y se escriben antes de tocar código.
- **Fechas y tiempo:** almacenadas como `TIMESTAMP WITH TIME ZONE`, procesadas como `java.time.Instant` UTC; toda entrada de fecha de la API se normaliza a UTC.
- **Desempate en la selección de precio:** a igualdad de prioridad máxima, devolver el primer registro de la colección recuperada (comportamiento documentado y determinista).
- `spring.jpa.open-in-view=false`; `show-sql=true` (H2 local).

---

## 3. Decisiones de diseño y trade-offs

| Decisión | Justificación / nota |
|---|---|
| Hexagonal sobre en capas | Mantiene las reglas de selección de precio puras y testables sin contexto de Spring. |
| API-first + codegen | Los errores de contrato se detectan en build; los DTO/controllers se mantienen sincronizados con la spec. |
| H2 + Liquibase | Simplicidad local/demo; `ddl-auto=none` deja a Liquibase como única autoridad del esquema. |
| Desempate determinista por prioridad | "Primer registro de la colección recuperada" favorece la simplicidad frente a criterios de ordenación extra; documentado como comportamiento intencionado. |
| `skipDefaultInterface` en el codegen | Los controllers implementan las interfaces generadas con mapeo explícito (hand-written `BrandController`/`PriceController`), manteniendo el pegamento de framework fuera de los casos de uso. |
| Separación de tests por tags | Permite ejecuciones enfocadas (`-Dgroups`) y evita el contexto pesado de e2e en builds de unit. |

---

## 4. Convenciones de Git y desarrollo

- Ramas: `master` es la rama estable/por defecto; el trabajo actual ocurre en `develop`.
- Mensajes de commit: `PREFIJO - Descripción corta` (imperativo, sin cuerpo). Prefijos observados: `FUNCT` (features/comportamiento), `RESTRUCT` (movimientos/renombres/organización). Mantén este estilo.
- CHANGELOG: [Keep a Changelog](https://keepachangelog.com) + [SemVer](https://semver.org); documentar cambios relevantes bajo `## [version] - YYYY-MM-DD` (ver `CHANGELOG.md`).
- Verificación antes de dar por terminado el trabajo: `./mvnw clean test`; por tag `./mvnw test -Dgroups=unit|repository|e2e`; por clase `./mvnw test -Dtest=NombreClase`.
- Los tests van etiquetados: `@Tag("unit")` (puro), `@Tag("repository")` (`@DataJpaTest`), `@Tag("e2e")` (`@SpringBootTest` + RestAssured).
- Nunca commitear secretos; solo commitear cuando el usuario lo pida explícitamente.

---

## 5. Estado actual y tareas pendientes

- **Implementado:**
  - Caso de uso de consulta de precio (+ `PriceSelector` en dominio) y listado de brands (soporte de brands añadido, commit `FUNCT - Support to brands`).
  - Adapters REST (Price + Brand), manejador global de excepciones, persistencia H2/Liquibase, Swagger + colección Postman, imagen/compose Docker.
  - Tests: unit (servicios, `PriceSelector`), repository (`SpringDataPriceRepositoryTest`), e2e (controllers de Price + Brand).
- **Estado del proceso:**
  - Flujo SDD definido en `AGENTS.md`; `docs/constitution.md` es el doc normativo; `specs/` es el hogar de las specs (vacío — aún no hay specs de features).
  - Equipos de agentes SDD en `.opencode/agents/`: `coordinador` (primary, orquesta el flujo), `planner`, `implementer`, `reviewer` (subagents).
  - Versión `1.0.1-SNAPSHOT` en `develop`; últimos commits: adopción de SDD y reestructuración de la carpeta de agents.
- **Abiertas / candidatas (a fecha de redacción):**
  - Aún no hay specs de features en `specs/` — las próximas features deberían empezar ahí.
  - Trabajo posible: tests unitarios directos de los adapters de repositorio (`BrandRepositoryAdapter`/`PriceRepositoryAdapter`), nuevas features vía el flujo SDD (spec → design → plan → tasks → código).
- **No presente:** pipelines de CI (no hay `.github/workflows`), OpenShift, Kafka, Redis — añadir aquí cuando se introduzcan.
