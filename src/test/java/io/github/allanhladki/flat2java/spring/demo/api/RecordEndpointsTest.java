package io.github.allanhladki.flat2java.spring.demo.api;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.restassured.RestAssured;
import io.restassured.config.EncoderConfig;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.ByteArrayInputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;

class RecordEndpointsTest extends AbstractApiTest {

    private static final int RECORD_LENGTH = 315;

    /** The pairs shown in the Swagger UI. */
    static Stream<Arguments> examples() {
        return Stream.of(
                Arguments.of("typical", ApiExamples.TYPICAL_JSON, ApiExamples.TYPICAL_RECORD),
                Arguments.of("accents", ApiExamples.ACCENTS_JSON, ApiExamples.ACCENTS_RECORD),
                Arguments.of("boundary", ApiExamples.BOUNDARY_JSON, ApiExamples.BOUNDARY_RECORD));
    }

    Response read(String record) {
        return given().contentType("text/plain; charset=UTF-8")
                .body(record.getBytes(StandardCharsets.UTF_8))
                .post("/read");
    }

    Response write(String json) {
        return given().contentType(ContentType.JSON).body(json).post("/write");
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("examples")
    void writesTheExampleObjectAsTheExampleRecord(String name, String json, String record) {
        Response response = write(json);

        response.then()
                .statusCode(200)
                .contentType("text/plain;charset=UTF-8")
                .header("X-Request-Id", notNullValue());
        assertEquals(RECORD_LENGTH, record.length());
        assertEquals(record, response.asString());
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("examples")
    void readsTheExampleRecordAndWritesItBackUnchanged(String name, String json, String record) {
        Response object = read(record);

        object.then().statusCode(200).contentType(ContentType.JSON);
        assertEquals(record, write(object.asString()).asString());
    }

    @Test
    void readsEveryKindOfField() {
        read(ApiExamples.TYPICAL_RECORD)
                .then()
                .statusCode(200)
                .body("recordType", equalTo("SAMP"))
                .body("schemaVersion", equalTo(1))
                .body("name", equalTo("John Doe"))
                .body("reference", equalTo("REF-1"))
                .body("priority", equalTo(7))
                .body("quantity", equalTo(100))
                .body("transactionId", equalTo(1234567890))
                .body("amount", equalTo(99.99f))
                .body("accountNumber", equalTo(987654321))
                .body("birthDate", equalTo("1990-01-01"))
                .body("createdAt", equalTo("2024-01-01T12:00:00"))
                .body("address.city", equalTo("Curitiba"))
                .body("address.postalCode", equalTo("80010000"))
                .body("contact.phone", equalTo("554133334444"))
                .body("contact.email", equalTo("john@example.com"))
                .body("weeklyHits", hasSize(4))
                .body("weeklyHits[3]", equalTo(40))
                .body("items", hasSize(3))
                .body("items[1].sku", equalTo("CD-200"))
                .body("items[1].unitPrice", equalTo(99.9f))
                .body("tags", hasSize(2))
                .body("tags[1]", equalTo("vip"))
                .body("internalNote", nullValue());
    }

    @Test
    void readsAccentedCharactersAsUtf8() {
        read(ApiExamples.ACCENTS_RECORD)
                .then()
                .statusCode(200)
                .body("name", equalTo("José María O'Connor-Smith"))
                .body("address.city", equalTo("São Paulo"))
                .body("tags[0]", equalTo("açaí"));
    }

    @Test
    void readsATextBodyWithoutCharsetAsUtf8() {
        // REST Assured adds charset=ISO-8859-1 to a content type without one; keep it out.
        given().config(
                        RestAssured.config()
                                .encoderConfig(
                                        EncoderConfig.encoderConfig()
                                                .appendDefaultContentCharsetToContentTypeIfUndefined(
                                                        false)))
                .contentType("text/plain")
                .body(ApiExamples.ACCENTS_RECORD.getBytes(StandardCharsets.UTF_8))
                .post("/read")
                .then()
                .statusCode(200)
                .body("name", equalTo("José María O'Connor-Smith"));
    }

    @Test
    void doesNotWriteTheFieldWithoutFlatField() {
        String withNote =
                ApiExamples.TYPICAL_JSON.replaceFirst("\\{", "{\"internalNote\": \"private\",");

        assertEquals(ApiExamples.TYPICAL_RECORD, write(withNote).asString());
    }

    @Test
    void rejectsATextLongerThanItsColumnInsteadOfCuttingIt() {
        String json = ApiExamples.TYPICAL_JSON.replace("John Doe", "J".repeat(31));

        write(json)
                .then()
                .statusCode(400)
                .body("error", equalTo("Serialization Error"))
                .body("message", containsString("name"));
    }

    @Test
    void rejectsARecordOfTheWrongLength() {
        read(ApiExamples.SHORT_RECORD)
                .then()
                .statusCode(400)
                .contentType(ContentType.JSON)
                .body("error", equalTo("Deserialization Error"))
                .body("message", containsString("315"))
                .body("requestId", notNullValue())
                .body("timestamp", notNullValue());
    }

    @Test
    void rejectsLettersInANumericColumn() {
        String record = ApiExamples.TYPICAL_RECORD.replace("0000000100", "00000ABCDE");

        read(record)
                .then()
                .statusCode(400)
                .body("error", equalTo("Deserialization Error"))
                .body("message", containsString("quantity"));
    }

    @Test
    void doesNotRepeatFieldValuesInErrors() {
        String record = ApiExamples.TYPICAL_RECORD.replace("0000000100", "00000SECRE");

        read(record)
                .then()
                .statusCode(400)
                .body("message", not(containsString("SECRE")))
                .body("message", not(containsString("John Doe")));
    }

    @Test
    void rejectsANegativeNumber() {
        write(ApiExamples.NEGATIVE_NUMBER_JSON)
                .then()
                .statusCode(400)
                .contentType(ContentType.JSON)
                .body("error", equalTo("Serialization Error"))
                .body("message", containsString("quantity"));
    }

    @Test
    void rejectsAListWithTheWrongNumberOfElements() {
        String json = ApiExamples.TYPICAL_JSON.replace("[\"new\", \"vip\"]", "[\"new\"]");

        write(json)
                .then()
                .statusCode(400)
                .body("error", equalTo("Serialization Error"))
                .body("message", containsString("tags"));
    }

    @Test
    void rejectsAnObjectWithoutRequiredValues() {
        write("{\"recordType\": \"SAMP\"}")
                .then()
                .statusCode(400)
                .body("error", equalTo("Serialization Error"));
    }

    @Test
    void rejectsAnEmptyBody() {
        given().contentType(ContentType.JSON).post("/write").then().statusCode(400);
        given().contentType("text/plain").post("/read").then().statusCode(400);
    }

    @Test
    void rejectsABodyOverTheSizeLimit() {
        given().contentType("text/plain")
                .body("x".repeat(100_000))
                .post("/read")
                .then()
                .statusCode(413);
    }

    @Test
    void rejectsMalformedJson() {
        write("{not json")
                .then()
                .statusCode(400)
                .contentType(ContentType.JSON)
                .body("error", equalTo("Bad Request"))
                .body("requestId", notNullValue());
    }

    @Test
    void rejectsAChunkedBodyOverTheSizeLimit() throws Exception {
        // No Content-Length: the size is counted while the body is read.
        HttpRequest request =
                HttpRequest.newBuilder(URI.create("http://localhost:" + port + "/read"))
                        .header("Content-Type", "text/plain")
                        .POST(
                                HttpRequest.BodyPublishers.ofInputStream(
                                        () -> new ByteArrayInputStream(new byte[100_000])))
                        .build();

        HttpResponse<String> response =
                HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(413, response.statusCode());
        assertTrue(response.body().contains("Payload Too Large"));
    }

    @Test
    void rejectsAnUnsupportedContentType() {
        given().contentType(ContentType.JSON)
                .body("{}")
                .post("/read")
                .then()
                .statusCode(415)
                .contentType(ContentType.JSON)
                .body("error", equalTo("Unsupported Media Type"));
    }

    @Test
    void answersUnknownPathsAndMethodsWithJsonErrors() {
        given().get("/missing")
                .then()
                .statusCode(404)
                .contentType(ContentType.JSON)
                .body("error", equalTo("Not Found"))
                .body("requestId", notNullValue());
        given().get("/read").then().statusCode(405).body("error", equalTo("Method Not Allowed"));
    }

    @Test
    void sendsTheRequestIdInTheHeaderAndTheErrorBody() {
        Response response = read(ApiExamples.SHORT_RECORD);

        String header = response.header("X-Request-Id");
        assertNotEquals(null, header);
        response.then().body("requestId", allOf(notNullValue(), equalTo(header)));
        assertNotEquals(header, read(ApiExamples.SHORT_RECORD).header("X-Request-Id"));
    }
}
