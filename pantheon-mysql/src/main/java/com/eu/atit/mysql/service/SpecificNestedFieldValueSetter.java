package com.eu.atit.mysql.service;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

class SpecificNestedFieldValueSetter<T> {
    final Instantiator<?> instantiator;
    final SpecificFieldValueSetter<?> primaryKeyValueSetter;
    private final FieldValueSetter<T> fieldValueSetter;
    private final ResultSetToInstance<?> resultSetToInstance;

    SpecificNestedFieldValueSetter(Field fieldToSet, ResultSetToInstance<?> resultSetToInstance, Instantiator<?> instantiator, SpecificFieldValueSetter<?> primaryKeyValueSetter) {
        fieldValueSetter = new FieldValueSetter<>(fieldToSet);
        this.resultSetToInstance = resultSetToInstance;
        this.instantiator = instantiator;
        this.primaryKeyValueSetter = primaryKeyValueSetter;
    }

    public void accept(T setFieldOn, Map<String, Object> row, List<Class<?>> observedClasses) {
        if (!observedClasses.contains(fieldValueSetter.getField().getType())) {
            observedClasses.add(fieldValueSetter.getField().getType());

            fieldValueSetter.accept(setFieldOn, resultSetToInstance.get(row, observedClasses));
        } else {
            Object instance = instantiator.get();

            primaryKeyValueSetter.accept(instance, row);
            fieldValueSetter.accept(setFieldOn, instance);
        }
    }
}
