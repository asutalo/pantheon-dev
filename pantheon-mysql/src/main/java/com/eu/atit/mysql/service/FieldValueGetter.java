package com.eu.atit.mysql.service;

import java.lang.reflect.Field;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Function to set a value into an instance of an object
 */
class FieldValueGetter<T> implements Function<T, Object> {
    final Field field;

    /**
     * @param field reflection of the variable that is to be used to set the value in the object
     */
    FieldValueGetter(Field field) {
        this.field = field;
    }

    @Override
    public Object apply(T t) {
        try {
            return field.get(t);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Failed to get value from field", e);
        }
    }
}
