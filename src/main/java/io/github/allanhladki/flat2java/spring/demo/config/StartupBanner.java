package io.github.allanhladki.flat2java.spring.demo.config;

import org.springframework.boot.Banner;
import org.springframework.boot.SpringBootVersion;
import org.springframework.boot.ansi.AnsiColor;
import org.springframework.boot.ansi.AnsiOutput;
import org.springframework.boot.ansi.AnsiStyle;
import org.springframework.core.env.Environment;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

/**
 * Prints the name of the project and the versions of the application, of flat2java, of Spring Boot
 * and of Java. The application version comes from {@code META-INF/build-info.properties} and the
 * flat2java version from the {@code pom.properties} of its jar, so both are available when the
 * application runs from {@code target/classes} too.
 */
public class StartupBanner implements Banner {

    private static final String ART = "/banner-art.txt";
    private static final String BUILD_INFO = "/META-INF/build-info.properties";
    private static final String FLAT2JAVA_POM =
            "/META-INF/maven/io.github.allanhladki.flat2java/flat2java-core/pom.properties";

    @Override
    public void printBanner(Environment environment, Class<?> sourceClass, PrintStream out) {
        out.println();
        out.print(AnsiOutput.toString(AnsiColor.BRIGHT_GREEN, read(ART), AnsiColor.DEFAULT));
        out.println();
        out.println(describe(environment));
        out.println();
    }

    /**
     * The line with the versions.
     *
     * @param environment the environment, for the active profiles
     * @return the line, with ANSI codes when they are enabled
     */
    static String describe(Environment environment) {
        String[] profiles = environment.getActiveProfiles();
        return AnsiOutput.toString(
                "  ",
                AnsiStyle.BOLD,
                "Spring Boot demo ",
                version(BUILD_INFO, "build.version"),
                AnsiStyle.NORMAL,
                AnsiColor.BRIGHT_BLACK,
                "  |  flat2java ",
                version(FLAT2JAVA_POM, "version"),
                "  |  Spring Boot ",
                SpringBootVersion.getVersion(),
                "  |  Java ",
                System.getProperty("java.version"),
                "  |  profile ",
                profiles.length == 0 ? "default" : String.join(",", profiles),
                AnsiColor.DEFAULT);
    }

    private static String version(String resource, String key) {
        return properties(resource).getProperty(key, "unknown");
    }

    private static Properties properties(String resource) {
        Properties properties = new Properties();
        try (InputStream in = StartupBanner.class.getResourceAsStream(resource)) {
            if (in != null) {
                properties.load(in);
            }
        } catch (IOException e) {
            // The version shows as unknown.
        }
        return properties;
    }

    private static String read(String resource) {
        try (InputStream in = StartupBanner.class.getResourceAsStream(resource)) {
            return in == null ? "" : new String(in.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            return "";
        }
    }
}
