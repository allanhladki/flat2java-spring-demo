package io.github.allanhladki.flat2java.spring.demo.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;

import io.github.allanhladki.flat2java.annotation.FlatField;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * A record that uses every kind of field flat2java supports. It is 315 columns long: the 6 of
 * {@link BaseRecord} followed by the fields below, in declaration order.
 *
 * <ul>
 *   <li>text, with the default and a custom padding character;
 *   <li>{@code int}, {@code Integer}, {@code Long} and {@code BigInteger} numbers;
 *   <li>{@code BigDecimal} with implied decimal places;
 *   <li>{@code LocalDate} and {@code LocalDateTime} with fixed-width patterns;
 *   <li>a nested Java record ({@link Address}) and a nested class ({@link Contact});
 *   <li>an array ({@code weeklyHits}) and lists of nested records and of text;
 *   <li>a field without {@code @FlatField}, which is not part of the record.
 * </ul>
 */
@JsonAutoDetect(fieldVisibility = Visibility.ANY)
public class SampleModel extends BaseRecord {

    @Schema(description = "Customer name (30 columns).", example = "John Doe")
    @FlatField(length = 30)
    private String name;

    @Schema(
            description = "Reference, padded on the right with '_' instead of spaces (8 columns).",
            example = "REF-1")
    @FlatField(length = 8, padding = '_')
    private String reference;

    @Schema(description = "A primitive int (3 columns).", example = "7")
    @FlatField(length = 3)
    private int priority;

    @Schema(description = "An Integer (10 columns).", example = "100")
    @FlatField(length = 10)
    private Integer quantity;

    @Schema(description = "A Long (20 columns).", example = "1234567890")
    @FlatField(length = 20)
    private Long transactionId;

    @Schema(description = "Amount with two implied decimal places (15 columns).", example = "99.99")
    @FlatField(length = 15, precision = 2)
    private BigDecimal amount;

    @Schema(description = "A BigInteger (20 columns).", example = "987654321")
    @FlatField(length = 20)
    private BigInteger accountNumber;

    @Schema(description = "Written as yyyyMMdd (8 columns).", example = "1990-01-01")
    @FlatField(length = 8, dateTimeFormat = "yyyyMMdd")
    private LocalDate birthDate;

    @Schema(
            description = "Written as yyyy-MM-dd'T'HH:mm:ss.SSSSSS (26 columns).",
            example = "2024-01-01T12:00:00")
    @FlatField(length = 26, dateTimeFormat = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS")
    private LocalDateTime createdAt;

    @Schema(description = "A nested Java record (45 columns).")
    @FlatField(length = 45)
    private Address address;

    @Schema(description = "A nested class with explicit field positions (42 columns).")
    @FlatField(length = 42)
    private Contact contact;

    @ArraySchema(
            arraySchema =
                    @Schema(
                            description =
                                    "An int array: exactly 4 elements of 3 columns each (12"
                                            + " columns).",
                            example = "[10, 20, 30, 40]"),
            minItems = 4,
            maxItems = 4)
    @FlatField(length = 3, occurs = 4)
    private int[] weeklyHits;

    @ArraySchema(
            arraySchema =
                    @Schema(
                            description =
                                    "A list of nested records: exactly 3 elements of 18 columns"
                                            + " each (54 columns)."),
            minItems = 3,
            maxItems = 3)
    @FlatField(length = 18, occurs = 3)
    private List<Item> items;

    @ArraySchema(
            arraySchema =
                    @Schema(
                            description =
                                    "A list of text: exactly 2 elements of 8 columns each (16"
                                            + " columns).",
                            example = "[\"new\", \"vip\"]"),
            minItems = 2,
            maxItems = 2)
    @FlatField(length = 8, occurs = 2)
    private List<String> tags;

    @Schema(description = "Not mapped: it is ignored when writing and left null when reading.")
    private String internalNote;
}
