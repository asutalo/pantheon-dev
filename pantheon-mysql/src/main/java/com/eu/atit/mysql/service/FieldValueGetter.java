package com.eu.atit.mysql.service;

import java.lang.reflect.Field;

/**
 * Function to set a value into an instance of an object
 */
public class FieldValueGetter {
    final Field field;

    /**
     * @param field reflection of the variable that is to be used to set the value in the object
     */
    FieldValueGetter(Field field) {
        this.field = field;
    }

    public Object apply(Object t) {
        try {
            return field.get(t);
        } catch (Exception e) {
            throw new RuntimeException("Failed to get value from field", e);
        }
    }
}
