package com.eu.atit.mysql.service;

import java.lang.reflect.Field;
import java.util.function.BiConsumer;

class SpecificFieldValueOverride<T> implements BiConsumer<T, T> {
    final FieldValueSetter<T> fieldValueSetter;
    final FieldValueGetter fieldValueGetter;

    SpecificFieldValueOverride(Field fieldToSet) {
        fieldValueGetter = new FieldValueGetter(fieldToSet);
        fieldValueSetter = new FieldValueSetter<>(fieldToSet);
    }

    @Override
    public void accept(T setFieldOn, T getValueFrom) {
        fieldValueSetter.accept(setFieldOn, fieldValueGetter.apply(getValueFrom));
    }
}
