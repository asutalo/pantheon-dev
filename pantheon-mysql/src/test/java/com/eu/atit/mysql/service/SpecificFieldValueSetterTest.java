package com.eu.atit.mysql.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Map;

class SpecificFieldValueSetterTest extends DomainsTestBase {
    @Test
    void accept_shouldSetValueFromRow() {
        int expected = 42;
        String someTableName = "someTableName";
        Field testField = getField();
        SpecificFieldValueSetter<SimpleTestClass> specificFieldValueSetter = new SpecificFieldValueSetter<>(testField, someTableName);
        Map<String, Object> row = Map.of(getAlias(testField, someTableName), expected);

        SimpleTestClass simpleTestClass = new SimpleTestClass();

        specificFieldValueSetter.accept(simpleTestClass, row);

        Assertions.assertEquals(expected, simpleTestClass.getVal());
    }
}