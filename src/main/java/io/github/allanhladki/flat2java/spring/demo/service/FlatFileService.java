package io.github.allanhladki.flat2java.spring.demo.service;

import io.github.allanhladki.flat2java.Flat2Java;
import io.github.allanhladki.flat2java.spring.demo.model.SampleModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;

/**
 * Reads and writes {@link SampleModel} records with flat2java. Only lengths are logged: a record
 * usually holds personal data.
 */
@Service
public class FlatFileService {

    private static final Logger log = LoggerFactory.getLogger(FlatFileService.class);

    private final Flat2Java flat2Java;

    FlatFileService(Flat2Java flat2Java) {
        this.flat2Java = flat2Java;
    }

    /**
     * Reads a record.
     *
     * @param line the record, without line terminator
     * @return the object
     */
    public SampleModel read(String line) {
        log.debug("Reading a record of {} characters", line.length());
        return flat2Java.read(line, SampleModel.class);
    }

    /**
     * Writes a record.
     *
     * @param model the object
     * @return the record, without line terminator
     */
    public String write(SampleModel model) {
        String line = flat2Java.write(model);
        log.debug("Wrote a record of {} characters", line.length());
        return line;
    }

    /**
     * Reads the record in a UTF-8 file. One trailing line terminator is ignored.
     *
     * @param location the path of the file
     * @return the object
     * @throws ResponseStatusException {@code 400} if the path is blank or invalid, or the file is
     *     too large to hold one record or is not UTF-8 text; {@code 404} if there is no file at the
     *     path
     */
    public SampleModel readFile(String location) {
        if (location == null || location.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "The path parameter is required");
        }
        Path path;
        try {
            path = Path.of(location);
        } catch (InvalidPathException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid path: " + location);
        }
        if (!Files.isRegularFile(path)) {
            throw notFound(path);
        }
        String content;
        try {
            // A record has at most 4 bytes per character, plus a line terminator. Checking the
            // size first keeps a huge file out of memory.
            long limit = 4L * flat2Java.recordLength(SampleModel.class) + 2;
            if (Files.size(path) > limit) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "The file is larger than one record (at most "
                                + limit
                                + " bytes): "
                                + path);
            }
            content = Files.readString(path, StandardCharsets.UTF_8);
        } catch (NoSuchFileException e) {
            throw notFound(path);
        } catch (CharacterCodingException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "The file is not UTF-8 text: " + path);
        } catch (IOException e) {
            throw new UncheckedIOException("Cannot read " + path, e);
        }
        log.debug("Read {} characters from {}", content.length(), path);
        return read(withoutLineTerminator(content));
    }

    private static ResponseStatusException notFound(Path path) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, "File not found: " + path);
    }

    private static String withoutLineTerminator(String content) {
        if (content.endsWith("\r\n")) {
            return content.substring(0, content.length() - 2);
        }
        if (content.endsWith("\n")) {
            return content.substring(0, content.length() - 1);
        }
        return content;
    }
}
