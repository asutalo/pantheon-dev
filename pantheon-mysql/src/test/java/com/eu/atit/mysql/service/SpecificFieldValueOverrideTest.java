package com.eu.atit.mysql.service;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

class SpecificFieldValueOverrideTest {

    private SpecificFieldValueOverride<TestObject> specificFieldValueOverride;

    @BeforeEach
    void setUp() throws NoSuchFieldException {
        Field field = TestObject.class.getDeclaredField("field");
        specificFieldValueOverride = new SpecificFieldValueOverride<>(field);
    }

    @Test
    void accept_shouldCopyValueFromSourceToTarget() {
        // Arrange
        TestObject target = new TestObject(null);
        TestObject source = new TestObject("Value");

        // Act
        specificFieldValueOverride.accept(target, source);

        // Assert
        assertEquals("Value", target.getField());
    }

    // Helper class for simulation
    static class TestObject {
        String field;

        public TestObject(String field) {
            this.field = field;
        }

        public String getField() {
            return field;
        }
    }
}