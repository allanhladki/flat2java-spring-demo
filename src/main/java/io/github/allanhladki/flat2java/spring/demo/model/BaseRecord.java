package io.github.allanhladki.flat2java.spring.demo.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;

import io.github.allanhladki.flat2java.annotation.FlatField;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Fields shared by every record of the file. flat2java writes the fields of a superclass before the
 * fields of its subclasses.
 */
@JsonAutoDetect(fieldVisibility = Visibility.ANY)
public class BaseRecord {

    @Schema(description = "Record type code (4 columns).", example = "SAMP")
    @FlatField(length = 4)
    private String recordType;

    @Schema(description = "Layout version (2 columns).", example = "1")
    @FlatField(length = 2)
    private int schemaVersion;
}
