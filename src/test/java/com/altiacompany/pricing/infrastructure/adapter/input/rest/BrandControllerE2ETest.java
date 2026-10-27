package com.altiacompany.pricing.infrastructure.adapter.input.rest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import io.restassured.RestAssured;

@Tag("e2e")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BrandControllerE2ETest {

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        RestAssured.basePath = "/api/brands";
    }

    @Test
    void shouldReturnAllBrands() {
        // @formatter:off
        given()
        .when()
                .get()
        .then()
                .statusCode(200)
                .body("$", hasSize(3))
                .body("[0].id", equalTo(1))
                .body("[0].name", equalTo("Renault"))
                .body("[0].code", equalTo("RENAULT"))
                .body("[0].active", equalTo(true))
                .body("[1].id", equalTo(2))
                .body("[1].name", equalTo("Opel"))
                .body("[1].code", equalTo("OPEL"))
                .body("[1].active", equalTo(true))
                .body("[2].id", equalTo(3))
                .body("[2].name", equalTo("Skoda"))
                .body("[2].code", equalTo("SKD"))
                .body("[2].active", equalTo(true));
        // @formatter:on
    }

    @Test
    void shouldFilterBrandsById() {
        // @formatter:off
        given()
                .queryParam("id", 1)
        .when()
                .get()
        .then()
                .statusCode(200)
                .body("$", hasSize(1))
                .body("[0].id", equalTo(1))
                .body("[0].name", equalTo("Renault"));
        // @formatter:on
    }

    @Test
    void shouldFilterBrandsByQuery() {
        // @formatter:off
        given()
                .queryParam("q", "renault")
        .when()
                .get()
        .then()
                .statusCode(200)
                .body("$", hasSize(1))
                .body("[0].id", equalTo(1))
                .body("[0].name", equalTo("Renault"));
        // @formatter:on
    }

    @Test
    void shouldReturnEmptyListWhenNoBrandsMatchFilters() {
        // @formatter:off
        given()
                .queryParam("q", "unknown")
        .when()
                .get()
        .then()
                .statusCode(200)
                .body("$", hasSize(0));
        // @formatter:on
    }
}
