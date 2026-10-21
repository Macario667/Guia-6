package com.inklusport;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.matchesRegex;

@QuarkusTest
class LoggingResourceTest {

    @Test
    void debeEmitirLogsYDevolverIdDeCorrelacion() {
        given()
        .when()
            .get("/logs/demo")
        .then()
            .statusCode(200)
            .header("X-Request-Id", matchesRegex(
                    "[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}"));
    }
}
