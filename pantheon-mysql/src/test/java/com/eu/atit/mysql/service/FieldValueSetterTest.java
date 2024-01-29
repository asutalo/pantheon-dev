package com.eu.atit.mysql.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

class FieldValueSetterTest extends DomainsTestBase {

    @Test
    void accept_shouldUpdateFieldValue() {
        int expected = 2;
        SimpleTestClass simpleTestClass = new SimpleTestClass();
        Field testField = getField();

        new FieldValueSetter(testField).accept(simpleTestClass, expected);
        int actual = simpleTestClass.getVal();

        Assertions.assertNotEquals(START_VALUE, actual);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void accept_shouldThrowExceptionWhenValueCannotBeSet() {
        SimpleTestClass simpleTestClass = new SimpleTestClass();
        Field testField = getField();

        Assertions.assertThrows(RuntimeException.class, () -> new FieldValueSetter(testField).accept(simpleTestClass, "notApplicable"));
    }
}