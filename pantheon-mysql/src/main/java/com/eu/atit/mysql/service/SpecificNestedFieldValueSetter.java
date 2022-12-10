package com.eu.atit.mysql.service;

import com.eu.atit.pantheon.helper.Pair;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.function.BiConsumer;

class SpecificNestedFieldValueSetter<T> implements BiConsumer<T, Map<String, Object>> {
    private final FieldValueSetter<T> fieldValueSetter;
    private final MySQLService<?> service;

    SpecificNestedFieldValueSetter(Field fieldToSet, MySQLService<?> service) {
        fieldValueSetter = new FieldValueSetter<>(fieldToSet);
        this.service = service;
    }

    @Override
    public void accept(T setFieldOn, Map<String, Object> row) {
        fieldValueSetter.accept(setFieldOn, service.fullInstanceOfT(row));
    }
}
