package com.eu.at_it.pantheon.helper;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class PairTest {
    private static final String SOME_STRING = "someString";
    private static final Integer SOME_INT = 1;

    private final Pair<String, Integer> pair = new Pair<>(SOME_STRING, SOME_INT);

    @Test
    void left() {
        Assertions.assertEquals(SOME_STRING, pair.left());
    }

    @Test
    void right() {
        Assertions.assertEquals(SOME_INT, pair.right());
    }

    @Test
    void equals() {
        Pair<String, Integer> otherPair = new Pair<>(SOME_STRING, SOME_INT);

        Assertions.assertEquals(pair, otherPair);
    }

    @Test
    void hashcode() {
        Assertions.assertEquals(pair.hashCode(), pair.hashCode());
    }
}