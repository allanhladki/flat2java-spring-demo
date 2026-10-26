package io.github.allanhladki.flat2java.spring.demo.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.List;

/** Logs the addresses of the application once it is ready. */
@Component
class StartupReport {

    private static final Logger log = LoggerFactory.getLogger(StartupReport.class);

    private final Environment environment;

    StartupReport(Environment environment) {
        this.environment = environment;
    }

    @EventListener
    void onReady(ApplicationReadyEvent event) {
        String port = environment.getProperty("local.server.port", "8080");
        log.info("Ready in {} s", String.format("%.1f", event.getTimeTaken().toMillis() / 1000.0));
        lines("http://localhost:" + port).forEach(log::info);
    }

    /**
     * The lines of the report.
     *
     * @param base the address of the application, without a trailing slash
     * @return the lines
     */
    static List<String> lines(String base) {
        return List.of(
                "  Swagger UI  " + base + "/swagger-ui",
                "  OpenAPI     " + base + "/swagger.yaml",
                "  Health      " + base + "/health",
                "  Try it      curl -X POST " + base + "/read --data-binary @samples/typical.txt");
    }
}
