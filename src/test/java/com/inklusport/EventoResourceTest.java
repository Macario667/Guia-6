
package com.inklusport;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

@QuarkusTest
class EventoResourceTest {

    @Test
    void debeCrearEventoConDatosValidos() {
        given()
            .contentType("application/json")
            .body("""
                {
                    "nombre": "Torneo regional",
                    "usuarioId": 25
                }
                """)
        .when()
            .post("/eventos")
        .then()
            .statusCode(201)
            .body("mensaje", is("Evento procesado"));
    }

    @Test
    void debeRechazarEventoSinNombre() {
        given()
            .contentType("application/json")
            .body("""
                {
                    "nombre": " ",
                    "usuarioId": 25
                }
                """)
        .when()
            .post("/eventos")
        .then()
            .statusCode(400)
            .body("error",
                is("El nombre y el usuario son obligatorios"));
    }
}
