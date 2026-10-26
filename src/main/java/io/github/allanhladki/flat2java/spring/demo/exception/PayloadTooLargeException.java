package io.github.allanhladki.flat2java.spring.demo.exception;

/** The request body is larger than the configured limit. */
public class PayloadTooLargeException extends RuntimeException {

    /**
     * Creates the exception.
     *
     * @param limit the limit, in bytes
     */
    public PayloadTooLargeException(long limit) {
        super("The request body is larger than " + limit + " bytes");
    }
}
