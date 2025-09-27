package com.eu.atit.pantheon.server.request.parsing;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


class PathSplatParserTest {
    @Test
    void accept_shouldAddStringToMap() {
        String key = "someKey";
        String val = "someVal";
        String val1 = "someVal1";
        String val2 = "someVal2";
        List<String> testVals = List.of(val, val1, val2);
        Map<String, Object> actualMap = new HashMap<>();
        Map<String, Object> expectedMap = Map.of(key, String.join("/", testVals));

        Iterator<String> testIterator = testVals.iterator();
        new PathSplatParser(key).accept(actualMap, testIterator);

        Assertions.assertEquals(expectedMap, actualMap);
        Assertions.assertFalse(testIterator.hasNext());
    }
}