package io.github.allanhladki.flat2java.spring.demo.model;

import io.github.allanhladki.flat2java.annotation.FlatField;
import io.swagger.v3.oas.annotations.media.Schema;

/** A Java record used as a nested record: 20 + 15 + 2 + 8 = 45 columns. */
public record Address(
        @Schema(description = "Street and number (20 columns).", example = "Rua das Flores 100")
                @FlatField(length = 20)
                String street,
        @Schema(description = "City (15 columns).", example = "Curitiba") @FlatField(length = 15)
                String city,
        @Schema(description = "State code (2 columns).", example = "PR") @FlatField(length = 2)
                String state,
        @Schema(description = "Postal code (8 columns).", example = "80010000")
                @FlatField(length = 8)
                String postalCode) {}
