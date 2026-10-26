package io.github.allanhladki.flat2java.spring.demo.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

/**
 * Gives every request an ID. The ID is written in each log line (the {@code requestId} MDC key),
 * returned in the {@code X-Request-Id} response header and in error responses.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RequestIdFilter extends OncePerRequestFilter {

    /** MDC key that holds the request ID. */
    public static final String REQUEST_ID = "requestId";

    /** Response header that carries the request ID. */
    public static final String HEADER = "X-Request-Id";

    private static final Logger log = LoggerFactory.getLogger(RequestIdFilter.class);

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String id = UUID.randomUUID().toString();
        MDC.put(REQUEST_ID, id);
        response.setHeader(HEADER, id);
        try {
            log.debug("{} {}", request.getMethod(), request.getRequestURI());
            chain.doFilter(request, response);
        } finally {
            MDC.remove(REQUEST_ID);
        }
    }

    /**
     * The ID of the current request.
     *
     * @return the ID, or {@code null} outside a request
     */
    public static String current() {
        return MDC.get(REQUEST_ID);
    }
}
