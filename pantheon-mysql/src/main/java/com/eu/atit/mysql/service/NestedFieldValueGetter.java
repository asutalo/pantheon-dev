package com.eu.atit.mysql.service;

import java.lang.reflect.Field;

/**
 * Function to set a value into an instance of an object
 */
class NestedFieldValueGetter extends FieldValueGetter {

    private final FieldValueGetter nestedPrimaryKeyFieldValueGetter;

    NestedFieldValueGetter(Field field, FieldValueGetter nestedPrimaryKeyFieldValueGetter) {
        super(field);
        this.nestedPrimaryKeyFieldValueGetter = nestedPrimaryKeyFieldValueGetter;
    }

    @Override
    public Object apply(Object t) {
        try {
            return nestedPrimaryKeyFieldValueGetter.apply(field.get(t));
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Failed to get value from field", e);
        }
    }

}
