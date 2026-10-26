package io.github.allanhladki.flat2java.spring.demo.exception;

import io.github.allanhladki.flat2java.exception.DeserializationException;
import io.github.allanhladki.flat2java.exception.Flat2JavaException;
import io.github.allanhladki.flat2java.exception.SerializationException;
import io.github.allanhladki.flat2java.spring.demo.filter.RequestIdFilter;
import io.github.allanhladki.flat2java.spring.demo.model.ErrorResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.Instant;

/**
 * Turns exceptions into {@link ErrorResponse} bodies. The errors that Spring MVC raises itself
 * ({@code 404}, {@code 405}, {@code 415}, a malformed body) take the same shape. Client errors are
 * logged as warnings and unexpected ones as errors; the response of an unexpected error never
 * carries the exception message.
 */
@RestControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(ApiExceptionHandler.class);

    /**
     * The record is invalid.
     *
     * @param e the exception
     * @return {@code 400}
     */
    @ExceptionHandler(DeserializationException.class)
    ResponseEntity<ErrorResponse> deserialization(DeserializationException e) {
        return clientError(HttpStatus.BAD_REQUEST, "Deserialization Error", e.getMessage());
    }

    /**
     * The object cannot be written, for example a number wider than its column.
     *
     * @param e the exception
     * @return {@code 400}
     */
    @ExceptionHandler(SerializationException.class)
    ResponseEntity<ErrorResponse> serialization(SerializationException e) {
        return clientError(HttpStatus.BAD_REQUEST, "Serialization Error", e.getMessage());
    }

    /**
     * An error of the application, such as an invalid {@code @FlatField} mapping.
     *
     * @param e the exception
     * @return {@code 500}
     */
    @ExceptionHandler(Flat2JavaException.class)
    ResponseEntity<ErrorResponse> flat2Java(Flat2JavaException e) {
        return serverError("Flat2Java Error", e);
    }

    /**
     * The request body is over the limit.
     *
     * @param e the exception
     * @return {@code 413}
     */
    @ExceptionHandler(PayloadTooLargeException.class)
    ResponseEntity<ErrorResponse> payloadTooLarge(PayloadTooLargeException e) {
        return clientError(HttpStatus.PAYLOAD_TOO_LARGE, "Payload Too Large", e.getMessage());
    }

    /**
     * Anything else.
     *
     * @param e the exception
     * @return {@code 500}
     */
    @ExceptionHandler(Exception.class)
    ResponseEntity<ErrorResponse> unexpected(Exception e) {
        return serverError("Internal Server Error", e);
    }

    /**
     * Every exception that {@link ResponseEntityExceptionHandler} handles ends here, including
     * {@code ResponseStatusException}.
     */
    @Override
    protected ResponseEntity<Object> handleExceptionInternal(
            Exception e,
            Object body,
            HttpHeaders headers,
            HttpStatusCode statusCode,
            WebRequest request) {
        HttpStatus status = HttpStatus.valueOf(statusCode.value());
        ResponseEntity<ErrorResponse> response;
        if (status.is5xxServerError()) {
            response = serverError("Internal Server Error", e);
        } else {
            response = clientError(status, status.getReasonPhrase(), clientMessage(e, status));
        }
        return ResponseEntity.status(statusCode)
                .headers(headers)
                .contentType(MediaType.APPLICATION_JSON)
                .body(response.getBody());
    }

    /** The message of a client error; a malformed body is described without the parser output. */
    private static String clientMessage(Exception e, HttpStatus status) {
        if (e instanceof HttpMessageNotReadableException) {
            return "The request body is missing or malformed";
        }
        if (e instanceof org.springframework.web.ErrorResponse error
                && error.getBody().getDetail() != null) {
            return error.getBody().getDetail();
        }
        return status.getReasonPhrase();
    }

    private static ResponseEntity<ErrorResponse> clientError(
            HttpStatus status, String error, String message) {
        log.warn("{}: {}", error, message);
        return response(status, error, message);
    }

    private static ResponseEntity<ErrorResponse> serverError(String error, Exception e) {
        log.error("Request failed", e);
        return response(HttpStatus.INTERNAL_SERVER_ERROR, error, "An unexpected error occurred");
    }

    private static ResponseEntity<ErrorResponse> response(
            HttpStatus status, String error, String message) {
        ErrorResponse body =
                new ErrorResponse(error, message, RequestIdFilter.current(), Instant.now());
        return ResponseEntity.status(status).contentType(MediaType.APPLICATION_JSON).body(body);
    }
}
