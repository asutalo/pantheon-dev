package com.eu.atit.pantheon.file.endpoint;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

class FileEndpointTest {
    @Test
    void shouldExpandBaseUri() {
        String baseUri = "/someFile";
        FileEndpoint fileEndpoint = new FileEndpoint(baseUri, "/somePath", "someMimeType");
        String uriDefinition = fileEndpoint.uriDefinition();

        Assertions.assertTrue(uriDefinition.startsWith(baseUri));
        Assertions.assertEquals(FileEndpoint.filePathRegex, uriDefinition.replace(baseUri + "/", ""));
    }


    private static Stream<Arguments> argumentsForMatch() {
        return Stream.of(
                Arguments.of("", false, ""),
                Arguments.of("/", false, "/"),
                Arguments.of("/endpoint", false, "/endpoint"),
                Arguments.of("/endpoint/", false, "/endpoint/"),
                Arguments.of("/endpoint/car/make/model/\\d\\d\\d\\?color=.+&finish=.+", false, "/endpoint/car/make/model/123?color=blue&finish=matte"),
                Arguments.of("/", false, "/some/folder/file"),
                Arguments.of("/", false, "/some/.folder/file.dat"),
                Arguments.of("/", false, "/some/.folder"),
                Arguments.of("/", false, "/some/folder/file.txt?"),
                Arguments.of("/", false, "/some/folder/file.txt/"),
                Arguments.of("/", true, "/some/folder/file.txt"),
                Arguments.of("/endpoint", true, "/endpoint/some/folder/file.txt")
        );
    }

    @ParameterizedTest
    @MethodSource("argumentsForMatch")
    void shouldMatch(String uriDefinition, boolean shouldMatch, String matching) {
        FileEndpoint testEndpoint = new FileEndpoint(uriDefinition, "/somePath", "someMimeType");

        Assertions.assertEquals(shouldMatch, testEndpoint.match(matching));

    }
}
