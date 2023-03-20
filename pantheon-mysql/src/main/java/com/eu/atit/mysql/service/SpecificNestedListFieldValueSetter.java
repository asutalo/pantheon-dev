package com.eu.atit.mysql.service;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

class SpecificNestedListFieldValueSetter<T> extends SpecificNestedFieldValueSetter<T> {
    private final FieldValueSetter<T> fieldValueSetter;
    private final ResultSetToInstance<?> resultSetToInstance;
    private final Type actualTypeArgument;

    SpecificNestedListFieldValueSetter(Field fieldToSet, ResultSetToInstance<?> resultSetToInstance, Instantiator<?> instantiator, SpecificFieldValueSetter<?> primaryKeyValueSetter) {
        super(fieldToSet, resultSetToInstance, instantiator, primaryKeyValueSetter);
        fieldValueSetter = new FieldValueSetter<>(fieldToSet);
        this.resultSetToInstance = resultSetToInstance;
        Type genericType = fieldToSet.getGenericType();
        actualTypeArgument = ((ParameterizedType) genericType).getActualTypeArguments()[0];
    }


    @Override
    public void accept(T setFieldOn, Map<String, Object> row, List<Class<?>> observedClasses) {
        if (!observedClasses.contains((Class<?>) actualTypeArgument)) {
            observedClasses.add((Class<?>) actualTypeArgument);
            fieldValueSetter.accept(setFieldOn, new ArrayList<>(List.of(resultSetToInstance.get(row, observedClasses))));

        } else {
            T instance = (T) instantiator.get();

            primaryKeyValueSetter.accept(instance, row);
            fieldValueSetter.accept(setFieldOn, new ArrayList<>(List.of(instance)));
        }
    }
}
