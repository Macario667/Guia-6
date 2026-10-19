package com.inklusport;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

@QuarkusTest
class EventoGestionResourceTest {

    @Test
    void debeMostrarMensajeEnInglesCuandoFaltaElNombre() {
        given()
            .contentType("application/json")
            .body("""
                {
                    "id": null,
                    "nombre": " ",
                    "capacidad": 10
                }
                """)
        .when()
            .post("/eventos/gestion/crear")
        .then()
            .statusCode(400)
            .body("mensaje", is("The event name is required"));
    }

    @Test
    void debeMostrarMensajeEnInglesCuandoFaltaLaCapacidad() {
        given()
            .contentType("application/json")
            .body("""
                {
                    "id": null,
                    "nombre": "Basketball tournament",
                    "capacidad": null
                }
                """)
        .when()
            .post("/eventos/gestion/crear")
        .then()
            .statusCode(400)
            .body("mensaje", is("Capacity is required"));
    }

    @Test
    void debeMostrarMensajeEnInglesCuandoLaCapacidadEsCero() {
        given()
            .contentType("application/json")
            .body("""
                {
                    "id": null,
                    "nombre": "Basketball tournament",
                    "capacidad": 0
                }
                """)
        .when()
            .post("/eventos/gestion/crear")
        .then()
            .statusCode(400)
            .body("mensaje", is("Capacity must be greater than zero"));
    }
}
