package com.eu.atit.pantheon.server.request.parsing;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

class IgnoredParserTest {
    @Test
    void accept_doesNothing() {
        Map<String, Object> expectedMap = Map.of("1", 2);
        Map<String, Object> actualMap = new HashMap<>(expectedMap);

        new IgnoredParser().accept(actualMap, "some string");

        Assertions.assertEquals(expectedMap, actualMap);
    }
}