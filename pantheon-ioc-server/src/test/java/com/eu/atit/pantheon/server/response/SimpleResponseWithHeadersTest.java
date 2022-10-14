package com.eu.atit.pantheon.server.response;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

class SimpleResponseWithHeadersTest {
    @Test
    void getHeaders() {
        Map<String, List<String>> expectedMap = Map.of("someKey", List.of("a", "b"));
        SimpleResponseWithHeaders response = new SimpleResponseWithHeaders(1, "someMsg", expectedMap);

        Assertions.assertEquals(expectedMap, response.getHeaders());
    }
}