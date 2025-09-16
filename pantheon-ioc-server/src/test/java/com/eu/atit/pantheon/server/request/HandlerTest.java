package com.eu.atit.pantheon.server.request;

import com.eu.atit.pantheon.server.endpoint.Endpoint;
import com.eu.atit.pantheon.server.endpoint.EndpointProcessor;
import com.eu.atit.pantheon.server.endpoint.Registry;
import com.eu.atit.pantheon.server.endpoints.Response;
import com.eu.atit.pantheon.server.request.parsing.ParsingService;
import com.eu.atit.pantheon.server.request.validation.Validator;
import com.eu.atit.pantheon.server.response.TextResponse;
import com.eu.atit.pantheon.server.response.exception.IocServerException;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import static com.eu.atit.pantheon.server.response.exception.InternalServerErrorException.INTERNAL_SERVER_ERROR_MESSAGE;
import static com.eu.atit.pantheon.server.response.exception.InternalServerErrorException.INTERNAL_SERVER_ERROR_STATUS;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HandlerTest {
    private static final String SOME_DECODED_URI = "/some/URI";
    private Handler handler;
    @Mock
    private Validator mockValidator;

    @Mock
    private HttpExchange mockHttpExchange;

    @Mock
    private OutputStream mockOutputStream;

    @Mock
    private ParsingService mockParsingService;

    @Mock
    private Registry mockRegistry;

    @Mock
    private Endpoint mockEndpoint;

    @Mock
    private EndpointProcessor mockEndpointProcessor;

    @BeforeEach
    void setUp() {
        handler = new Handler(mockValidator, mockParsingService, mockRegistry, mockEndpointProcessor);
    }

    @Test
    void shouldHandleAndRespondWithIocServerExceptions() throws IOException {
        String expectedMessage = "msg";
        int expectedStatus = 1;

        when(mockParsingService.decodeUri(mockHttpExchange)).thenReturn(SOME_DECODED_URI);
        when(mockHttpExchange.getResponseHeaders()).thenReturn(new Headers());
        when(mockHttpExchange.getResponseBody()).thenReturn(mockOutputStream);
        doThrow(new IocServerException(expectedStatus, expectedMessage)).when(mockValidator).validateUri(SOME_DECODED_URI);

        handler.handle(mockHttpExchange);

        verifyResponse(expectedStatus, expectedMessage);
    }

    @Test
    void shouldHandleUnexpectedErrorsAndRespondWithInternalServerError() throws IOException {
        when(mockHttpExchange.getResponseHeaders()).thenReturn(new Headers());
        when(mockHttpExchange.getResponseBody()).thenReturn(mockOutputStream);
        doThrow(NullPointerException.class).when(mockParsingService).decodeUri(mockHttpExchange);

        handler.handle(mockHttpExchange);

        verifyResponse(INTERNAL_SERVER_ERROR_STATUS, INTERNAL_SERVER_ERROR_MESSAGE);
    }

    @Test
    void shouldFindAndExecuteExistingEndpoint() throws IOException {
        int expectedStatus = 3;
        String expectedMessage = "msg";
        Map<String, List<String>> additionalHeaders = Map.of("h1", List.of("asd"));

        Headers mockHeaders = mock(Headers.class);

        when(mockParsingService.decodeUri(mockHttpExchange)).thenReturn(SOME_DECODED_URI);
        when(mockHttpExchange.getResponseHeaders()).thenReturn(mockHeaders);
        when(mockHttpExchange.getResponseBody()).thenReturn(mockOutputStream);
        when(mockRegistry.getEndpoint(SOME_DECODED_URI)).thenReturn(mockEndpoint);

        when(mockEndpointProcessor.process(mockEndpoint, mockParsingService, SOME_DECODED_URI, mockHttpExchange)).thenReturn(getResponse(expectedStatus, expectedMessage, additionalHeaders));

        handler.handle(mockHttpExchange);

        verifyResponse(expectedStatus, expectedMessage);
        verify(mockHeaders).putAll(additionalHeaders);
    }

    private Response getResponse(int expectedStatus, String expectedMessage, Map<String, List<String>> additionalHeaders) {
        return new Response() {
            @Override
            public int getStatusCode() {
                return expectedStatus;
            }

            @Override
            public byte[] getBody() {
                return expectedMessage.getBytes(StandardCharsets.UTF_8);
            }

            @Override
            public Map<String, List<String>> getHeaders() {
                return additionalHeaders;
            }
        };
    }

    private void verifyResponse(int responseCode, String responseBody) throws IOException {
        verify(mockHttpExchange).sendResponseHeaders(responseCode, responseBody.length());
        verify(mockOutputStream).write(responseBody.getBytes(StandardCharsets.UTF_8));
        verify(mockOutputStream).flush();
        verify(mockOutputStream).close();
    }
}