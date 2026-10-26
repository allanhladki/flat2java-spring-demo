package io.github.allanhladki.flat2java.spring.demo.config;

import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;
import org.springframework.stereotype.Component;

import java.util.Map;

/** Adds the versions of flat2java, Spring Boot and Java to {@code /info}. */
@Component
class VersionsInfoContributor implements InfoContributor {

    @Override
    public void contribute(Info.Builder builder) {
        builder.withDetail(
                "versions",
                Map.of(
                        "application", Versions.application(),
                        "flat2java", Versions.flat2java(),
                        "springBoot", Versions.springBoot(),
                        "java", Versions.java()));
    }
}
