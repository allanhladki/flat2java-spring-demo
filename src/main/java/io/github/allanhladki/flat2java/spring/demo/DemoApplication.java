package io.github.allanhladki.flat2java.spring.demo;

import io.github.allanhladki.flat2java.spring.demo.config.StartupBanner;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(DemoApplication.class);
        application.setBanner(new StartupBanner());
        application.run(args);
    }
}
