# Plan: Add /api/brands endpoint

## Goal
Add GET /api/brands to return list of brandIds with registered prices. Follow hexagonal architecture, populate data like prices do, add tests.

## Architecture analysis
- OpenAPI-first: update `src/main/resources/rest/openapi.yaml`, regenerate with `./mvnw clean compile`
- Hexagonal: domain (pure), application (ports/use cases/services), infrastructure (adapters/config)
- Generated code in `target/generated/openapi/` - do not edit
- Data: Liquibase changelog, currently has PRICES table with seed data in `002-insert-initial-prices.xml`
- Brands are stored as BRAND_ID in PRICES table; need distinct brand IDs

## Changes needed

### 1. OpenAPI spec
Add to `src/main/resources/rest/openapi.yaml`:
- Path `/api/brands` GET, operationId `getAllBrands`, tag `Brands`
- Response 200 with schema `BrandListResponse` containing `brandIds` array of int64
- 500 error response

### 2. Regenerate OpenAPI models/APIs
Run `./mvnw clean compile` to generate `BrandsApi` interface and `BrandListResponse` DTO in target.

### 3. Application layer
- Create `GetBrandsUseCase` interface in `application/port/input/` 
- Create `GetBrandsService` in `application/service/` implementing use case
- Add method `List<Long> findAllBrandIds()` to `application/port/output/PriceRepository`

### 4. Infrastructure - Persistence
- Add method to `SpringDataPriceRepository`: `@Query("SELECT DISTINCT p.brandId FROM PriceEntity p ORDER BY p.brandId") List<Long> findAllBrandIds();`
- Implement in `PriceRepositoryAdapter`

### 5. Infrastructure - REST
- Create `BrandController` implementing generated `BrandsApi`
- Add bean in `BeanConfiguration` for use case
- Create mapper if needed (BrandRestMapper) or map directly

### 6. Data seeding
"puebla con datos el sistema al igual que los prices" - prices seeded via Liquibase changesets. Current data has brandId=1. Add another changeset to insert more brand data/prices if needed? Or just ensure it's populated. The task says "populate with data the system like prices do" - follow same pattern as prices (add Liquibase changeset). Maybe add changeset 003 to insert prices for another brand or ensure brands are present. Let us add changeset 003 with additional brand data.

### 7. Tests
Add tests following existing patterns:
- Unit test for service (GetBrandsServiceTest) like GetPriceServiceTest
- Repository test for finding brand IDs (add to SpringDataPriceRepositoryTest or new test)
- E2E test for controller (BrandControllerE2ETest) like PriceControllerE2ETest

## Implementation order
1. Update OpenAPI spec
2. Regenerate to get generated interfaces/DTOs
3. Add persistence method for distinct brand IDs
4. Add output port method
5. Add use case + service
6. Add controller + mapper
7. Add bean configuration
8. Add Liquibase changeset for additional brand data
9. Add tests
10. Run tests
