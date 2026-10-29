# Requirements — GET /api/products (listado de productos)

> Feature 001. Fuente: `specs/001-products-list/feature.md` (semilla elicitada y validada).
> Patrón de referencia: listado `GET /api/brands` existente.

## 1. Historias de usuario

- **US1** — Como consumidor de la API, quiero listar los productos disponibles del catálogo (`GET /api/products`), para descubrir sobre qué productos puedo consultar precios.
- **US2** — Como consumidor de la API, quiero filtrar el listado con un texto libre opcional (`q` en query string), para localizar rápidamente un producto sin recorrer la lista completa.

## 2. Requisitos funcionales

| ID | Requisito |
|---|---|
| FR-1 | Exponer el endpoint `GET /api/products` que responde `200 OK` con el catálogo completo de productos precargados vía seed Liquibase. |
| FR-2 | Aceptar el query param opcional `q` (string): filtro de texto libre de coincidencia parcial (contains) e insensible a mayúsculas/minúsculas sobre los campos textuales del producto (`name`, `description`). |
| FR-3 | Cuando `q` está ausente, vacío o en blanco (`null`/`""`/whitespace), no se filtra: se devuelve el catálogo completo. |
| FR-4 | Cuando no existen productos en el catálogo o ninguno coincide con `q`, responder `200 OK` con lista JSON vacía `[]` (resultado válido, no error). |
| FR-5 | El catálogo es de solo lectura: únicamente el endpoint de listado; sin escritura, sin detalle por id, sin paginación, sin ordenación. |
| FR-6 | La respuesta de cada producto contiene exactamente: `id` (int64), `name` (string), `description` (string, opcional/nullable). |
| FR-7 | No se garantiza el orden de la colección devuelta: se expone tal cual la proporciona la persistencia. |
| FR-8 | El contrato REST se define primero en `src/main/resources/rest/openapi.yaml` (nuevo path `/api/products`, tag `Products`, schema `ProductResponse`) y el código se regenera con `./mvnw clean compile`. Nada en `target/generated/openapi/**` se edita a mano. |
| FR-9 | Liquibase es la única autoridad del esquema y del seed: nuevo changeset que crea la tabla `PRODUCTS` y precarga el catálogo (patrón `004-create-brands-table.xml`); incluirlo en `db.changelog-master.xml`. |

## 3. Reglas de negocio (R1–R5 de la semilla)

- **R1**: el listado devuelve el catálogo completo de productos precargado.
- **R2**: filtro opcional `q`: coincidencia parcial (contains) e insensible a mayúsculas sobre `name` y `description` (los únicos campos textuales del producto).
- **R3**: sin productos o sin coincidencias → 200 con lista vacía.
- **R4**: solo lectura; los productos provienen únicamente del seed Liquibase.
- **R5**: sin orden garantizado en la respuesta.

## 4. Casos y casos borde (criterios de aceptación)

| CA-ID | Caso | Resultado esperado |
|---|---|---|
| CA-1 | `GET /api/products` sin filtro, con catálogo precargado | 200 con todos los productos del seed; cada item con `id`, `name`, `description`. |
| CA-2 | `GET /api/products?q=<fragmento en minúsculas>` donde el fragmento está en la primera mitad del `name` en mayúsculas de un producto | 200 con solo el/los productos coincidentes (case-insensitive parcial). |
| CA-3 | `GET /api/products?q=<fragmento>` presente solo en la `description` de un producto | 200 con solo ese producto (filter también sobre `description`). |
| CA-4 | `GET /api/products?q=<texto sin coincidencias>` | 200 con `[]`. |
| CA-5 | `GET /api/products?q=` (vacío o solo espacios) | 200 con el catálogo completo (R2/FR-3). |
| CA-6 | Catálogo vacío (base limpia sin seed aplicable) | 200 con `[]` (unit-level: repo devuelve lista vacía). |
| CA-7 | `description` null en un producto + `q` presente | El producto no se descarta por NPE; se compara solo con campos no nulos (sobre `name`). |

## 5. Fuera de alcance (explícito)

- CRUD de productos y cambio de estado.
- Detalle por id (`GET /api/products/{id}`).
- Paginación u ordenación de resultados.
- Vínculos product↔price o product↔brand en la respuesta (`GET /api/prices/active` no cambia).
- Campos adicionales del producto (código, estado/active, etc. — solo id + name + description).

## 6. Supuestos explícitos (no respondidos por el usuario; decisión del planner documentada)

- **S1** — El seed de productos se incluye en el mismo changeset que crea la tabla (`006-create-products-table.xml`), replicando la convención observada en `004-create-brands-table.xml` (que crea `BRANDS` y su seed juntos).
- **S2** — Contenido del seed propuesto (3 filas deterministas con ids distintos, verificables en e2e):
  - `(35455, 'Standard Product', 'Seed product referenced by prices')` — id coherente con la fila de precios existente (productId 35455 en el seed `002`).
  - `(1, 'Basic Product', 'Entry-level product')`
  - `(2, 'Premium Product', 'High-end product')`
  (Si el usuario prefiere otros valores del catálogo, solo cambia ese changeset y los literales de aserción e2e; nada estructural.)
- **S3** — El puerto de salida `ProductRepository` expone solo `findAll()`: no se replica el `findById` del `BrandRepository` porque no tiene consumidor hoy (brands tampoco lo usa) y el detalle por id está fuera de alcance (YAGNI).
- **S4** — El filtrado `q` ocurre en el service de aplicación en memoria sobre `findAll()` (idéntico a `GetBrandsService`), sin queries derivadas en Spring Data.
- **S5** — Sin test `@Tag("repository")` para products: `SpringDataProductRepository` es un `JpaRepository` vacío sin métodos derivados (no hay nada que testear más allá de lo que cubre e2e; en brands tampoco existe tal test).

## 7. Coherencia con la constitución

- Hexagonal pura (domain sin Spring/JPA), API-first, Liquibase como única autoridad de esquema/seed, `ddl-auto=none`, `open-in-view=false`. Sin fechas ni reglas de precio implicadas → los principios UTC/desempate no aplican a esta feature.
