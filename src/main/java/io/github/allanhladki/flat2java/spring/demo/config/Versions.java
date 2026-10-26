package io.github.allanhladki.flat2java.spring.demo.config;

import org.springframework.boot.SpringBootVersion;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * The versions that the banner and {@code /info} report. The application version comes from {@code
 * META-INF/build-info.properties} and the flat2java version from the {@code pom.properties} of its
 * jar, so both are available when the application runs from {@code target/classes} too.
 */
final class Versions {

    private static final String BUILD_INFO = "/META-INF/build-info.properties";
    private static final String FLAT2JAVA_POM =
            "/META-INF/maven/io.github.allanhladki.flat2java/flat2java-core/pom.properties";

    private Versions() {}

    static String application() {
        return property(BUILD_INFO, "build.version");
    }

    static String flat2java() {
        return property(FLAT2JAVA_POM, "version");
    }

    static String springBoot() {
        return SpringBootVersion.getVersion();
    }

    static String java() {
        return System.getProperty("java.version");
    }

    private static String property(String resource, String key) {
        Properties properties = new Properties();
        try (InputStream in = Versions.class.getResourceAsStream(resource)) {
            if (in != null) {
                properties.load(in);
            }
        } catch (IOException e) {
            // The version shows as unknown.
        }
        return properties.getProperty(key, "unknown");
    }
}
