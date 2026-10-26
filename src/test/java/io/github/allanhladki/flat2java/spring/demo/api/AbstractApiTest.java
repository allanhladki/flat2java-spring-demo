package io.github.allanhladki.flat2java.spring.demo.api;

import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

/** Starts the application on a random port; {@link #given()} sends requests to it. */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
abstract class AbstractApiTest {

    @LocalServerPort int port;

    RequestSpecification given() {
        return RestAssured.given().port(port);
    }
}
