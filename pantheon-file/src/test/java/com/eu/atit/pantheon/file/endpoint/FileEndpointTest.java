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

class FileEndpointTest {
    private final String someBaseUri = "/someFile";
    private final String someBasePath = "/somePath";
    private final String someMimeType = "someMimeType";
    private final FileEndpoint fileEndpoint = new FileEndpoint(someBaseUri, someBasePath, someMimeType);

    @Test
    void shouldExpandBaseUri() {
        String uriDefinition = fileEndpoint.uriDefinition();

        Assertions.assertTrue(uriDefinition.startsWith(someBaseUri));
        Assertions.assertEquals(FileEndpoint.filePathRegex, uriDefinition.replace(someBaseUri + "/", ""));
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

    @Test
    void shouldRespondWithExpectedFileAndMimeType() throws IOException {
        FileEndpoint spy = spy(fileEndpoint);
        String expectedPathSuffix = "/someSuffix";
        Path somePath = Path.of(someBasePath, expectedPathSuffix);
        byte[] expectedContent = "someString".getBytes(StandardCharsets.UTF_8);
        Map<String, Object> uriParams = Map.of(filePathKey, expectedPathSuffix, "otherKey", "otherVal");

        doReturn(somePath).when(spy).resolve(uriParams, filePathKey);
        doReturn(expectedContent).when(spy).readFile(somePath);

        FileResponse fileResponse = spy.get(uriParams, Map.of(), new Headers());
        Assertions.assertEquals(expectedContent, fileResponse.getBody());
        Assertions.assertEquals(List.of(someMimeType), fileResponse.getHeaders().get("Content-Type"));
    }
}
