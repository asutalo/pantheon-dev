package com.eu.atit.mysql.service;

import java.lang.reflect.Field;
import java.util.Map;

public class LazyNestedObjectValueSetter<T> extends SpecificFieldValueSetter<T> {
    private final FieldValueSetter<T> fieldValueSetter;
    private final Instantiator<?> instantiator;
    private final SpecificFieldValueSetter<T> primaryKeyValueSetter;

    public LazyNestedObjectValueSetter(Field fieldToSet, String tableName, Instantiator<?> instantiator, SpecificFieldValueSetter<T> primaryKeyValueSetter) {
        super(fieldToSet, tableName);
        fieldValueSetter = new FieldValueSetter<>(fieldToSet);
        this.instantiator = instantiator;
        this.primaryKeyValueSetter = primaryKeyValueSetter;
    }

    @Override
    public void accept(Object setFieldOn, Map<String, Object> row) {
        T instance = (T) instantiator.get();

        primaryKeyValueSetter.accept(instance, row);//todo doublecheck it works properly for nested primary key
        fieldValueSetter.accept(setFieldOn, instance);
    }
}