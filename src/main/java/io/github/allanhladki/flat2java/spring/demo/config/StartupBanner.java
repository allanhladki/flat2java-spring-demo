package io.github.allanhladki.flat2java.spring.demo.config;

import org.springframework.boot.Banner;
import org.springframework.boot.ansi.AnsiColor;
import org.springframework.boot.ansi.AnsiOutput;
import org.springframework.boot.ansi.AnsiStyle;
import org.springframework.core.env.Environment;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

/**
 * Prints the name of the project and the versions of the application, of flat2java, of Spring Boot
 * and of Java.
 */
public class StartupBanner implements Banner {

    private static final String ART = "/banner-art.txt";

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
                Versions.application(),
                AnsiStyle.NORMAL,
                AnsiColor.BRIGHT_BLACK,
                "  |  flat2java ",
                Versions.flat2java(),
                "  |  Spring Boot ",
                Versions.springBoot(),
                "  |  Java ",
                Versions.java(),
                "  |  profile ",
                profiles.length == 0 ? "default" : String.join(",", profiles),
                AnsiColor.DEFAULT);
    }

    private static String read(String resource) {
        try (InputStream in = StartupBanner.class.getResourceAsStream(resource)) {
            return in == null ? "" : new String(in.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            return "";
        }
    }
}
