# CONSTITUCIÓN (Suprema e Inmutable)

Este documento es el marco normativo supremo del repositorio pricing-service. Todos los agentes, contribuidores y procesos automatizados DEBEN cumplirlo. Los conflictos se resuelven en favor de esta Constitución.

## 1. Principios de arquitectura
- **La Arquitectura Hexagonal es obligatoria.** `domain/` DEBE permanecer puro (sin Spring, JPA ni anotaciones/dependencias de frameworks). `application/` define los puertos y casos de uso; `infrastructure/` solo aporta adapters/config.
- **Regla de dependencias:** las dependencias apuntan hacia dentro (infrastructure → application → domain). El dominio tiene cero dependencias hacia fuera.
- **Fronteras:** los puertos en `application/port/` son contratos; las implementaciones viven en `application/service/` o en adapters en `infrastructure/`.

## 2. API-first
- **Fuente de verdad:** `src/main/resources/rest/openapi.yaml`. Todos los contratos REST DEBEN definirse allí primero.
- **El código generado es sagrado:** controllers/interfaces/DTOs generados por `openapi-generator-maven-plugin` en `target/generated/openapi/` NO DEBEN editarse manualmente. Regenerar con `./mvnw clean compile` tras cambios en la spec.

## 3. Datos y tiempo
- **UTC en todo sitio:** los precios se almacenan como `TIMESTAMP WITH TIME ZONE`, procesados como `java.time.Instant` UTC. Las consultas de la API DEBEN normalizarse a UTC.
- **Determinismo:** a igualdad de prioridad máxima, la selección DEBE devolver el primer registro encontrado en la colección recuperada (comportamiento documentado).

## 4. Persistencia
- **Gestión de esquema:** Liquibase es el único mecanismo para esquema/migraciones/seeds (`src/main/resources/db/changelog/`). `spring.jpa.hibernate.ddl-auto=none`. `spring.jpa.open-in-view=false`.

## 5. Disciplina de testing
- **Etiquetado:** usar `@Tag("unit")`, `@Tag("repository")`, `@Tag("e2e")` apropiadamente. Unit = puro, sin contexto de Spring; repository = `@DataJpaTest`; e2e = `@SpringBootTest` completo con RestAssured.
- **Verificación:** preferir ejecuciones enfocadas (`-Dgroups`, `-Dtest`) antes que la suite completa.

## 6. Inmutabilidad y enmienda
- **Suprema:** esta Constitución prevalece sobre notas del README, convenciones o preferencias de agentes.
- **Inmutable:** este fichero NO DEBE ser modificado por agentes salvo instrucción explícita y unánime del propietario humano con justificación escrita. No se permiten violaciones.
