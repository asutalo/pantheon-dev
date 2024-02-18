package com.eu.atit.mysql.service;

import com.eu.atit.mysql.service.annotations.MySqlField;

import java.lang.reflect.Field;
import java.util.function.BiConsumer;

/**
 * Function to set a value into an instance of an object
 */
public class FieldValueSetter implements BiConsumer<Object, Object> {
    private final Field field;
    private final String fieldName;

    /**
     * @param field reflection of the variable that is to be used to set the value in the object
     */
    FieldValueSetter(Field field) {
        fieldName = field.getName();
        this.field = field;
    }

    @Override
    public void accept(Object setOn, Object value) {
        try {
            field.set(setOn, value);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set value on field", e);
        }
    }

    Field getField() {
        return field;
    }

    @Override
    public String toString() {
        return "FieldValueSetter{" +
                "field=" + field +
                '}';
    }

    public String getFieldName() {
        return fieldName;
    }
}
