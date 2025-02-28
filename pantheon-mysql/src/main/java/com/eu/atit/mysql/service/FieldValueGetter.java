package com.eu.atit.mysql.service;

import java.lang.reflect.Field;

/**
 * Function to set a value into an instance of an object
 */
public class FieldValueGetter {
    static final String FAILED_TO_GET_VALUE_FROM_FIELD = "Failed to get value from field";
    final Field field;

    /**
     * @param field reflection of the variable that is to be used to set the value in the object
     */
    FieldValueGetter(Field field) {
        field.setAccessible(true);
        this.field = field;
    }

    public Object apply(Object t) {
        try {
            return field.get(t);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(FAILED_TO_GET_VALUE_FROM_FIELD, e);
        }
    }
}
