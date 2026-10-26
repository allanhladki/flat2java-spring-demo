package io.github.allanhladki.flat2java.spring.demo.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class StartupReportTest {

    @Test
    void listsTheAddressesOfTheApplication() {
        assertThat(StartupReport.lines("http://localhost:9090"))
                .anyMatch(line -> line.endsWith("http://localhost:9090/swagger-ui"))
                .anyMatch(line -> line.endsWith("http://localhost:9090/swagger.yaml"))
                .anyMatch(line -> line.endsWith("http://localhost:9090/health"))
                .anyMatch(line -> line.contains("curl -X POST http://localhost:9090/read"));
    }
}
