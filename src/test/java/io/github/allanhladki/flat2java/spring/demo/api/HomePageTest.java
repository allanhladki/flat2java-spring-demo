package io.github.allanhladki.flat2java.spring.demo.api;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;

import org.junit.jupiter.api.Test;

class HomePageTest extends AbstractApiTest {

    @Test
    void servesTheHomePageAtTheRoot() {
        given().get("/")
                .then()
                .statusCode(200)
                .contentType(containsString("html"))
                .body(containsString("flat2java Spring Boot demo"))
                .body(containsString("/swagger-ui"))
                .body(containsString("/swagger.yaml"))
                .body(containsString("/health"))
                .body(containsString("/info"));
    }

    @Test
    void startsThePlaygroundWithTheTypicalRecord() {
        given().get("/").then().statusCode(200).body(containsString(ApiExamples.TYPICAL_RECORD));
    }

    @Test
    void usesNoExternalResources() {
        given().get("/")
                .then()
                .body(not(containsString("https://cdn")))
                .body(not(containsString("src=\"http")));
    }

    @Test
    void reportsTheVersionsThatThePageShows() {
        given().get("/info")
                .then()
                .statusCode(200)
                .body("versions.application", notNullValue())
                .body("versions.flat2java", equalTo("0.1.0"))
                .body("versions.springBoot", notNullValue())
                .body("versions.java", notNullValue());
    }
}
