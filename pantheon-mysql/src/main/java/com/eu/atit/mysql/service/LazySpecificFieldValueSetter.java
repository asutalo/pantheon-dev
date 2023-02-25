package com.eu.atit.mysql.service;

import java.lang.reflect.Field;
import java.util.Map;

public class LazySpecificFieldValueSetter<T> extends SpecificFieldValueSetter<T>{
    private final MySQLService<?> service;
    private final FieldValueSetter<T> fieldValueSetter;

    LazySpecificFieldValueSetter(Field fieldToSet, String tableName, MySQLService<?> service) {
        super(fieldToSet, tableName);
        fieldValueSetter = new FieldValueSetter<>(fieldToSet);
        this.service = service;
    }

    @Override
    public void accept(T setFieldOn, Map<String, Object> row) {
        fieldValueSetter.accept(setFieldOn, service.lazyInstanceOfT(row));
    }
}