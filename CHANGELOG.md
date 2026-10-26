# Changelog

All notable changes to this project are documented in this file. The format follows
[Keep a Changelog](https://keepachangelog.com/en/1.1.0/), and the project uses
[Semantic Versioning](https://semver.org/). Each version states the flat2java version it is built on.

## [0.1.0] - 2026-10-26

Initial release. Requires Java 21. Built on flat2java 0.1.0 and Spring Boot 3.5.

### Added

**Application**

- `POST /read` reads a fixed-width record from a `text/plain` body and returns it as JSON.
- `POST /write` writes a JSON object as a record.
- `GET /file` reads a record from a UTF-8 file on the server. One trailing line terminator is
  ignored, and a file larger than one record is rejected before it is read.
- `SampleModel`, a 315-column record that uses every kind of field flat2java supports: a
  superclass, `String` with a custom padding character, `int`, `Integer`, `Long`, `BigInteger`,
  `BigDecimal` with implied decimals, `LocalDate`, `LocalDateTime`, a nested Java record, a nested
  class with explicit positions, an array, lists of nested records and of text, and a field
  without `@FlatField`.
- The `Flat2Java` bean comes from the `flat2java-spring` auto-configuration and the `flat2java.*`
  properties.
- JSON error responses with the error type, a message without field values, the request ID and a
  timestamp. Record and object errors are `400`, an invalid mapping or an unexpected exception is
  `500` with a generic message. The errors that Spring MVC raises (`404`, `405`, `415`, a malformed
  body) use the same shape.
- A home page at `/` with links, the versions and a playground that calls `/read` and `/write`.
  It has no external resources. `/info` reports the versions it shows.
- A startup banner with the versions of the application, flat2java, Spring Boot and Java, and a
  "Ready" report with the addresses of the Swagger UI, the OpenAPI document and the health check.
- A console log with aligned columns (time, level, request ID, class, message) and colors where
  the terminal supports them. The framework messages of the startup are silenced, and a browser
  asking for `/favicon.ico` is not a warning.
- A request ID in every log line of a request, in the `X-Request-Id` response header and in error
  responses. Only lengths are logged, never the contents of a record.
- A 64 KB limit for request bodies, also for bodies without a `Content-Length`, and
  `flat2java.fail-on-string-truncation=true`: a text longer than its column is a `400` instead of
  being cut.
- `text/plain` bodies without a charset are read as UTF-8.
- OpenAPI document at `/swagger` (YAML at `/swagger.yaml`) and Swagger UI at `/swagger-ui`, with
  springdoc-openapi. Each endpoint has named examples that work as they are, and two that fail on
  purpose. Example records are in `samples/`.
- Spring Boot Actuator health checks at `/health`, `/health/live` and `/health/ready`.

**Build and tests**

- `@SpringBootTest` tests with REST Assured. They check every example of the Swagger UI in both
  directions, each kind of field, and every error response.
- Mapping errors reported when the code compiles, with the `flat2java-processor` annotation
  processor.
- Maven Wrapper, Spotless formatting check (google-java-format, AOSP style) and a JaCoCo coverage
  report.
- Dockerfile for the JVM image, which includes `samples/`.
- CI that runs the tests and builds and starts the JVM image on every pull request.
- Release workflow that creates a signed tag and a GitHub release from a pull request labeled
  `release`.

[0.1.0]: https://github.com/allanhladki/flat2java-spring-demo/releases/tag/v0.1.0
