package com.eu.atit.mysql.service;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static com.eu.atit.mysql.service.FieldValueGetter.FAILED_TO_GET_VALUE_FROM_FIELD;
import static org.junit.jupiter.api.Assertions.*;

class FieldValueGetterTest {

    @Test
    void shouldGetValueFromPublicFieldSuccessfully() throws NoSuchFieldException {
        class TestClass {
            public String field = "testValue";
        }
        assertGetValue(TestClass.class, new TestClass());
    }

    @Test
    void shouldGetValueFromPrivateFieldSuccessfully() throws NoSuchFieldException {
        class TestClass {
            private String field = "testValue";
        }

        assertGetValue(TestClass.class, new TestClass());
    }

    @Test
    void shouldThrowRuntimeExceptionWhenFieldIsInaccessible() throws NoSuchFieldException {
        class TestClass {
            private final String field = "testValue";
        }

        Field field = TestClass.class.getDeclaredField("field");
        FieldValueGetter fieldValueGetter = new FieldValueGetter(field);

        field.setAccessible(false);
        RuntimeException exception = assertThrows(RuntimeException.class, () -> fieldValueGetter.apply(new TestClass()));
        assertTrue(exception.getMessage().contains(FAILED_TO_GET_VALUE_FROM_FIELD));
    }

    private void assertGetValue(Class<?> clazz, Object testObject) throws NoSuchFieldException {
        Field field = clazz.getDeclaredField("field");
        FieldValueGetter fieldValueGetter = new FieldValueGetter(field);

        Object result = fieldValueGetter.apply(testObject);

        assertEquals("testValue", result);
    }
}