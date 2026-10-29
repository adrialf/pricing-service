package com.altiacompany.pricing.infrastructure.adapter.input.rest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;

import java.util.Map;

import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import io.restassured.RestAssured;

@Tag("e2e")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProductControllerE2ETest {

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        RestAssured.basePath = "/api/products";
    }

    @Test
    void shouldReturnAllProducts() {
        // @formatter:off
        given()
        .when()
                .get()
        .then()
                .statusCode(200)
                .body("$", hasSize(3))
                .body("$", hasItem(seedProduct(35455, "Standard Product", "Seed product referenced by prices")))
                .body("$", hasItem(seedProduct(1, "Basic Product", "Entry-level product")))
                .body("$", hasItem(seedProduct(2, "Premium Product", "High-end product")));
        // @formatter:on
    }

    @Test
    void shouldFilterProductsByQuery() {
        // @formatter:off
        given()
                .queryParam("q", "premium")
        .when()
                .get()
        .then()
                .statusCode(200)
                .body("$", hasSize(1))
                .body("$", hasItem(seedProduct(2, "Premium Product", "High-end product")));
        // @formatter:on
    }

    @Test
    void shouldReturnEmptyListWhenNoProductsMatchQuery() {
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

    @Test
    void shouldReturnAllProductsWhenQueryIsEmpty() {
        // @formatter:off
        given()
                .queryParam("q", "")
        .when()
                .get()
        .then()
                .statusCode(200)
                .body("$", hasSize(3))
                .body("$", hasItem(seedProduct(35455, "Standard Product", "Seed product referenced by prices")))
                .body("$", hasItem(seedProduct(1, "Basic Product", "Entry-level product")))
                .body("$", hasItem(seedProduct(2, "Premium Product", "High-end product")));
        // @formatter:on
    }

    /**
     * Order-agnostic matcher for a single product entry of the JSON array, pairing
     * its full content (id, name, description) without relying on collection indexes
     * (required by design.md section 9: the products listing does not guarantee order).
     */
    private static Matcher<Map<String, Object>> seedProduct(int id, String name, String description) {
        return allOf(
                Matchers.<String, Object>hasEntry("id", id),
                Matchers.<String, Object>hasEntry("name", name),
                Matchers.<String, Object>hasEntry("description", description));
    }
}
