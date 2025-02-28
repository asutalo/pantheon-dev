package com.eu.atit.mysql.service;

import java.lang.reflect.Field;
import java.util.function.BiConsumer;

class SpecificFieldValueOverride<T> implements BiConsumer<T, T> {
    final FieldAccessor fieldAccessor;

    SpecificFieldValueOverride(Field fieldToSet) {
        this.fieldAccessor = new FieldAccessor(fieldToSet);
    }

    @Override
    public void accept(T targetObject, T sourceObject) {
        Object valueToSet = fieldAccessor.getValueFrom(sourceObject);
        fieldAccessor.setValueOn(targetObject, valueToSet);
    }

    // Encapsulates both getter and setter
    static class FieldAccessor {
        private final FieldValueSetter setter;
        private final FieldValueGetter getter;

        FieldAccessor(Field field) {
            this.getter = new FieldValueGetter(field);
            this.setter = new FieldValueSetter(field);
        }

        Object getValueFrom(Object sourceObject) {
            return getter.apply(sourceObject);
        }

        void setValueOn(Object targetObject, Object value) {
            setter.accept(targetObject, value);
        }
    }
}
