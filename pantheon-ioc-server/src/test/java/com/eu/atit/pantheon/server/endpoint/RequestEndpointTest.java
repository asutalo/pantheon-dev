package com.eu.atit.pantheon.server.endpoint;

import com.eu.atit.pantheon.server.request.parsing.Parser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

class RequestEndpointTest {
    private static Stream<Arguments> argumentsForMatch() {
        return Stream.of(
                Arguments.of("", true, ""),
                Arguments.of("/", true, "/"),
                Arguments.of("/endpoint", true, "/endpoint"),
                Arguments.of("/endpoint/", true, "/endpoint/"),
                Arguments.of("/endpoint/car/make/model/\\d\\d\\d\\?color=.+&finish=.+", true, "/endpoint/car/make/model/123?color=blue&finish=matte")
        );
    }

    @Test
    void uriParsers() {
        List<Parser> expected = List.of();
        RequestEndpoint endpoint = new TestRequestEndpoint("", expected);

        Assertions.assertEquals(expected, endpoint.uriParsers());
    }

    @Test
    void parsedUriDefinition() {
        String expected = "expected";
        RequestEndpoint endpoint = new TestRequestEndpoint(expected, List.of());

        Assertions.assertEquals(expected, endpoint.parsedUriDefinition());
    }

    @ParameterizedTest
    @MethodSource("argumentsForMatch")
    void match(String parsedUriDefinition, boolean shouldMatch, String matching) {
        RequestEndpoint endpoint = new TestRequestEndpoint(parsedUriDefinition, List.of());

        Assertions.assertEquals(shouldMatch, endpoint.match(matching));
    }

    static class TestRequestEndpoint extends RequestEndpoint {
        public TestRequestEndpoint(String parsedUriDefinition, List<Parser> uriParsers) {
            super(parsedUriDefinition, uriParsers);
        }

        @Override
        public String uriDefinition() {
            return null;
        }
    }
}