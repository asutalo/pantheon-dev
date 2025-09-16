package com.eu.atit.pantheon.server.request;

import com.eu.atit.pantheon.server.response.TextResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class TextResponseTest {
    static final int SOME_STATUS_CODE = 1;
    static final String SOME_MESSAGE = "msg";

    @Test
    void getStatusCode() {
        Assertions.assertEquals(SOME_STATUS_CODE, new TextResponse(SOME_STATUS_CODE, SOME_MESSAGE).getStatusCode());
    }

    @Test
    void getBody() {
        Assertions.assertEquals(SOME_MESSAGE, new String(new TextResponse(SOME_STATUS_CODE, SOME_MESSAGE).getBody()));
    }

    @Test
    void getHeaders() {
        Map<String, List<String>> extraHeaders = Map.of("someKey", List.of("a", "b"));

        TextResponse response = new TextResponse(1, "someMsg", extraHeaders);

        Map<String, List<String>> expectedMap = new HashMap<>(extraHeaders);
        expectedMap.putAll(response.baseHeaders());
        Assertions.assertEquals(expectedMap, response.getHeaders());
    }
}