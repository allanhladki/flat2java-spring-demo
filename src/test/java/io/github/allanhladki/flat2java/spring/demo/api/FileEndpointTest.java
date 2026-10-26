package io.github.allanhladki.flat2java.spring.demo.api;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;

import io.restassured.response.ValidatableResponse;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

class FileEndpointTest extends AbstractApiTest {

    @TempDir Path directory;

    private ValidatableResponse file(String path) {
        return given().queryParam("path", path).get("/file").then();
    }

    private Path write(String name, String content) throws IOException {
        return Files.writeString(directory.resolve(name), content, StandardCharsets.UTF_8);
    }

    @Test
    void readsTheSampleFilesWithARelativePath() {
        file(ApiExamples.TYPICAL_PATH).statusCode(200).body("name", equalTo("John Doe"));
        file(ApiExamples.ACCENTS_PATH)
                .statusCode(200)
                .body("name", equalTo("José María O'Connor-Smith"));
        file(ApiExamples.BOUNDARY_PATH).statusCode(200).body("quantity", equalTo(2147483647));
    }

    @Test
    void readsAFileWithAnAbsolutePath() throws IOException {
        Path path = write("record.txt", ApiExamples.TYPICAL_RECORD);

        file(path.toString()).statusCode(200).body("name", equalTo("John Doe"));
    }

    @Test
    void ignoresOneTrailingLineTerminator() throws IOException {
        file(write("lf.txt", ApiExamples.TYPICAL_RECORD + "\n").toString()).statusCode(200);
        file(write("crlf.txt", ApiExamples.TYPICAL_RECORD + "\r\n").toString()).statusCode(200);
    }

    @Test
    void readsAFileWhosePathHasSpacesAndAccents() throws IOException {
        Path path = write("relatório de março.txt", ApiExamples.ACCENTS_RECORD);

        file(path.toString()).statusCode(200).body("address.city", equalTo("São Paulo"));
    }

    @Test
    void rejectsAFileWithMoreThanOneRecord() throws IOException {
        Path path =
                write(
                        "two.txt",
                        ApiExamples.TYPICAL_RECORD + "\n" + ApiExamples.TYPICAL_RECORD + "\n");

        file(path.toString()).statusCode(400).body("error", equalTo("Deserialization Error"));
    }

    @Test
    void rejectsAFileTooLargeToHoldOneRecordWithoutReadingIt() throws IOException {
        Path path = write("large.txt", "x".repeat(1_000_000));

        file(path.toString())
                .statusCode(400)
                .body("error", equalTo("Bad Request"))
                .body("message", containsString("larger than one record"));
    }

    @Test
    void rejectsAFileWithAnInvalidRecord() throws IOException {
        file(write("short.txt", "short\n").toString())
                .statusCode(400)
                .body("error", equalTo("Deserialization Error"))
                .body("requestId", org.hamcrest.Matchers.notNullValue());
    }

    @Test
    void rejectsAFileThatIsNotUtf8() throws IOException {
        Path path = directory.resolve("latin1.txt");
        Files.write(path, new byte[] {(byte) 0xE9, (byte) 0xFF, (byte) 0xFE});

        file(path.toString())
                .statusCode(400)
                .body("error", equalTo("Bad Request"))
                .body("message", containsString("UTF-8"));
    }

    @Test
    void requiresThePathParameter() {
        given().get("/file")
                .then()
                .statusCode(400)
                .body("error", equalTo("Bad Request"))
                .body("message", containsString("path"));
    }

    @Test
    void rejectsABlankPath() {
        file("").statusCode(400);
        file("   ").statusCode(400);
    }

    @Test
    void rejectsAPathThatTheFileSystemCannotRepresent() {
        file("bad\u0000path").statusCode(400).body("message", containsString("Invalid path"));
    }

    @Test
    void answers404ForAMissingFileOrADirectory() {
        file(directory.resolve("missing.txt").toString())
                .statusCode(404)
                .body("error", equalTo("Not Found"))
                .body("message", containsString("missing.txt"));
        file(directory.toString()).statusCode(404);
    }
}
