package com.eu.atit.mysql.service;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

class SpecificNestedFieldValueSetter<T> {
    private final FieldValueSetter<T> fieldValueSetter;
    private final MySQLService<?> service;

    SpecificNestedFieldValueSetter(Field fieldToSet, MySQLService<?> service) {
        fieldValueSetter = new FieldValueSetter<>(fieldToSet);
        this.service = service;
    }

    public void accept(T setFieldOn, Map<String, Object> row, List<Class<?>> observedClasses) {
        if (!observedClasses.contains(fieldValueSetter.getField().getType())) {
            observedClasses.add(fieldValueSetter.getField().getType());
            fieldValueSetter.accept(setFieldOn, service.fullInstanceOfT(row, observedClasses));
        } else {
            fieldValueSetter.accept(setFieldOn, service.lazyInstanceOfT(row));
        }
    }
}
