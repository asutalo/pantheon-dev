package com.eu.atit.pantheon.server.request.validation;

import com.eu.atit.pantheon.server.response.exception.NotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

class ValidatorTest {
    private Validator validator;

    private static Stream<Arguments> argumentsForUriValidation() {
        return Stream.of(
                Arguments.of("", true),
                Arguments.of("/", false),
                Arguments.of("/endpoint/car/make/model/123?color=blue&finish=sparkle", false),
                Arguments.of("/endpoint/car?make/model/123?color=blue&finish=sparkle", true),
                Arguments.of("/endpoint/car/make/model=/123?color=blue&finish=sparkle", true),
                Arguments.of("/endpoint/car/make/model/123?color=blue&finish=sparkle/apple", true),
                Arguments.of("/endpoint&car/make/model/123?color=blue&finish=sparkle", true)
        );
    }

    @BeforeEach
    void setUp() {
        validator = new Validator();
    }

    @ParameterizedTest
    @MethodSource("argumentsForUriValidation")
    void validateUri_shouldSupportRFC3986(String decodedUriString, boolean expectException) {

        if (expectException) {
            Assertions.assertThrows(NotFoundException.class, () -> validator.validateUri(decodedUriString));
        } else {
            Assertions.assertDoesNotThrow(() -> validator.validateUri(decodedUriString));
        }
    }
}