package io.github.allanhladki.flat2java.spring.demo.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;

import io.github.allanhladki.flat2java.annotation.FlatField;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * A class used as a nested record, with an explicit field order: the phone is declared after the
 * e-mail but written first. The positions of a nested record are independent from the enclosing
 * class. 12 + 30 = 42 columns.
 */
@JsonAutoDetect(fieldVisibility = Visibility.ANY)
public class Contact {

    @Schema(
            description = "E-mail address, written second (30 columns).",
            example = "john@example.com")
    @FlatField(position = 2, length = 30)
    private String email;

    @Schema(description = "Phone number, written first (12 columns).", example = "554133334444")
    @FlatField(position = 1, length = 12)
    private String phone;
}
