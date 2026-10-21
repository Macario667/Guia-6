package com.inklusport;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.matchesRegex;

@QuarkusTest
class EventoGestionResourceTest {

    @Test
    void debeCrearEventoConIdVacio() {
        given()
            .contentType("application/json")
            .body("""
                {
                    "id": null,
                    "nombre": "Torneo regional",
                    "capacidad": 20
                }
                """)
        .when()
            .post("/eventos/gestion/crear")
        .then()
            .statusCode(200)
            .body(is("Evento listo para crear: Torneo regional"));
    }

    @Test
    void debeActualizarEventoConId() {
        given()
            .contentType("application/json")
            .body("""
                {
                    "id": 42,
                    "nombre": "Torneo regional",
                    "capacidad": 20
                }
                """)
        .when()
            .put("/eventos/gestion/actualizar")
        .then()
            .statusCode(200)
            .body(is("Evento listo para actualizar: 42"));
    }

    @Test
    void debeRechazarIdAlCrear() {
        given()
            .contentType("application/json")
            .body("""
                {
                    "id": 42,
                    "nombre": "Torneo regional",
                    "capacidad": 20
                }
                """)
        .when()
            .post("/eventos/gestion/crear")
        .then()
            .statusCode(400)
            .body("mensaje", is("El ID debe estar vacío al crear un evento"));
    }

    @Test
    void debeRechazarActualizacionSinId() {
        given()
            .contentType("application/json")
            .body("""
                {
                    "id": null,
                    "nombre": "Torneo regional",
                    "capacidad": 20
                }
                """)
        .when()
            .put("/eventos/gestion/actualizar")
        .then()
            .statusCode(400)
            .body("mensaje", is("El ID es obligatorio para actualizar un evento"));
    }

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
