package com.eu.atit.mysql.service;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

class SpecificNestedListFieldValueSetter<T> extends SpecificNestedFieldValueSetter<T>{
    private final FieldValueSetter<T> fieldValueSetter;
    private final MySQLService<?> service;
    private final Type actualTypeArgument;

    SpecificNestedListFieldValueSetter(Field fieldToSet, MySQLService<?> service) {
        super(fieldToSet, service);
        fieldValueSetter = new FieldValueSetter<>(fieldToSet);
        this.service = service;
        Type genericType = fieldToSet.getGenericType();
        actualTypeArgument = ((ParameterizedType) genericType).getActualTypeArguments()[0];
    }

    @Override
    public void accept(T setFieldOn, Map<String, Object> row, List<Class<?>> observedClasses) {
        if (!observedClasses.contains((Class<?>) actualTypeArgument)){
            observedClasses.add((Class<?>) actualTypeArgument);
            fieldValueSetter.accept(setFieldOn, new ArrayList<>(List.of(service.fullInstanceOfT(row, observedClasses))));
        } else {
            fieldValueSetter.accept(setFieldOn, new ArrayList<>(List.of(service.primaryInstanceOfT(row))));
        }
    }
}
