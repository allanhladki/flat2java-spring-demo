package io.github.allanhladki.flat2java.spring.demo.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;

import org.springframework.boot.info.BuildProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class OpenApiConfig {

    @Bean
    OpenAPI openApi(BuildProperties build) {
        return new OpenAPI()
                .info(
                        new Info()
                                .title("flat2java Spring Boot demo")
                                .version(build.getVersion())
                                .description(
                                        "Reads and writes fixed-width records with flat2java. Each"
                                            + " endpoint has named examples in its request body:"
                                            + " pick one in the Examples list, then press Execute.")
                                .license(
                                        new License()
                                                .name("MIT License")
                                                .url("https://opensource.org/licenses/MIT")));
    }
}
