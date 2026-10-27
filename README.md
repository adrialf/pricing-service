# Pricing Service

A Spring Boot RESTful microservice to query applicable product prices based on specific parameters (application date, product ID, and brand ID).

---

## 1. Getting Started

### 1.1. Prerequisites
* **Java Development Kit (JDK) 25**
* **Docker** & **Docker Compose** (optional, for container execution)
* **Postman** (optional, for running integration test collections)

### 1.2. Running Locally
To run the application locally, use the provided Maven wrapper:

```bash
./mvnw spring-boot:run
```

Once the application starts, it will listen on port `8080`.

The active price lookup endpoint is available at `GET /api/prices/active` accepting `applicationDate`, `productId`, and `brandId` as query parameters. For example:

```
http://localhost:8080/api/prices/active?applicationDate=2020-06-14T10:00:00Z&productId=35455&brandId=1
```

---

## 2. Technical Stack
* **Java 25**
* **Spring Boot 4.1.0** (Spring Web, Spring Data JPA)
* **H2 Database** (In-memory)
* **OpenAPI Generator** (Maven plugin for code generation)
* **Springdoc OpenAPI** (Swagger UI Integration)
* **RestAssured** (E2E Integration Testing)
* **Mockito & JUnit 5** (Unit Testing)
* **Liquibase** (Database migrations)

---

## 3. Architecture

The project strictly follows **Hexagonal Architecture** principles to decouple core business logic from framework-specific technologies and databases.

* `domain`: Pure business model layer. Has zero external dependencies (no Spring Framework or JPA annotations).
    * `model`: Domain objects representing business entities.
    * `exception`: Custom domain/business-specific exceptions.
    * `service`: Pure domain services containing business rules that operate on domain entities.
* `application`: Business rules and orchestration layer.
    * `port`: Hexagonal boundary contracts (inward use cases, outward service provider interfaces).
    * `service`: Implementation of the use cases, coordinating domain entities and ports.
* `infrastructure`: Adapter and configuration layer.
    * `adapter`: Concrete implementations of ports (REST, JPA).
    * `config`: Spring configuration classes for Bean instantiations and third-party setups.

---

## 4. Database Console (H2) & Migrations

The application uses an in-memory H2 database preloaded with sample prices.
* **Console URL**: [http://localhost:8080/h2-console](http://localhost:8080/h2-console) (Note: To access the H2 console interface, you must manually add the `spring-boot-h2console` dependency to your pom.xml).
* **JDBC URL**: `jdbc:h2:mem:pricingdb`
* **Username**: `sa`
* **Password**: *(leave blank)*

Database migrations, table schema creation, and sample data seeding are fully managed by **Liquibase** on startup. Changelog migration scripts are located under `src/main/resources/db/changelog/`.

**Timezone Assumption**: To ensure timezone-agnostic and robust executions across environments, all pricing configuration dates (`startDate`, `endDate`) are stored in the database as `TIMESTAMP WITH TIME ZONE` and processed internally as UTC (`java.time.Instant`). All client queries received through the REST API are automatically normalized to UTC before querying.

**Priority Collision Resolution**: In the event that multiple active price records share the same maximum priority value for the queried application date, product, and brand, the service resolves it by selecting the first price record encountered in the retrieved collection.

---

## 5. API Specification

### 5.1. API-First Development
This project adopts an **API-First** development approach. The API contract is defined first using the OpenAPI 3.0 specification before any controller implementation begins.
* **Specification File**: `src/main/resources/rest/openapi.yaml`

### 5.2. Code Generation
The Java REST controllers interfaces and request/response DTOs are automatically generated from the OpenAPI specification file during the Maven lifecycle.
* **Maven Plugin**: `openapi-generator-maven-plugin`
* **Triggering Generation**: Code is automatically generated on compile time. You can manually force generation using `./mvnw clean compile`.
* **Output Location**: Generated classes are placed in `target/generated-sources/openapi/` including REST controller interfaces defining endpoint signatures and request/response DTO models.

### 5.3. Accessing Swagger UI
Once the service is running, you can access:
* **Interactive Swagger UI**: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
* **OpenAPI Raw Specification (JSON)**: [http://localhost:8080/api-docs](http://localhost:8080/api-docs)

---

## 6. Testing

The project categorizes tests using JUnit 5 `@Tag` annotations to separate execution concerns:
* `@Tag("unit")`: Pure unit tests for application and domain services (no Spring context).
* `@Tag("repository")`: Database slice tests targeting persistence mappings and queries (using `@DataJpaTest`).
* `@Tag("e2e")`: End-to-end API tests booting the entire Spring Boot container (using `@SpringBootTest`).

### 6.1. Running All Tests
To run the entire test suite combined:

```bash
./mvnw clean test
```

### 6.2. Running a Specific Test
To run a single test class:

```bash
./mvnw test -Dtest=PriceControllerE2ETest
```

Or to run a specific test method within a class:

```bash
./mvnw test -Dtest=PriceControllerE2ETest#test1_RequestAt202006141000_Product35455_Brand1
```

### 6.3. Running Tests by Tag
To run tests belonging to a specific tag group:

**Unit Tests only**:

```bash
./mvnw test -Dgroups=unit
```
  
**Repository Slice Tests only**:

```bash
./mvnw test -Dgroups=repository
```
  
**E2E API Tests only**:

```bash
./mvnw test -Dgroups=e2e
```

### 6.4. Running Postman Collection Tests
A ready-to-use Postman collection containing the E2E test scenarios with automated assertion scripts is located at:

```
src/test/resources/postman/pricing-service.postman_collection.json
```

**How to run**:
1. Start the application (`./mvnw spring-boot:run`).
2. Open **Postman** and click **Import**.
3. Select `src/test/resources/postman/pricing-service.postman_collection.json`.
4. Run individual requests or execute the full suite using Postman **Collection Runner** against `http://localhost:8080`.

---

## 7. Containerization (Docker)

The project includes containerization support via Docker. You can build the application image and run it locally.

### 7.1. Building the OCI Image
The project uses **Cloud Native Buildpacks** to compile and build an optimized, layered, secure docker image without maintaining a manual `Dockerfile`.

```bash
./mvnw spring-boot:build-image
```

This will produce the image `pricing:<VERSION>` locally (where `<VERSION>` matches the version configured in your pom.xml).

### 7.2. Running with Docker Compose
To simplify orchestration, a `docker-compose.yml` file is provided in the project root directory. You can orchestrate and start the containerized microservice by running:

```bash
# Start container in detached mode
docker compose up -d

# Verify logs
docker compose logs -f

# Stop the container
docker compose down
```
