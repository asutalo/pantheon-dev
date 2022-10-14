package com.eu.at_it.pantheon.validation;

import com.eu.at_it.pantheon.helper.EndpointInput;
import com.eu.at_it.pantheon.server.endpoints.PantheonEndpoint;
import com.eu.at_it.pantheon.server.endpoints.Response;
import com.eu.at_it.pantheon.server.exceptions.UnauthorizedException;
import com.sun.net.httpserver.Headers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Proxy;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class EndpointRequestValidatorTest {
    private static final Map<String, Object> SOME_ARGS = Map.of();
    private static final Headers SOME_HEADERS = new Headers();
    @Mock
    private EndpointValidator mockEndpointValidator;
    private Map<String, EndpointValidator> endpointValidatorMap;

    @BeforeEach
    void setUp() {
        endpointValidatorMap = Map.of("get", mockEndpointValidator);
    }

    private PantheonEndpoint getProxy() {
        return (PantheonEndpoint) Proxy.newProxyInstance(PantheonEndpoint.class.getClassLoader(),
                new Class[]{PantheonEndpoint.class}, new EndpointRequestValidator(new TestImpl(), endpointValidatorMap));
    }

    @Test
    void proxy_shouldExecuteMethod_whenValidationPasses() {
        PantheonEndpoint proxy = getProxy();

        when(mockEndpointValidator.test(any())).thenReturn(true);

        proxy.get(SOME_ARGS, SOME_ARGS, SOME_HEADERS);

        verify(mockEndpointValidator).test(new EndpointInput(new Object[]{SOME_ARGS, SOME_ARGS, SOME_HEADERS}));
        verify(mockEndpointValidator).negate();
    }

    @Test
    void proxy_shouldExecuteMethod_whenNoValidationConfigured() {
        PantheonEndpoint proxy = getProxy();

        proxy.put(SOME_ARGS, SOME_ARGS, SOME_HEADERS);

        verify(mockEndpointValidator, never()).test(any());
        verify(mockEndpointValidator).negate();
    }

    @Test
    void proxy_shouldNotExecuteMethod_whenValidationFails() {
        PantheonEndpoint proxy = getProxy();

        when(mockEndpointValidator.test(any())).thenReturn(false);

        Assertions.assertThrows(UnauthorizedException.class, () -> proxy.get(SOME_ARGS, SOME_ARGS, SOME_HEADERS));

        verify(mockEndpointValidator, never()).negate();
    }

    class TestImpl implements PantheonEndpoint {
        @Override
        public Response head(Map<String, Object> uriParams, Map<String, Object> requestBody, Headers requestHeaders) {
            return null;
        }

        @Override
        public Response get(Map<String, Object> uriParams, Map<String, Object> requestBody, Headers requestHeaders) {
            mockEndpointValidator.negate();
            return null;
        }

        @Override
        public Response put(Map<String, Object> uriParams, Map<String, Object> requestBody, Headers requestHeaders) {
            mockEndpointValidator.negate();
            return null;
        }

        @Override
        public Response post(Map<String, Object> uriParams, Map<String, Object> requestBody, Headers requestHeaders) {
            return null;
        }

        @Override
        public Response patch(Map<String, Object> uriParams, Map<String, Object> requestBody, Headers requestHeaders) {
            return null;
        }

        @Override
        public Response delete(Map<String, Object> uriParams, Map<String, Object> requestBody, Headers requestHeaders) {
            return null;
        }
    }


}