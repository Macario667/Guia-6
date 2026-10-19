
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
                    "capacidad": 20
                }
                """)
        .when()
            .post("/eventos")
        .then()
            .statusCode(200)
            .body(is("Evento válido: Torneo regional"));
    }

    @Test
    void debeRechazarEventoSinNombre() {
        given()
            .contentType("application/json")
            .body("""
                {
                    "nombre": " ",
                    "capacidad": 20
                }
                """)
        .when()
            .post("/eventos")
        .then()
            .statusCode(400)
            .contentType("application/json")
            .body("error", is("Error de validación"))
            .body("mensaje", is("El nombre del evento es obligatorio"));
    }
}
