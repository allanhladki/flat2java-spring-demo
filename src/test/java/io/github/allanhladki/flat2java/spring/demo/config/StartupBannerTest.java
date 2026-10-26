package io.github.allanhladki.flat2java.spring.demo.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringBootVersion;
import org.springframework.mock.env.MockEnvironment;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

class StartupBannerTest {

    @Test
    void printsTheNameAndTheVersions() {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();

        new StartupBanner()
                .printBanner(
                        new MockEnvironment(),
                        StartupBannerTest.class,
                        new PrintStream(bytes, true, StandardCharsets.UTF_8));

        assertThat(bytes.toString(StandardCharsets.UTF_8))
                .contains("|_|  |_|")
                .contains("flat2java 0.1.0")
                .contains("Spring Boot " + SpringBootVersion.getVersion())
                .contains("Java " + System.getProperty("java.version"))
                .contains("profile default")
                .doesNotContain("unknown");
    }

    @Test
    void listsTheActiveProfiles() {
        MockEnvironment environment = new MockEnvironment();
        environment.setActiveProfiles("dev", "local");

        assertThat(StartupBanner.describe(environment)).contains("profile dev,local");
    }
}
