package io.github.allanhladki.flat2java.spring.demo.api;

/**
 * Example requests shown in the Swagger UI. Every JSON example is the object of the record with the
 * same name, and a test checks that each pair converts in both directions.
 */
final class ApiExamples {

    private ApiExamples() {}

    private static final String BEFORE_QUANTITY =
            """
            {
              "recordType": "SAMP",
              "schemaVersion": 1,
              "name": "John Doe",
              "reference": "REF-1",
              "priority": 7,
            """;

    private static final String AFTER_QUANTITY =
            """
              "transactionId": 1234567890,
              "amount": 99.99,
              "accountNumber": 987654321,
              "birthDate": "1990-01-01",
              "createdAt": "2024-01-01T12:00:00",
              "address": {
                "street": "Rua das Flores 100",
                "city": "Curitiba",
                "state": "PR",
                "postalCode": "80010000"
              },
              "contact": {
                "email": "john@example.com",
                "phone": "554133334444"
              },
              "weeklyHits": [10, 20, 30, 40],
              "items": [
                {"sku": "AB-100", "units": 2, "unitPrice": 12.50},
                {"sku": "CD-200", "units": 1, "unitPrice": 99.90},
                {"sku": "EF-300", "units": 10, "unitPrice": 0.99}
              ],
              "tags": ["new", "vip"]
            }
            """;

    static final String TYPICAL_JSON = BEFORE_QUANTITY + "  \"quantity\": 100,\n" + AFTER_QUANTITY;

    static final String ACCENTS_JSON =
            """
            {
              "recordType": "SAMP",
              "schemaVersion": 1,
              "name": "José María O'Connor-Smith",
              "reference": "REF-2",
              "priority": 3,
              "quantity": 42,
              "transactionId": 987654321,
              "amount": 123.45,
              "accountNumber": 123456789,
              "birthDate": "1985-06-15",
              "createdAt": "2024-06-15T14:30:00",
              "address": {
                "street": "Av. Paulista, 1578",
                "city": "São Paulo",
                "state": "SP",
                "postalCode": "01310200"
              },
              "contact": {
                "email": "jose@example.com",
                "phone": "551133334444"
              },
              "weeklyHits": [0, 5, 0, 15],
              "items": [
                {"sku": "ÇÃ-001", "units": 3, "unitPrice": 7.25},
                {"sku": "ÉP-002", "units": 1, "unitPrice": 1500.00},
                {"sku": "ÕE-003", "units": 12, "unitPrice": 0.50}
              ],
              "tags": ["açaí", "ñandú"]
            }
            """;

    static final String BOUNDARY_JSON =
            """
            {
              "recordType": "SAMP",
              "schemaVersion": 99,
              "name": "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123",
              "reference": "ABCDEFGH",
              "priority": 999,
              "quantity": 2147483647,
              "transactionId": 9223372036854775807,
              "amount": 9999999999999.99,
              "accountNumber": 99999999999999999999,
              "birthDate": "2000-12-31",
              "createdAt": "2024-12-31T23:59:59.999999",
              "address": {
                "street": "ABCDEFGHIJKLMNOPQRST",
                "city": "ABCDEFGHIJKLMNO",
                "state": "ZZ",
                "postalCode": "99999999"
              },
              "contact": {
                "email": "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123",
                "phone": "999999999999"
              },
              "weeklyHits": [999, 999, 999, 999],
              "items": [
                {"sku": "ABCDEF", "units": 999, "unitPrice": 9999999.99},
                {"sku": "ABCDEF", "units": 999, "unitPrice": 9999999.99},
                {"sku": "ABCDEF", "units": 999, "unitPrice": 9999999.99}
              ],
              "tags": ["12345678", "ABCDEFGH"]
            }
            """;

    /** Fails with {@code 400}: the record has 5 characters instead of 315. */
    static final String TYPICAL_RECORD =
            "SAMP01John Doe                     "
                + " REF-1___00700000001000000000000123456789000000000000999900000000000987654321199001012024-01-01T12:00:00.000000Rua"
                + " das Flores 100  Curitiba       PR80010000554133334444john@example.com          "
                + "    010020030040AB-100002000001250CD-200001000009990EF-300010000000099new    "
                + " vip     ";

    static final String ACCENTS_RECORD =
            "SAMP01José María O'Connor-Smith    "
                + " REF-2___00300000000420000000000098765432100000000001234500000000000123456789198506152024-06-15T14:30:00.000000Av."
                + " Paulista, 1578  São Paulo      SP01310200551133334444jose@example.com          "
                + "    000005000015ÇÃ-001003000000725ÉP-002001000150000ÕE-003012000000050açaí   "
                + " ñandú   ";
    static final String BOUNDARY_RECORD =
            "SAMP99ABCDEFGHIJKLMNOPQRSTUVWXYZ0123ABCDEFGH99921474836470922337203685477580799999999999999999999999999999999999200012312024-12-31T23:59:59.999999ABCDEFGHIJKLMNOPQRSTABCDEFGHIJKLMNOZZ99999999999999999999ABCDEFGHIJKLMNOPQRSTUVWXYZ0123999999999999ABCDEF999999999999ABCDEF999999999999ABCDEF99999999999912345678ABCDEFGH";

    /** Fails with {@code 400}: the record has 5 characters instead of 315. */
    static final String SHORT_RECORD = "short";

    /** Fails with {@code 400}: numbers are unsigned. */
    static final String NEGATIVE_NUMBER_JSON =
            BEFORE_QUANTITY + "  \"quantity\": -1,\n" + AFTER_QUANTITY;

    static final String TYPICAL_PATH = "samples/typical.txt";
    static final String ACCENTS_PATH = "samples/accents.txt";
    static final String BOUNDARY_PATH = "samples/boundary.txt";
}
