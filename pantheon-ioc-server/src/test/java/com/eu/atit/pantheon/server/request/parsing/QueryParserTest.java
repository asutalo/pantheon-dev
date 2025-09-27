package com.eu.atit.pantheon.server.request.parsing;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

class QueryParserTest {
    @Test
    void accept_shouldParseAndAddStringToMap() {
        String key = "someKey";
        String val = "someVal";
        String toParse = "toParse=" + val;
        Map<String, Object> actualMap = new HashMap<>();
        Map<String, Object> expectedMap = Map.of(key, val);

        Iterator<String> testIterator = List.of(toParse).iterator();
        new QueryParser(key).accept(actualMap, testIterator);

        Assertions.assertEquals(expectedMap, actualMap);
        Assertions.assertFalse(testIterator.hasNext());
    }
}