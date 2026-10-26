package io.github.allanhladki.flat2java.spring.demo.api;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;

import org.junit.jupiter.api.Test;

class OpenApiTest extends AbstractApiTest {

    @Test
    void servesTheOpenApiDocumentWithNamedExamples() {
        String document =
                given().get("/swagger.yaml")
                        .then()
                        .statusCode(200)
                        .contentType(containsString("openapi"))
                        .extract()
                        .asString();

        for (String expected :
                new String[] {
                    "/read:",
                    "/write:",
                    "/file:",
                    "typical:",
                    "accents:",
                    "boundary:",
                    "invalid-length:",
                    "negative-number:",
                    "samples/typical.txt",
                    "Error:"
                }) {
            org.junit.jupiter.api.Assertions.assertTrue(
                    document.contains(expected), "The document has no " + expected);
        }
    }

    @Test
    void servesTheSwaggerUi() {
        given().get("/swagger-ui")
                .then()
                .statusCode(200)
                .contentType(containsString("html"))
                .body(containsString("swagger"));
    }

    @Test
    void servesTheHealthChecks() {
        given().get("/health").then().statusCode(200).body("status", equalTo("UP"));
        given().get("/health/ready").then().statusCode(200);
        given().get("/health/live").then().statusCode(200);
    }
}
