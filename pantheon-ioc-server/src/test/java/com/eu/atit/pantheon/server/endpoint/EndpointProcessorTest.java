package com.eu.atit.pantheon.server.endpoint;

import com.eu.atit.pantheon.server.request.parsing.Parser;
import com.eu.atit.pantheon.server.request.parsing.ParsingService;
import com.eu.atit.pantheon.server.response.exception.NotFoundException;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EndpointProcessorTest {
    private static final String SOME_DECODED_URI = "someDecodedUri";
    private static final Map<String, Object> SOME_URI_PARAMS = Map.of();
    private static final Map<String, Object> SOME_REQUEST_BODY = Map.of();
    private static final List<Parser> SOME_PARSERS = List.of();
    private static final Headers SOME_HEADERS = new Headers();

    @Mock
    private HttpExchange mockHttpExchange;

    @Mock
    private ParsingService mockParsingService;

    private static Stream<Arguments> endpointActionsArguments() {
        Endpoint mockEndpoint = mock(Endpoint.class);
        return Stream.of(
                Arguments.of(mockEndpoint, "get", (Runnable) () -> verify(mockEndpoint).get(SOME_URI_PARAMS, SOME_REQUEST_BODY, SOME_HEADERS)),
                Arguments.of(mockEndpoint, "HeAd", (Runnable) () -> verify(mockEndpoint).head(SOME_URI_PARAMS, SOME_REQUEST_BODY, SOME_HEADERS)),
                Arguments.of(mockEndpoint, "DELETE", (Runnable) () -> verify(mockEndpoint).delete(SOME_URI_PARAMS, SOME_REQUEST_BODY, SOME_HEADERS)),
                Arguments.of(mockEndpoint, "puT", (Runnable) () -> verify(mockEndpoint).put(SOME_URI_PARAMS, SOME_REQUEST_BODY, SOME_HEADERS)),
                Arguments.of(mockEndpoint, "patch", (Runnable) () -> verify(mockEndpoint).patch(SOME_URI_PARAMS, SOME_REQUEST_BODY, SOME_HEADERS)),
                Arguments.of(mockEndpoint, "POst", (Runnable) () -> verify(mockEndpoint).get(SOME_URI_PARAMS, SOME_REQUEST_BODY, SOME_HEADERS))
        );
    }

    @ParameterizedTest
    @MethodSource("endpointActionsArguments")
    void process_shouldExecuteGetEndpointAction(Endpoint mockEndpoint, String method, Runnable assertion) throws IOException {
        when(mockEndpoint.uriParsers()).thenReturn(SOME_PARSERS);
        when(mockParsingService.parseRequestUri(SOME_DECODED_URI, SOME_PARSERS)).thenReturn(SOME_URI_PARAMS);
        when(mockParsingService.parseRequestBody(mockHttpExchange)).thenReturn(SOME_REQUEST_BODY);
        when(mockHttpExchange.getRequestMethod()).thenReturn(method);
        when(mockHttpExchange.getRequestHeaders()).thenReturn(SOME_HEADERS);

        new EndpointProcessor().process(mockEndpoint, mockParsingService, SOME_DECODED_URI, mockHttpExchange);

        assertion.run();
    }

    @Test
    void process_ShouldThrowNotFoundExceptionForUnsupportedMethod() throws IOException {
        Endpoint mockEndpoint = mock(Endpoint.class);
        when(mockParsingService.parseRequestUri(SOME_DECODED_URI, SOME_PARSERS)).thenReturn(SOME_URI_PARAMS);
        when(mockParsingService.parseRequestBody(mockHttpExchange)).thenReturn(SOME_REQUEST_BODY);
        when(mockHttpExchange.getRequestMethod()).thenReturn("unknown");

        Assertions.assertThrows(NotFoundException.class, () -> new EndpointProcessor().process(mockEndpoint, mockParsingService, SOME_DECODED_URI, mockHttpExchange));
    }
}