package com.example.webapp.controller;

import io.restassured.RestAssured;
import io.restassured.http.Method;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

import static io.restassured.RestAssured.given;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class HealthCheckControllerTest {

    @LocalServerPort
    private int port;

    @BeforeEach
    public void setUp() {
        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost";
    }

    @Test
    public void testHealthCheck() {
        given()
                .when()
                .request(Method.GET, "/healthz")
                .then()
                .statusCode(HttpStatus.OK.value())
                .header("Cache-Control", "no-cache, no-store, must-revalidate")
                .header("Pragma", "no-cache")
                .header("X-Content-Type-Options", "nosniff");
    }

    @Test
    public void testMethodNotAllowed() {
        given()
                .when()
                .request(Method.POST, "/healthz")
                .then()
                .statusCode(HttpStatus.METHOD_NOT_ALLOWED.value())
                .header("Cache-Control", "no-cache, no-store, must-revalidate")
                .header("Pragma", "no-cache")
                .header("X-Content-Type-Options", "nosniff");

        given()
                .when()
                .request(Method.PUT, "/healthz")
                .then()
                .statusCode(HttpStatus.METHOD_NOT_ALLOWED.value())
                .header("Cache-Control", "no-cache, no-store, must-revalidate")
                .header("Pragma", "no-cache")
                .header("X-Content-Type-Options", "nosniff");

        given()
                .when()
                .request(Method.DELETE, "/healthz")
                .then()
                .statusCode(HttpStatus.METHOD_NOT_ALLOWED.value())
                .header("Cache-Control", "no-cache, no-store, must-revalidate")
                .header("Pragma", "no-cache")
                .header("X-Content-Type-Options", "nosniff");

        given()
                .when()
                .request(Method.PATCH, "/healthz")
                .then()
                .statusCode(HttpStatus.METHOD_NOT_ALLOWED.value())
                .header("Cache-Control", "no-cache, no-store, must-revalidate")
                .header("Pragma", "no-cache")
                .header("X-Content-Type-Options", "nosniff");

        given()
                .when()
                .request(Method.HEAD, "/healthz")
                .then()
                .statusCode(HttpStatus.METHOD_NOT_ALLOWED.value())
                .header("Cache-Control", "no-cache, no-store, must-revalidate")
                .header("Pragma", "no-cache")
                .header("X-Content-Type-Options", "nosniff");

        given()
                .when()
                .request(Method.OPTIONS, "/healthz")
                .then()
                .statusCode(HttpStatus.METHOD_NOT_ALLOWED.value())
                .header("Cache-Control", "no-cache, no-store, must-revalidate")
                .header("Pragma", "no-cache")
                .header("X-Content-Type-Options", "nosniff");
    }

    @Test
    public void testHealthCheckWithQueryParams() {
        given()
                .queryParam("unexpectedParam", "value")
                .when()
                .request(Method.GET, "/healthz")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .header("Cache-Control", "no-cache, no-store, must-revalidate")
                .header("Pragma", "no-cache")
                .header("X-Content-Type-Options", "nosniff");
    }

    @Test
    public void testHealthCheckWithRequestBody() {
        given()
                .body("{\"unexpected\": \"data\"}")
                .when()
                .request(Method.GET, "/healthz")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .header("Cache-Control", "no-cache, no-store, must-revalidate")
                .header("Pragma", "no-cache")
                .header("X-Content-Type-Options", "nosniff");
    }

}