package com.eu.atit.pantheon.file.endpoint;

import com.eu.atit.pantheon.file.response.FileResponse;
import com.sun.net.httpserver.Headers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static com.eu.atit.pantheon.file.endpoint.FileEndpoint.filePathKey;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

class SingleFileEndpointTest {
    private final String someUri = "/someFile";
    private final String somePath = "/somePath";
    private final String someMimeType = "someMimeType";
    private final SingleFileEndpoint singleFileEndpoint = new SingleFileEndpoint(someUri, somePath, someMimeType);

    @Test
    void shouldNotModifyUri() {
        Assertions.assertEquals(someUri, singleFileEndpoint.uriDefinition());
    }

    private static Stream<Arguments> argumentsForMatch() {
        return Stream.of(
                Arguments.of("", false, ""), //todo huh?
                Arguments.of("/", true, "/"),
                Arguments.of("/endpoint", true, "/endpoint"),
                Arguments.of("/endpoint/", false, "/endpoint/"), //todo huh?
                Arguments.of("/endpoint/car/make/model/\\d\\d\\d\\?color=.+&finish=.+", true, "/endpoint/car/make/model/123?color=blue&finish=matte"),
                Arguments.of("/", false, "/some/folder/file"),
                Arguments.of("/", false, "/some/.folder/file.dat"),
                Arguments.of("/", false, "/some/.folder"),
                Arguments.of("/", false, "/some/folder/file.txt?"),
                Arguments.of("/", false, "/some/folder/file.txt/"),
                Arguments.of("/", false, "/some/folder/file.txt"),
                Arguments.of("/endpoint", false, "/endpoint/some/folder/file.txt")
        );
    }

    @ParameterizedTest
    @MethodSource("argumentsForMatch")
    void shouldMatch(String uriDefinition, boolean shouldMatch, String matching) {
        SingleFileEndpoint testEndpoint = new SingleFileEndpoint(uriDefinition, "/somePath", "someMimeType");

        Assertions.assertEquals(shouldMatch, testEndpoint.match(matching));
    }



    @Test
    void shouldRespondWithExpectedFileAndMimeType() throws IOException {
        SingleFileEndpoint spy = spy(singleFileEndpoint);
        byte[] expectedContent = "someString".getBytes(StandardCharsets.UTF_8);
        doReturn(expectedContent).when(spy).readFile(Path.of(somePath));

        FileResponse fileResponse = spy.get(Map.of(filePathKey, "somePathToSomewhere", "otherKey", "otherVal"), Map.of(), new Headers());
        Assertions.assertEquals(expectedContent, fileResponse.getBody());
        Assertions.assertEquals(List.of(someMimeType), fileResponse.getHeaders().get("Content-Type"));
    }
}