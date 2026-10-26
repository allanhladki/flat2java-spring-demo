<div align="center">

# flat2java-spring-demo

**A Spring Boot REST application that reads and writes fixed-width records with flat2java.**

[![CI](https://github.com/allanhladki/flat2java-spring-demo/actions/workflows/ci.yml/badge.svg)](https://github.com/allanhladki/flat2java-spring-demo/actions/workflows/ci.yml)
[![flat2java](https://img.shields.io/maven-central/v/io.github.allanhladki.flat2java/flat2java-spring?label=flat2java)](https://central.sonatype.com/namespace/io.github.allanhladki.flat2java)
[![License: MIT](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)

![Java 21](https://img.shields.io/badge/Java-21-ED8B00?logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5-6DB33F?logo=springboot&logoColor=white)
![Maven](https://img.shields.io/badge/build-Maven-C71A36?logo=apachemaven&logoColor=white)

[Quick start](#quick-start) ·
[Endpoints](#endpoints) ·
[Record layout](#record-layout) ·
[Configuration](#configuration) ·
[Changelog](CHANGELOG.md)

</div>

---

This project shows [flat2java](https://github.com/allanhladki/flat2java) in a Spring Boot application.
It exposes REST endpoints that turn a fixed-width text record into JSON and JSON back into a
record, through the `flat2java-spring` auto-configuration, which registers the `Flat2Java` bean. The mapped class, `SampleModel`, uses
every kind of field the library supports, and the Swagger UI has ready-to-run examples for each
endpoint.

## Contents

- [Requirements](#requirements)
- [Quick start](#quick-start)
- [Startup](#startup)
- [Endpoints](#endpoints)
- [Record layout](#record-layout)
- [Errors](#errors)
- [Configuration](#configuration)
- [Running in a container](#running-in-a-container)
- [Tests](#tests)
- [Project layout](#project-layout)
- [Contributing](#contributing)
- [License](#license)

## Requirements

| Tool | Version |
|---|---|
| JDK | 21 |
| Maven | Not required: the repository includes the Maven Wrapper (`./mvnw`) |
| Docker or Podman | Optional, to build and run the container image |

| Component | Version |
|---|---|
| Spring Boot | 3.5.16 |
| springdoc-openapi | 2.8.17 |
| flat2java | 0.1.0 |

## Quick start

```bash
git clone https://github.com/allanhladki/flat2java-spring-demo.git
cd flat2java-spring-demo
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

The application listens on `http://localhost:8080`. Open the Swagger UI at
`http://localhost:8080/swagger-ui`: every endpoint has a list of named examples in its request body
or `path` parameter. Pick one, press **Execute**, and compare the request and the response. The
examples named `invalid-length` and `negative-number` fail on purpose, to show the error responses.

The same from the command line. `$(cat ...)` drops the line terminator at the end of the sample
file:

```bash
curl -X POST http://localhost:8080/read \
  -H 'Content-Type: text/plain; charset=UTF-8' \
  --data-binary "$(cat samples/typical.txt)"
```

```json
{
  "recordType": "SAMP",
  "schemaVersion": 1,
  "name": "John Doe",
  "reference": "REF-1",
  "priority": 7,
  "quantity": 100,
  "transactionId": 1234567890,
  "amount": 99.99,
  "accountNumber": 987654321,
  "birthDate": "1990-01-01",
  "createdAt": "2024-01-01T12:00:00",
  "address": {"street": "Rua das Flores 100", "city": "Curitiba", "state": "PR", "postalCode": "80010000"},
  "contact": {"email": "john@example.com", "phone": "554133334444"},
  "weeklyHits": [10, 20, 30, 40],
  "items": [
    {"sku": "AB-100", "units": 2, "unitPrice": 12.5},
    {"sku": "CD-200", "units": 1, "unitPrice": 99.9},
    {"sku": "EF-300", "units": 10, "unitPrice": 0.99}
  ],
  "tags": ["new", "vip"],
  "internalNote": null
}
```

## Startup

The application prints a banner with its version and the versions of flat2java, Spring Boot and
Java, then a "Ready" report with the addresses of the Swagger UI, the OpenAPI document and the
health check, and a `curl` command to try:

```text
  __  _         _    ____     _
 / _|| |  __ _ | |_ |___ \   (_)  __ _ __   __  __ _
| |_ | | / _` || __|  __) |  | | / _` |\ \ / / / _` |
|  _|| || (_| || |_  / __/   | || (_| | \ V / | (_| |
|_|  |_| \__,_| \__||_____| _/ | \__,_|  \_/   \__,_|
                           |__/

  Spring Boot demo 0.1.0-SNAPSHOT  |  flat2java 0.1.0  |  Spring Boot 3.5.16  |  Java 21.0.7  |  profile default

2026-10-26 13:24:24.628  INFO          StartupReport              | Ready in 2.7 s
2026-10-26 13:24:24.628  INFO          StartupReport              |   Swagger UI  http://localhost:8080/swagger-ui
2026-10-26 13:24:24.629  INFO          StartupReport              |   OpenAPI     http://localhost:8080/swagger.yaml
2026-10-26 13:24:24.629  INFO          StartupReport              |   Health      http://localhost:8080/health
2026-10-26 13:24:24.629  INFO          StartupReport              |   Try it      curl -X POST http://localhost:8080/read --data-binary @samples/typical.txt
```

## Endpoints

| Method and path | Request | Response | Purpose |
|---|---|---|---|
| `POST /read` | `text/plain`, one record | `application/json` | Reads a record into a `SampleModel` |
| `POST /write` | `application/json`, a `SampleModel` | `text/plain; charset=UTF-8` | Writes a `SampleModel` as a record |
| `GET /file?path=<path>` | Query parameter `path` | `application/json` | Reads the file at `path` as one record |
| `GET /swagger` | | OpenAPI document (JSON; YAML at `/swagger.yaml`) | API description |
| `GET /swagger-ui` | | HTML | Swagger UI |
| `GET /health`, `/health/live`, `/health/ready` | | `application/json` | Spring Boot Actuator health checks |

The names follow the library: `read` turns a record into an object, `write` turns an object into a
record.

`POST /write` takes the JSON that `POST /read` returns. The Swagger UI has its examples; from the
command line, send the object as the body:

```bash
curl -X POST http://localhost:8080/write \
  -H 'Content-Type: application/json' \
  -d '{"recordType":"SAMP","schemaVersion":1,"name":"John Doe","reference":"REF-1","priority":7,
       "quantity":100,"transactionId":1234567890,"amount":99.99,"accountNumber":987654321,
       "birthDate":"1990-01-01","createdAt":"2024-01-01T12:00:00",
       "address":{"street":"Rua das Flores 100","city":"Curitiba","state":"PR","postalCode":"80010000"},
       "contact":{"email":"john@example.com","phone":"554133334444"},
       "weeklyHits":[10,20,30,40],
       "items":[{"sku":"AB-100","units":2,"unitPrice":12.50},
                {"sku":"CD-200","units":1,"unitPrice":99.90},
                {"sku":"EF-300","units":10,"unitPrice":0.99}],
       "tags":["new","vip"]}'
```

The response is the 315-character record in `samples/typical.txt`.

`GET /file` reads a UTF-8 file with one record and one optional line terminator at its end. A
relative path is relative to the directory where the application was started. The sample files are
`samples/typical.txt`, `samples/accents.txt` and `samples/boundary.txt`:

```bash
curl 'http://localhost:8080/file?path=samples/accents.txt'
```

A file larger than one record is rejected before it is read.

> [!WARNING]
> `GET /file` reads any file that the application process can read. It exists to demonstrate
> reading a record from disk. Do not expose this application on a network you do not control.

## Record layout

`SampleModel` maps a record of 315 characters. It extends `BaseRecord`, and flat2java writes the
fields of a superclass first.

| Field | Columns | Length | Java type | Format |
|---|---|---|---|---|
| `recordType` | 1-4 | 4 | `String` (`BaseRecord`) | Left-aligned, padded with spaces |
| `schemaVersion` | 5-6 | 2 | `int` (`BaseRecord`) | Zero-filled on the left |
| `name` | 7-36 | 30 | `String` | Left-aligned, padded with spaces |
| `reference` | 37-44 | 8 | `String` | Padded with `_` (`padding = '_'`) |
| `priority` | 45-47 | 3 | `int` | Zero-filled on the left |
| `quantity` | 48-57 | 10 | `Integer` | Zero-filled on the left |
| `transactionId` | 58-77 | 20 | `Long` | Zero-filled on the left |
| `amount` | 78-92 | 15 | `BigDecimal` | Two implied decimal places: `000000000009999` is `99.99` |
| `accountNumber` | 93-112 | 20 | `BigInteger` | Zero-filled on the left |
| `birthDate` | 113-120 | 8 | `LocalDate` | `yyyyMMdd` |
| `createdAt` | 121-146 | 26 | `LocalDateTime` | `yyyy-MM-dd'T'HH:mm:ss.SSSSSS` |
| `address` | 147-191 | 45 | `Address`, a Java record | `street` 20, `city` 15, `state` 2, `postalCode` 8 |
| `contact` | 192-233 | 42 | `Contact`, a class | `phone` 12, then `email` 30: explicit `position`, the reverse of the declaration order |
| `weeklyHits` | 234-245 | 12 | `int[]` | 4 elements of 3 columns (`occurs = 4`) |
| `items` | 246-299 | 54 | `List<Item>` | 3 elements of 18 columns; `Item` is a record: `sku` 6, `units` 3, `unitPrice` 9 with two decimals |
| `tags` | 300-315 | 16 | `List<String>` | 2 elements of 8 columns |
| `internalNote` | | | `String` | Not mapped: no `@FlatField`, so it is ignored when writing |

Rules that the examples show:

- Numbers are unsigned and never truncated: a negative number or a number wider than its column
  fails.
- Lists and arrays have exactly `occurs` elements.
- A `String` longer than its column fails with a `400`, because the demo sets
  `flat2java.fail-on-string-truncation=true`. The library default is to cut the text and log a
  warning, which loses data silently.
- Accented characters count as one column each. Requests and responses use UTF-8; a `text/plain`
  body without a charset is read as UTF-8.

The full rules are in the [flat2java README](https://github.com/allanhladki/flat2java#record-format).

## Errors

Errors are returned as JSON:

```json
{
  "error": "Deserialization Error",
  "message": "SampleModel: expected a record of 315 characters, got 5",
  "requestId": "a30d30a9-9fe7-4554-9df4-25ded44e86aa",
  "timestamp": "2026-10-26T14:38:05.816839271Z"
}
```

| Cause | Status | `error` |
|---|---|---|
| A record that cannot be read: wrong length, letters in a numeric column, an invalid date | 400 | `Deserialization Error` |
| An object that cannot be written: a negative number, a number or text wider than its column, a list of the wrong size | 400 | `Serialization Error` |
| Missing or empty body, malformed JSON, missing, invalid or blank `path`, a file larger than one record or not in UTF-8 | 400 | `Bad Request` |
| No file at `path`, or a path that is a directory; unknown path | 404 | `Not Found` |
| HTTP method not allowed | 405 | `Method Not Allowed` |
| Body over 64 KB | 413 | `Payload Too Large` |
| `Content-Type` not accepted by the endpoint | 415 | `Unsupported Media Type` |
| Invalid `@FlatField` mapping or any unexpected exception | 500 | `Flat2Java Error` or `Internal Server Error`, with a generic message |

Messages name the field, never its value, so they can be logged. Every response, including errors,
has an `X-Request-Id` header. The same ID is the `requestId` of the error, and its first 8
characters appear in every log line of the request:

```text
2026-10-26 13:24:25.155  WARN efda5e8e ApiExceptionHandler        | Deserialization Error: SampleModel: expected a record of 315 characters, got 5
```

The columns are the time, the level, the request ID (empty outside a request), the class and the
message. The terminal gets colors when it supports them.

The application never logs the contents of a record.

## Configuration

`src/main/resources/application.properties`:

| Property | Value | Purpose |
|---|---|---|
| `server.port` | `8080` | HTTP port |
| `demo.max-body-size` | `65536` | Largest request body in bytes; a larger body is a `413` |
| `springdoc.api-docs.path` | `/swagger` | OpenAPI document path (JSON; the YAML is at `/swagger.yaml`) |
| `springdoc.swagger-ui.path` | `/swagger-ui` | Swagger UI path |
| `management.endpoints.web.base-path` | `/` | Serves the health checks at the root instead of `/actuator` |
| `logging.pattern.console` | columns, with colors | Time, level, request ID, class and message; framework messages of the startup are silenced |
| `logging.level.io.github.allanhladki` | `INFO` (`DEBUG` with the `dev` profile) | Application log level |
| `flat2java.fail-on-string-truncation` | `true` | A text longer than its column is an error; the library default is `false` |
| other `flat2java.*` | the defaults of `flat2java-spring` | [flat2java options](https://github.com/allanhladki/flat2java/blob/master/flat2java-spring/README.md#properties) |

With the `dev` profile (`application-dev.properties`) the application listens on `localhost` only
and logs at `DEBUG`; in the container it listens on all interfaces. Any property can be overridden
with a system property or an environment variable, for example
`SERVER_PORT=9090 ./mvnw spring-boot:run`. The tests start the application on a random port.

## Running in a container

The [Dockerfile](Dockerfile) copies the packaged jar and the `samples` directory into an
`eclipse-temurin:21-jre` image, so the `path` examples work in it: `samples/typical.txt` is
relative to `/app`.

```bash
./mvnw package
docker build -t flat2java-spring-demo:jvm .
docker run --rm -p 8080:8080 flat2java-spring-demo:jvm
```

> [!NOTE]
> The image is built and started on every pull request. There is no native image.

## Tests

```bash
./mvnw verify
```

| Command | What it does |
|---|---|
| `./mvnw verify` | Runs the tests, checks formatting, writes the coverage report to `target/site/jacoco` |
| `./mvnw spotless:apply` | Formats the code (google-java-format, AOSP style) |
| `./mvnw test -Dtest=RecordEndpointsTest` | Runs one test class |

The tests start the application on a random port with `@SpringBootTest` and call it with REST
Assured:

| Class | Covers |
|---|---|
| `RecordEndpointsTest` | `/read` and `/write`: every example of the Swagger UI in both directions, each kind of field, accents, a text over its column, and every error response |
| `FileEndpointTest` | `/file`: relative, absolute and accented paths, line terminators, and the missing, blank, invalid, oversized, non-UTF-8 and multi-record cases |
| `OpenApiTest` | The OpenAPI document and its named examples, the Swagger UI and the health checks |

## Project layout

```text
src/main/java/io/github/allanhladki/flat2java/spring/demo/
├── api/         FlatFileController (the endpoints) and ApiExamples (the Swagger examples)
├── config/      WebConfig (UTF-8 for text/plain), OpenApiConfig, StartupBanner and StartupReport
├── exception/   ApiExceptionHandler: exceptions to JSON error responses
├── filter/      RequestIdFilter (request ID in the log, header and errors) and BodySizeLimitFilter
├── model/       SampleModel, BaseRecord, Address, Contact, Item and ErrorResponse
└── service/     FlatFileService: calls flat2java and reads files
samples/         Example records, one per file
Dockerfile       JVM image
```

## Contributing

Bug reports and pull requests are welcome. See [CONTRIBUTING.md](CONTRIBUTING.md) for the
development workflow, and [SECURITY.md](SECURITY.md) to report a vulnerability privately.
Problems with the library itself belong in the
[flat2java repository](https://github.com/allanhladki/flat2java/issues).

## License

flat2java-spring-demo is released under the [MIT License](LICENSE).
