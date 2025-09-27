package com.eu.atit.pantheon.server.request.parsing;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

class IgnoredParserTest {
    @Test
    void accept_doesNothingButIterate() {
        Map<String, Object> expectedMap = Map.of("1", 2);
        Map<String, Object> actualMap = new HashMap<>(expectedMap);

        Iterator<String> testIterator = List.of("some string").iterator();
        new IgnoredParser().accept(actualMap, testIterator);

        Assertions.assertEquals(expectedMap, actualMap);
        Assertions.assertFalse(testIterator.hasNext());
    }
}