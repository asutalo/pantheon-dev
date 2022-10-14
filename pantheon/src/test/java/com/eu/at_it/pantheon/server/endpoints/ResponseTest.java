package com.eu.at_it.pantheon.server.endpoints;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

class ResponseTest {
    @Test
    void getHeaders_defaultsToEmptyMap() {
        Map<String, Object> expected = Map.of();

        Map<String, List<String>> actual = new Response() {
            @Override
            public int getStatusCode() {
                return 0;
            }

            @Override
            public String getMessage() {
                return null;
            }
        }.getHeaders();

        Assertions.assertEquals(expected, actual);
    }
}