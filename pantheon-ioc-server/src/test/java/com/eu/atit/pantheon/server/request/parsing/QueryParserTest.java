package com.eu.atit.pantheon.server.request.parsing;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

class QueryParserTest {
    @Test
    void accept_shouldParseAndAddStringToMap() {
        String key = "someKey";
        String val = "someVal";
        String toParse = "toParse=" + val;
        Map<String, Object> actualMap = new HashMap<>();
        Map<String, Object> expectedMap = Map.of(key, val);

        new QueryParser(key).accept(actualMap, toParse);

        Assertions.assertEquals(expectedMap, actualMap);
    }
}