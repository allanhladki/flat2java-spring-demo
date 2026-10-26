package io.github.allanhladki.flat2java.spring.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Reads a {@code text/plain} body without a charset as UTF-8. Spring MVC defaults to ISO-8859-1,
 * which would count each accented character as two columns.
 */
@Configuration
class WebConfig implements WebMvcConfigurer {

    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.stream()
                .filter(StringHttpMessageConverter.class::isInstance)
                .map(StringHttpMessageConverter.class::cast)
                .forEach(converter -> converter.setDefaultCharset(StandardCharsets.UTF_8));
    }
}
