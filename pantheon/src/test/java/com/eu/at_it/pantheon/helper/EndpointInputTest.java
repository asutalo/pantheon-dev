package com.eu.at_it.pantheon.helper;

import com.sun.net.httpserver.Headers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Map;

class EndpointInputTest {
    private static final Map<String, Object> SOME_PARAMS = Map.of("some", "params");
    private static final Map<String, Object> SOME_BODY = Map.of("some", "body");
    private static final Headers SOME_HEADERS = new Headers();
    private static final Object[] SOME_ARGS = new Object[]{SOME_PARAMS, SOME_BODY, SOME_HEADERS};

    private static final EndpointInput ENDPOINT_INPUT = new EndpointInput(SOME_ARGS);

    @Test
    void getUriParams() {
        Assertions.assertEquals(SOME_PARAMS, ENDPOINT_INPUT.getUriParams());
    }

    @Test
    void getRequestBody() {
        Assertions.assertEquals(SOME_BODY, ENDPOINT_INPUT.getRequestBody());
    }

    @Test
    void getRequestHeaders() {
        Assertions.assertEquals(SOME_HEADERS, ENDPOINT_INPUT.getRequestHeaders());
    }

    @Test
    void hashCodeShouldBeSameForOneObject() {
        Assertions.assertEquals(ENDPOINT_INPUT.hashCode(), ENDPOINT_INPUT.hashCode());
    }
}