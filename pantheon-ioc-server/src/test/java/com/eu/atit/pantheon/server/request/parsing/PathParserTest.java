package com.eu.atit.pantheon.server.request.parsing;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

class PathParserTest {
    @Test
    void accept_shouldAddStringToMap() {
        String key = "someKey";
        String val = "someVal";
        Map<String, Object> actualMap = new HashMap<>();
        Map<String, Object> expectedMap = Map.of(key, val);

        new PathParser(key).accept(actualMap, val);

        Assertions.assertEquals(expectedMap, actualMap);
    }
}