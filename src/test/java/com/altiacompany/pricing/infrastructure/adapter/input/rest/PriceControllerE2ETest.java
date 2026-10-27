package com.altiacompany.pricing.infrastructure.adapter.input.rest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import io.restassured.RestAssured;

@Tag("e2e")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PriceControllerE2ETest {

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        RestAssured.basePath = "/api/prices/active";
    }

    @Test
    void test1_RequestAt202006141000_Product35455_Brand1() {
        // @formatter:off
        given()
                .queryParam("applicationDate", "2020-06-14T10:00:00Z")
                .queryParam("productId", 35455)
                .queryParam("brandId", 1)
        .when()
                .get()
        .then()
                .statusCode(200)
                .body("productId", is(35455))
                .body("brandId", is(1))
                .body("priceList", is(1))
                .body("price", is(35.50f))
                .body("currency", is("EUR"));
        // @formatter:on
    }

    @Test
    void test2_RequestAt202006141600_Product35455_Brand1() {
        // @formatter:off
        given()
                .queryParam("applicationDate", "2020-06-14T16:00:00Z")
                .queryParam("productId", 35455)
                .queryParam("brandId", 1)
        .when()
                .get()
        .then()
                .statusCode(200)
                .body("productId", is(35455))
                .body("brandId", is(1))
                .body("priceList", is(2))
                .body("price", is(25.45f))
                .body("currency", is("EUR"));
            // @formatter:on
    }

    @Test
    void test3_RequestAt202006142100_Product35455_Brand1() {
        // @formatter:off
        given()
                .queryParam("applicationDate", "2020-06-14T21:00:00Z")
                .queryParam("productId", 35455)
                .queryParam("brandId", 1)
        .when()
                .get()
        .then()
                .statusCode(200)
                .body("productId", is(35455))
                .body("brandId", is(1))
                .body("priceList", is(1))
                .body("price", is(35.50f))
                .body("currency", is("EUR"));
        // @formatter:on
    }

    @Test
    void test4_RequestAt202006151000_Product35455_Brand1() {
        // @formatter:off
        given()
                .queryParam("applicationDate", "2020-06-15T10:00:00Z")
                .queryParam("productId", 35455)
                .queryParam("brandId", 1)
        .when()
                .get()
        .then()
                .statusCode(200)
                .body("productId", is(35455))
                .body("brandId", is(1))
                .body("priceList", is(3))
                .body("price", is(30.50f))
                .body("currency", is("EUR"));
        // @formatter:on
    }

    @Test
    void test5_RequestAt202006162100_Product35455_Brand1() {
        // @formatter:off
        given()
                .queryParam("applicationDate", "2020-06-16T21:00:00Z")
                .queryParam("productId", 35455)
                .queryParam("brandId", 1)
        .when()
                .get()
        .then()
                .statusCode(200)
                .body("productId", is(35455))
                .body("brandId", is(1))
                .body("priceList", is(4))
                .body("price", is(38.95f))
                .body("currency", is("EUR"));
        // @formatter:on
    }

    @Test
    void testPriceNotFound() {
        // @formatter:off
        given()
                .queryParam("applicationDate", "2020-06-16T21:00:00Z")
                .queryParam("productId", 99999)
                .queryParam("brandId", 1)
        .when()
                .get()
        .then()
                .statusCode(404)
                .body("status", is(404))
                .body("error", is("Not Found"))
                .body("message", containsString("No applicable price found"));
        // @formatter:on
    }

    @Test
    void testMissingRequiredParameter() {
        // @formatter:off
        given()
                .queryParam("productId", 35455)
                .queryParam("brandId", 1)
        .when()
                .get()
        .then()
                .statusCode(400)
                .body("status", is(400))
                .body("error", is("Bad Request"))
                .body("message", containsString("Required request parameter"));
        // @formatter:on
    }

    @Test
    void testInvalidDateFormat() {
        // @formatter:off
        given()
                .queryParam("applicationDate", "not-a-date")
                .queryParam("productId", 35455)
                .queryParam("brandId", 1)
        .when()
                .get()
        .then()
                .statusCode(400)
                .body("status", is(400))
                .body("error", is("Bad Request"))
                .body("message", containsString("Failed to convert value"));
        // @formatter:on
    }

    @Test
    void testInvalidNumberFormat() {
        // @formatter:off
        given()
                .queryParam("applicationDate", "2020-06-14T10:00:00Z")
                .queryParam("productId", "not-a-number")
                .queryParam("brandId", 1)
        .when()
                .get()
        .then()
                .statusCode(400)
                .body("status", is(400))
                .body("error", is("Bad Request"))
                .body("message", containsString("Failed to convert value"));
        // @formatter:on
    }
}
