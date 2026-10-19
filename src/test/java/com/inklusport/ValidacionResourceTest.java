package com.inklusport;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@QuarkusTest
class ValidacionResourceTest {

    @Test
    void debeAceptarEventoValido() {
        given()
            .contentType("application/json")
            .body("""
                {
                    "nombre": "Torneo de baloncesto",
                    "capacidad": 20
                }
                """)
        .when()
            .post("/validacion/eventos")
        .then()
            .statusCode(201)
            .body("nombre", equalTo("Torneo de baloncesto"))
            .body("capacidad", equalTo(20));
    }

    @Test
    void debeRechazarNombreEnBlancoYCapacidadCero() {
        given()
            .contentType("application/json")
            .body("""
                {
                    "nombre": "   ",
                    "capacidad": 0
                }
                """)
        .when()
            .post("/validacion/eventos")
        .then()
            .statusCode(400);
    }

    @Test
    void debeRechazarCapacidadNula() {
        given()
            .contentType("application/json")
            .body("""
                {
                    "nombre": "Torneo de atletismo",
                    "capacidad": null
                }
                """)
        .when()
            .post("/validacion/eventos")
        .then()
            .statusCode(400);
    }

    @Test
    void debeAceptarEventoValidoEnValidacionManual() {
        given()
            .contentType("application/json")
            .body("""
                {
                    "nombre": "Torneo de baloncesto",
                    "capacidad": 20
                }
                """)
        .when()
            .post("/eventos/validacion-manual")
        .then()
            .statusCode(200)
            .body("valido", equalTo(true))
            .body("mensaje", equalTo("El evento cumple las reglas de validación"));
    }

    @Test
    void debeRechazarEventoInvalidoEnValidacionManual() {
        given()
            .contentType("application/json")
            .body("""
                {
                    "nombre": " ",
                    "capacidad": 0
                }
                """)
        .when()
            .post("/eventos/validacion-manual")
        .then()
            .statusCode(400)
            .body("valido", equalTo(false));
    }
}