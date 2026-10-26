package io.github.allanhladki.flat2java.spring.demo.api;

import io.github.allanhladki.flat2java.spring.demo.model.ErrorResponse;
import io.github.allanhladki.flat2java.spring.demo.model.SampleModel;
import io.github.allanhladki.flat2java.spring.demo.service.FlatFileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Records", description = "Convert fixed-width records to JSON and back with flat2java.")
public class FlatFileController {

    private static final String UTF_8_TEXT = MediaType.TEXT_PLAIN_VALUE + ";charset=UTF-8";

    private final FlatFileService service;

    FlatFileController(FlatFileService service) {
        this.service = service;
    }

    @PostMapping(
            path = "/read",
            consumes = MediaType.TEXT_PLAIN_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Read a record",
            description = "Converts one fixed-width record of 315 characters into a SampleModel.",
            requestBody =
                    @io.swagger.v3.oas.annotations.parameters.RequestBody(
                            required = true,
                            content =
                                    @Content(
                                            mediaType = MediaType.TEXT_PLAIN_VALUE,
                                            examples = {
                                                @ExampleObject(
                                                        name = "typical",
                                                        summary = "A typical record",
                                                        value = ApiExamples.TYPICAL_RECORD),
                                                @ExampleObject(
                                                        name = "accents",
                                                        summary = "Accented characters",
                                                        value = ApiExamples.ACCENTS_RECORD),
                                                @ExampleObject(
                                                        name = "boundary",
                                                        summary =
                                                                "Every column filled with its"
                                                                        + " largest value",
                                                        value = ApiExamples.BOUNDARY_RECORD),
                                                @ExampleObject(
                                                        name = "invalid-length",
                                                        summary =
                                                                "Fails with 400: 5 characters"
                                                                        + " instead of 315",
                                                        value = ApiExamples.SHORT_RECORD)
                                            })))
    @ApiResponse(responseCode = "200", description = "The record, as an object")
    @ApiResponse(
            responseCode = "400",
            description = "The record is invalid",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(
            responseCode = "415",
            description = "The request is not text/plain",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public SampleModel read(@RequestBody String line) {
        return service.read(line);
    }

    @PostMapping(
            path = "/write",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = UTF_8_TEXT)
    @Operation(
            summary = "Write a record",
            description = "Converts a SampleModel into one fixed-width record of 315 characters.",
            requestBody =
                    @io.swagger.v3.oas.annotations.parameters.RequestBody(
                            required = true,
                            content =
                                    @Content(
                                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                                            schema = @Schema(implementation = SampleModel.class),
                                            examples = {
                                                @ExampleObject(
                                                        name = "typical",
                                                        summary = "A typical object",
                                                        value = ApiExamples.TYPICAL_JSON),
                                                @ExampleObject(
                                                        name = "accents",
                                                        summary = "Accented characters",
                                                        value = ApiExamples.ACCENTS_JSON),
                                                @ExampleObject(
                                                        name = "boundary",
                                                        summary =
                                                                "Every column filled with its"
                                                                        + " largest value",
                                                        value = ApiExamples.BOUNDARY_JSON),
                                                @ExampleObject(
                                                        name = "negative-number",
                                                        summary =
                                                                "Fails with 400: numbers are"
                                                                        + " unsigned",
                                                        value = ApiExamples.NEGATIVE_NUMBER_JSON)
                                            })))
    @ApiResponse(
            responseCode = "200",
            description = "The record",
            content = @Content(mediaType = MediaType.TEXT_PLAIN_VALUE))
    @ApiResponse(
            responseCode = "400",
            description = "The object cannot be written",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public String write(@RequestBody SampleModel model) {
        return service.write(model);
    }

    @GetMapping(path = "/file", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Read a record from a file",
            description =
                    "Reads the UTF-8 file at the given path on the server as one record. The"
                        + " relative paths of the examples are relative to the directory where the"
                        + " application was started, which is the repository root when it runs from"
                        + " a clone.")
    @ApiResponse(responseCode = "200", description = "The record, as an object")
    @ApiResponse(
            responseCode = "400",
            description = "The path is missing or invalid, or the record is invalid",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(
            responseCode = "404",
            description = "There is no file at the path",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public SampleModel file(
            @Parameter(
                            description = "Path of the file to read",
                            required = true,
                            example = ApiExamples.TYPICAL_PATH,
                            examples = {
                                @ExampleObject(name = "typical", value = ApiExamples.TYPICAL_PATH),
                                @ExampleObject(name = "accents", value = ApiExamples.ACCENTS_PATH),
                                @ExampleObject(
                                        name = "boundary",
                                        value = ApiExamples.BOUNDARY_PATH),
                                @ExampleObject(
                                        name = "missing",
                                        summary = "Fails with 404",
                                        value = "samples/missing.txt")
                            })
                    @RequestParam(name = "path", required = false)
                    String path) {
        return service.readFile(path);
    }
}
