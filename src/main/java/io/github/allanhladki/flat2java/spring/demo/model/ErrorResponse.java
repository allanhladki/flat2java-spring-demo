package io.github.allanhladki.flat2java.spring.demo.model;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

/**
 * Body of every error response.
 *
 * @param error the error type, for example {@code Deserialization Error}
 * @param message what went wrong; it never contains field values
 * @param requestId the ID of the request, also sent in the {@code X-Request-Id} header and written
 *     in every log line of the request
 * @param timestamp when the error happened
 */
@Schema(name = "Error")
public record ErrorResponse(
        @Schema(example = "Deserialization Error") String error,
        @Schema(
                        example =
                                "SampleModel (columns 1-315): expected 315 characters, but the"
                                        + " record has 5")
                String message,
        @Schema(example = "056c94d3-8c88-40c2-881c-3b889175a904") String requestId,
        @Schema(example = "2026-10-26T04:17:44.720Z") Instant timestamp) {}
