package com.eu.atit.mysql.service;

import static java.util.Collections.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

class SpecificListFieldValueOverrideTest {

    private SpecificListFieldValueOverride<Object> specificListFieldValueOverride;

    @BeforeEach
    void setUp() throws NoSuchFieldException {
        specificListFieldValueOverride = new SpecificListFieldValueOverride<>(TestObject.class.getDeclaredField("field"));
    }

    @Test
    void accept_shouldMergeListFromTwoObjects() {
        // Arrange
        TestObject target = new TestObject(List.of("A", "B"));
        TestObject source = new TestObject(List.of("B", "C"));

        // Act
        specificListFieldValueOverride.accept(target, source);

        // Assert
        assertEquals(List.of("A", "B", "C"), target.getField());
    }


    @Test
    void accept_shouldMergeListFromTwoObjectsWhenSourceListEmpty() {
        // Arrange
        List<Object> targetList = List.of("A", "B");
        List<Object> sourceList = emptyList();
        TestObject target = new TestObject(targetList);
        TestObject source = new TestObject(sourceList);

        // Act
        specificListFieldValueOverride.accept(target, source);

        // Assert
        assertEquals(targetList, target.getField());
    }

    @Test
    void accept_shouldMergeListFromTwoObjectsWhenTargetListEmpty() {
        // Arrange
        List<Object> targetList = emptyList();
        List<Object> sourceList = List.of("B", "C");
        TestObject target = new TestObject(targetList);
        TestObject source = new TestObject(sourceList);

        // Act
        specificListFieldValueOverride.accept(target, source);

        // Assert
        assertEquals(sourceList, target.getField());
    }

    @Test
    void accept_shouldMergeListFromTwoObjectsWhenBothListsEmpty() {
        // Arrange
        List<Object> targetList = emptyList();
        List<Object> sourceList = emptyList();
        TestObject target = new TestObject(targetList);
        TestObject source = new TestObject(sourceList);

        // Act
        specificListFieldValueOverride.accept(target, source);

        // Assert
        assertEquals(emptyList(), targetList);
    }

    // Helper class for simulation
    static class TestObject {

        List<Object> field;

        public TestObject(List<Object> field) {
            this.field = field;
        }

        public List<Object> getField() {
            return field;
        }
    }
}