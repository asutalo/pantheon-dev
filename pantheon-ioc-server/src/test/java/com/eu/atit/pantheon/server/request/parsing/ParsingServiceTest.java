package com.eu.atit.pantheon.server.request.parsing;

import com.eu.atit.pantheon.server.response.exception.BadRequestException;
import com.sun.net.httpserver.HttpExchange;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ParsingServiceTest {
    @Mock
    private Parser mockParser;

    @Mock
    private HttpExchange mockHttpExchange;

    private static Stream<Arguments> argumentsForGetUriParsers() {
        return Stream.of(
                Arguments.of("", List.of(new IgnoredParser())),
                Arguments.of("/", List.of()),
                Arguments.of("/endpoint", List.of(new IgnoredParser(), new IgnoredParser())),
                Arguments.of("/endpoint/", List.of(new IgnoredParser(), new IgnoredParser())),
                Arguments.of("/endpoint/(id=(\\?))", List.of(new IgnoredParser(), new IgnoredParser(), new PathParser("id"))),
                Arguments.of("/endpoint?bo=((\\d)\\?)", List.of(new IgnoredParser(), new IgnoredParser(), new QueryParser("bo"))),
                Arguments.of("/endpoint/car/make/model/(modelId=(\\d\\d\\d)\\?)\\?color=(.+)&finish=(.+)",
                        List.of(
                                new IgnoredParser(),
                                new IgnoredParser(),
                                new IgnoredParser(),
                                new IgnoredParser(),
                                new IgnoredParser(),
                                new PathParser("modelId"),
                                new QueryParser("color"),
                                new QueryParser("finish")
                        )),
                Arguments.of("/endpoint/car/make/model/(modelId=\\d\\d\\d)\\?color=(.+)",
                        List.of(
                                new IgnoredParser(),
                                new IgnoredParser(),
                                new IgnoredParser(),
                                new IgnoredParser(),
                                new IgnoredParser(),
                                new PathParser("modelId"),
                                new QueryParser("color")
                        )),
                Arguments.of("/endpoint/car/make/model/(modelId=\\d\\d\\d)",
                        List.of(
                                new IgnoredParser(),
                                new IgnoredParser(),
                                new IgnoredParser(),
                                new IgnoredParser(),
                                new IgnoredParser(),
                                new PathParser("modelId")
                        ))
        );
    }

    private static Stream<Arguments> argumentsForParseUriDefinition() {
        return Stream.of(
                Arguments.of("", "\\/"),
                Arguments.of("/", "\\/"),
                Arguments.of("\\/", "\\/"),
                Arguments.of("/endpoint", "/endpoint"),
                Arguments.of("/endpoint/", "/endpoint"),
                Arguments.of("/endpoint?bo=((\\d)\\?)", "/endpoint?bo=(\\d)\\?"),
                Arguments.of("/endpoint/(id=\\?)", "/endpoint/\\?"),
                Arguments.of("/endpoint/(id=(\\d)\\?)", "/endpoint/(\\d)\\?"),
                Arguments.of(
                        "/endpoint/car/make/model/(modelId=\\d\\d\\d)\\?color=(.+)&finish=(.+)",
                        "/endpoint/car/make/model/\\d\\d\\d\\?color=.+&finish=.+")
        );
    }

    private static Stream<Arguments> argumentsForRequestUri() {
        return Stream.of(
                Arguments.of("", 1),
                Arguments.of("/", 0),
                Arguments.of("/endpoint", 2),
                Arguments.of("/endpoint/", 2),
                Arguments.of("/endpoint/(id=\\?)", 3),
                Arguments.of("/endpoint/(id=(\\d)\\?)", 3),
                Arguments.of("/endpoint?bo=((\\d)\\?)", 3),
                Arguments.of("/endpoint/car/make/model/(modelId=\\d\\d\\d)\\?color=(.+)&finish=(.+)", 8)
        );
    }

    private static Stream<Arguments> argumentsForDecodeUri() {
        return Stream.of(
                Arguments.of("/uri/le", "/uri/le"),
                Arguments.of("/uri/le?asd=x", "/uri/le?asd=x"),
                Arguments.of("/uri?text=Hello+G%C3%BCnter", "/uri?text=Hello+Günter"),
                Arguments.of("/uri?text=Hello+G%C3%BCnter&day=blue", "/uri?text=Hello+Günter&day=blue")
        );
    }

    @Test
    void isSingleton() {
        assertEquals(ParsingService.getInstance(), ParsingService.getInstance());
    }

    @ParameterizedTest
    @MethodSource("argumentsForDecodeUri")
    void decodeUri(String uri, String expectedDecodedUri) {
        when(mockHttpExchange.getRequestURI()).thenReturn(URI.create(uri));
        assertEquals(expectedDecodedUri, ParsingService.getInstance().decodeUri(mockHttpExchange));
    }

    @Test
    void parseRequestBody_shouldParseValidJson() throws IOException {
        Map<String, Object> expected = Map.of("this", "is_valid");
        when(mockHttpExchange.getRequestBody()).thenReturn(new ByteArrayInputStream("{\"this\": \"is_valid\"}".getBytes(StandardCharsets.UTF_8)));
        assertEquals(expected, ParsingService.getInstance().parseRequestBody(mockHttpExchange));
    }

    @Test
    void parseRequestBody_shouldParseEmptyJson() throws IOException {
        Map<String, Object> expected = Map.of();
        when(mockHttpExchange.getRequestBody()).thenReturn(new ByteArrayInputStream("{}".getBytes(StandardCharsets.UTF_8)));
        assertEquals(expected, ParsingService.getInstance().parseRequestBody(mockHttpExchange));
    }

    @Test
    void parseRequestBody_shouldParseEmptyBody() throws IOException {
        Map<String, Object> expected = Map.of();
        when(mockHttpExchange.getRequestBody()).thenReturn(new ByteArrayInputStream("".getBytes(StandardCharsets.UTF_8)));
        assertEquals(expected, ParsingService.getInstance().parseRequestBody(mockHttpExchange));
    }

    @Test
    void parseRequestBody_shouldThrowBadRequestExceptionForInvalidJson() {
        when(mockHttpExchange.getRequestBody()).thenReturn(new ByteArrayInputStream("not valid".getBytes(StandardCharsets.UTF_8)));
        assertThrows(BadRequestException.class, () -> ParsingService.getInstance().parseRequestBody(mockHttpExchange));
    }

    @ParameterizedTest
    @MethodSource("argumentsForGetUriParsers")
    void getUriParsers(String uriDefinition, List<Parser> expectedParsers) {
        ParsingService parsingService = ParsingService.getInstance();
        List<Parser> actualParsers = parsingService.getUriParsers(uriDefinition);

        assertEquals(expectedParsers.size(), actualParsers.size());
        for (int i = 0; i < expectedParsers.size(); i++) {
            assertEquals(expectedParsers.get(i).getClass(), actualParsers.get(i).getClass());

            if (expectedParsers.get(i) instanceof QueryParser) {
                assertEquals(((QueryParser) expectedParsers.get(i)).getKey(), ((QueryParser) actualParsers.get(i)).getKey());
            } else if (expectedParsers.get(i) instanceof PathParser) {
                assertEquals(((PathParser) expectedParsers.get(i)).getKey(), ((PathParser) actualParsers.get(i)).getKey());
            }
        }
    }

    @ParameterizedTest
    @MethodSource("argumentsForParseUriDefinition")
    void parseUriDefinition(String uriDefinition, String expectedParsedUriDefinition) {
        ParsingService parsingService = ParsingService.getInstance();
        assertEquals(expectedParsedUriDefinition, parsingService.parseUriDefinition(uriDefinition));
    }

    @ParameterizedTest
    @MethodSource("argumentsForRequestUri")
    void parseRequestUri(String uriString, int expectedParsers) {
        List<Parser> parsers = new ArrayList<>();

        for (int i = 0; i < expectedParsers; i++) {
            parsers.add(mockParser);
        }

        ParsingService parsingService = ParsingService.getInstance();
        parsingService.parseRequestUri(uriString, parsers);

        verify(mockParser, times(expectedParsers)).accept(any(), any());
    }
}