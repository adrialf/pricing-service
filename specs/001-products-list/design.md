# Design — GET /api/products (listado de productos)

> Replica 1:1 del enfoque del listado `GET /api/brands`, con la superficie mínima para el alcance (sin `id`/`active`, sin `findById`, sin test repository).

## 1. Enfoque general

La feature es un clon estructural de `GET /api/brands`: cambia el recurso y su ventana de filtrado (solo `q` sobre `name`/`description`), no el patrón. Cadena completa:

```
ProductsApi (generada) → ProductController (adapter input)
  → ProductRestMapper (query params → GetProductsQuery)
  → GetProductsUseCase / GetProductsService (application)
    → ProductRepository (port output) ← implementado por ProductRepositoryAdapter (adapter output)
      → SpringDataProductRepository → ProductEntity → ProductPersistenceMapper → Product (domain)
```

El filtrado se hace en memoria en el service de aplicación (como `GetBrandsService`); el repo Spring Data es un `JpaRepository` simple.

## 2. Contrato OpenAPI (cambio primero, codegen después)

En `src/main/resources/rest/openapi.yaml`, nuevo path (después de `/api/brands`) y nuevo schema en `components`:

```yaml
  /api/products:
    get:
      summary: Retrieve all products.
      operationId: getAllProducts
      tags:
        - Products
      parameters:
        - name: q
          in: query
          required: false
          description: Filter products by name or description (case-insensitive partial match).
          schema:
            type: string
          example: "premium"
      responses:
        '200':
          description: Successful retrieval of products (empty array when no match).
          content:
            application/json:
              schema:
                type: array
                items:
                  $ref: '#/components/schemas/ProductResponse'
        '500':
          description: Internal server error.
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/ErrorResponse'

    ProductResponse:
      type: object
      required:
        - id
        - name
      properties:
        id:
          type: integer
          format: int64
          description: Product identifier.
          example: 35455
        name:
          type: string
          description: Commercial name of the product.
          example: "Standard Product"
        description:
          type: string
          description: Product description.
          example: "Seed product referenced by prices"
```

- Tag nuevo `Products` (paralelo a `Brands`) → genera `ProductsApi` en `...infrastructure.adapter.input.rest.api` gracias a `useTags`.
- Con `skipDefaultInterface`, `ProductController` implementa la interfaz generada igual que `BrandController implements BrandsApi`.
- `description` no es required (columna nullable en BD → campo opcional en DTO).
- 400 no se declara: no hay parámetros cuya validez esté sujeta a restricción (solo un string opcional); igual que en `/api/brands`.
- Regeneración: `./mvnw clean compile`. Marcar la nueva interfaz generada en `target/generated/openapi/` como NO editable.

## 3. Dominio (`domain/model/Product.java`)

POJO puro, clonado del estilo `Brand.java` (constructores finales, getters, `equals`/`hashCode`/`toString`, sin anotaciones):

```java
public class Product {
    private final Long id;
    private final String name;
    private final String description;
    // ctor + getters + equals/hashCode/toString
}
```

- No hay `active` ni `code` — superficie mínima del elicit (id + name + description).
- Sin invariantes de negocio más allá de inmutabilidad (catálogo de solo lectura).

## 4. Application (puertos + service)

- `application/port/input/GetProductsUseCase.java`

```java
public interface GetProductsUseCase {
    List<Product> getAllProducts(GetProductsQuery query);
}
```

- `application/port/input/GetProductsQuery.java` — query inmutable con Builder (clon estilo `GetBrandsQuery`, que es una clase, no un record de Java), único campo `q`.

- `application/port/output/ProductRepository.java` — puerto de salida:

```java
public interface ProductRepository {
    List<Product> findAll();
}
```
(Sin `findById`: ver supuesto S3 in `requirements.md`.)

- `application/service/GetProductsService.java` — implementa el caso de uso; filtrado en memoria idéntico a `GetBrandsService`:

```java
public class GetProductsService implements GetProductsUseCase {
    private final ProductRepository productRepository;

    @Override
    public List<Product> getAllProducts(GetProductsQuery query) {
        return productRepository.findAll().stream()
                .filter(product -> matchesQuery(product, query.getQ()))
                .toList();
    }

    private boolean matchesQuery(Product product, String q) {
        if (q == null || q.isBlank()) {
            return true;
        }
        String normalizedQuery = q.trim().toLowerCase(Locale.ROOT);
        return containsIgnoreCase(product.getName(), normalizedQuery)
                || containsIgnoreCase(product.getDescription(), normalizedQuery);
    }

    private boolean containsIgnoreCase(String value, String normalizedQuery) {
        return value != null && value.toLowerCase(Locale.ROOT).contains(normalizedQuery);
    }
}
```
- `q` null/blank → sin filtro (FR-3/CA-5). `description` null no produce NPE (CA-7).

## 5. Infrastructure — adapter input (REST)

- `infrastructure/adapter/input/rest/ProductController.java`:

```java
@RestController
public class ProductController implements ProductsApi {
    private final GetProductsUseCase getProductsUseCase;
    private final ProductRestMapper restMapper;

    public ProductController(GetProductsUseCase getProductsUseCase, ProductRestMapper restMapper) { ... }

    @Override
    public ResponseEntity<List<ProductResponse>> getAllProducts(@Nullable String q) {
        GetProductsQuery query = GetProductsQuery.builder().q(q).build();
        List<Product> domainProducts = getProductsUseCase.getAllProducts(query);
        return ResponseEntity.ok(restMapper.toResponse(domainProducts));
    }
}
```

- `infrastructure/adapter/input/rest/ProductRestMapper.java` — `@Component`, `Product → ProductResponse` (set id/name/description) y sobrecarga `toResponse(List<Product>)` (patrón `BrandRestMapper`).
- `ProductResponse` (DTO con setters) vive en `target/generated/openapi/...rest.dto` — nunca editable a mano.

## 6. Infrastructure — adapter output (persistencia)

- `infrastructure/adapter/output/persistence/ProductEntity.java` — `@Entity @Table(name = "PRODUCTS")`, campos `ID BIGINT PK`, `NAME VARCHAR(255) NOT NULL`, `DESCRIPTION VARCHAR(500)` nullable. `equals/hashCode/toString` estilo `BrandEntity`.
- `SpringDataProductRepository.java` — `extends JpaRepository<ProductEntity, Long>` (sin métodos derivados; sin test `@Tag("repository")`, S5).
- `ProductPersistenceMapper.java` — `@Component`, `toDomain(entity)` / `toEntity(domain)` (patrón `BrandPersistenceMapper`).
- `ProductRepositoryAdapter.java` — `@Repository` que implementa `ProductRepository`; `findAll()` mapea entidades a dominio con el mapper (patrón `BrandRepositoryAdapter`).

## 7. Configuración (wiring)

`infrastructure/config/BeanConfiguration.java`: añadir bean (mismo patrón que `getBrandsUseCase`):

```java
@Bean
public GetProductsUseCase getProductsUseCase(ProductRepository productRepository) {
    return new GetProductsService(productRepository);
}
```

Los mappers y los adapters ya son beans por estereotipos (`@Component`/`@Repository`); el `@RestController` se detecta por escaneo.

## 8. Liquibase (única autoridad de esquema/seed)

- Nuevo fichero `src/main/resources/db/changelog/changes/006-create-products-table.xml`, siguiendo el patrón de `004-create-brands-table.xml` (tabla + seed en el mismo changeset, con `rollback`):

```xml
<changeSet id="006-create-products-table" author="adrialf">
    <sql>
        CREATE TABLE PRODUCTS (
            ID BIGINT PRIMARY KEY,
            NAME VARCHAR(255) NOT NULL,
            DESCRIPTION VARCHAR(500)
        );

        INSERT INTO PRODUCTS (ID, NAME, DESCRIPTION) VALUES
        (35455, 'Standard Product', 'Seed product referenced by prices'),
        (1, 'Basic Product', 'Entry-level product'),
        (2, 'Premium Product', 'High-end product');
    </sql>
    <rollback>
        DROP TABLE PRODUCTS;
    </rollback>
</changeSet>
```

- Añadir include en `db.changelog-master.xml` después de `005-add-brand-fk-to-prices.xml`:

```xml
<include file="db/changelog/changes/006-create-products-table.xml"/>
```

- El id 35455 mantiene coherencia con las filas de `PRICES` ya sembradas (productId 35455).
- Arranque: Liquibase crea y puebla la tabla; JPA solo mapea la entidad (no interviene en el DDL, `ddl-auto=none`).

## 9. Testing (réplicas del patrón, por tag)

| Tag | Clase nueva | Replicado de | Cobertura |
|---|---|---|---|
| `unit` | `application/service/GetProductsServiceTest.java` | `GetBrandsServiceTest` | sin filtro → todo; `q` match parcial case-insensitive sobre name; match solo por description; sin matches → vacío; `q` blank → sin filtro; null-safe con description null. Mock de `ProductRepository`. |
| `e2e` | `infrastructure/adapter/input/rest/ProductControllerE2ETest.java` | `BrandControllerE2ETest` | `@SpringBootTest(RANDOM_PORT)` + RestAssured: 200 con tamaño 3 y contenido del seed; `q=premium` → 1 item; `q=unknown` → `[]`; `q` vacío → 3 items. |

⚠️ **Detalle vs. brands**: como R5 no garantiza orden, las aserciones e2e NO deben depender de índices (`[0].id`) como hace `BrandControllerE2ETest`; se usarán matchers orden-agnósticos (`hasSize`, `hasItem`, `containsInAnyOrder`/verificación por pares id/name/description). Sobre datos concretos del seed: dependen de S2 en `requirements.md`.

- No se añade test `@Tag("repository")` (S5). No se tocan tests/endpoint de brands ni de prices.

## 10. CHANGELOG

Entrada bajo `## [Unreleased] → ### Added` en `CHANGELOG.md`: `REST endpoint GET /api/products to list the product catalog with an optional free-text filter q (case-insensitive partial match).`

## 11. Alternativas descartadas

| Alternativa | Porqué descartada |
|---|---|
| Filtrar `q` con query derivada en Spring Data (p. ej. `findByNameContainingIgnoreCase`) | El patrón brands filtra en memoria en el service; mantener homogeneidad y testabilidad unit pura; el catálogo es pequeño (demo). |
| Incluir `findById` en el port + entity, replicando `BrandRepository` | Sin consumidor; detalle por id fuera de alcance; YAGNI (el `findById` de brands está hoy sin uso). |
| Test `@Tag("repository")` para el Spring Data repo | Repo sin métodos derivados; no hay lógica propia que verificar. |
| Changeset liquibase separado para el seed (`007-insert-products.xml`) | La convención observada en el repo para el bootstrap de brands es tabla+seed en el mismo changeset; menos entradas en el changelog y rollback atómico. |
| Incluir `code`/`active` u otros campos en `ProductResponse` | El elicit y el alcance fijan id + name + description; añadir campos obligaría a decisiones de esquema/seed adicionales no solicitadas. |
| Reusar la entity/mapper de Brand para products | Entidades y conceptos de dominio distintos (Brand no cambia según la semilla); acoplarlos viola el modelo DDD pretendido. |
